package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.WEEKLY
import es.lordcarlosmp.marketsurveyapi.database.SubscriptionRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

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
		
		mockMvc.perform(post("/subscriptions")
				.contentType(APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(exampleSubscription)))
				.andExpect(status().isCreated)
		
		verify(repo).create(exampleSubscription)
	}
	
	@Test
	fun getMarketSurveySubscribers() {
		
		`when`(repo.findAll()).thenReturn(exampleSubscriptionList)
		
		val content = mockMvc.perform(get("/subscriptions"))
				.andExpect(status().isOk)
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andReturn().response.contentAsString
		
		assertEquals(exampleSubscriptionList, mapper.readValue(content, subscriptionListType))
	}
	
	@Test
	fun getAllMarketSurveySubscribersInFrequency() {
		
		val frequency = WEEKLY
		
		`when`(repo.findAllInFrecuency(frequency)).thenReturn(exampleSubscriptionList)
		
		val content = mockMvc.perform(get("/subscriptions/frequency")
				.param("frequency", "$frequency"))
				.andExpect(status().isOk)
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andReturn().response.contentAsString
		
		
		assertEquals(exampleSubscriptionList, mapper.readValue(content, subscriptionListType))
	}
	
	@Test
	fun testDeleteById() {
		val id = "abcabc123123"
		
		mockMvc.perform(delete("/subscriptions")
				.param("id", id))
				.andExpect(status().isOk)
		
		verify(repo).delete(id)
	}
	
	@Test
	fun testGetSubscriptionById() {
		val id = "abcabc123123"
		
		`when`(repo.findById(id)).thenReturn(exampleSubscription)
		
		val content = mockMvc.perform(get("/subscriptions/id")
				.param("id", id))
				.andExpect(status().isOk)
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andReturn().response.contentAsString
		
		assertEquals(exampleSubscription, mapper.readValue(content, subscriptionType))
	}
}