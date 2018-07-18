package es.lordcarlosmp.marketsurveyapi

import com.fasterxml.jackson.databind.ObjectMapper
import es.lordcarlosmp.marketsurveyapi.Gender.FEMALE
import es.lordcarlosmp.marketsurveyapi.Gender.MALE
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.WEEKLY
import es.lordcarlosmp.marketsurveyapi.SubscriptionType.FTP
import es.lordcarlosmp.marketsurveyapi.SubscriptionType.MAIL
import es.lordcarlosmp.marketsurveyapi.database.SubscriptionRepository
import es.lordcarlosmp.marketsurveyapi.database.formatToyyyymmdd
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.isEmptyOrNullString
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
class SubscriptionControllerTest {
	
	@InjectMocks
	lateinit var subscriptionController: SubscriptionController
	
	@Mock
	lateinit var repo: SubscriptionRepository
	
	lateinit var mockMvc: MockMvc
	
	@Before
	fun initMocks() {
		MockitoAnnotations.initMocks(this)
		this.mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build()
	}
	
	@Test
	fun createMarketSurveySubscription() {
		val subscription = Subscription(
				null,
				Request(1, null, listOf("ARG", "ES"), Target(
						listOf(MALE, FEMALE),
						Range(1, 10000),
						Range(1, 50000)
				)),
				listOf(FTP, MAIL),
				WEEKLY,
				SendData(mail = "amail@mail.com"))
		
		mockMvc.perform(post("/subscriptions")
				.contentType(APPLICATION_JSON_UTF8)
				.content(ObjectMapper().writeValueAsString(subscription)))
	}
	
	@Test
	fun getMarketSurveySubscribers() {
		val subscription = Subscription(
				null,
				Request(1, Date(), listOf("ES", "ARG"), Target(
						listOf(MALE, FEMALE),
						Range(18, 45),
						Range(10000, 50000)
				)),
				listOf(FTP, MAIL),
				WEEKLY,
				SendData(mail = "amail@mail.com"))
		
		val list = listOf(subscription, subscription, subscription)
		
		
		
		`when`(repo.findAll()).thenReturn(list)
		
		mockMvc.perform(get("/subscriptions"))
				.andExpect(status().isOk)
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize<Any>(3)))
				.andExpect(jsonPath("$[0].id", isEmptyOrNullString()))
				.andExpect(jsonPath("$[0].request.subject", `is`(1)))
				.andExpect(jsonPath("$[0].request.date", `is`(subscription.request.date!!.formatToyyyymmdd())))
				.andExpect(jsonPath("$[0].request.countries", hasSize<Any>(2)))
				.andExpect(jsonPath("$[0].request.target.age.start", `is`(18)))
				.andExpect(jsonPath("$[0].request.target.age.endInclusive", `is`(45)))
				.andExpect(jsonPath("$[0].request.countries[0]", `is`("ES")))
				.andExpect(jsonPath("$[0].type", hasSize<String>(2)))
				.andExpect(jsonPath("$[0].frequency", `is`("WEEKLY")))
				.andExpect(jsonPath("$[0].sendData.mail", `is`("amail@mail.com")))
		
	}
}