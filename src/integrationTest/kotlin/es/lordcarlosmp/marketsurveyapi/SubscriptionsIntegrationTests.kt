package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.WEEKLY
import es.lordcarlosmp.marketsurveyapi.database.SubscriptionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringJUnit4ClassRunner::class)
@AutoConfigureMockMvc
@SpringBootTest
class SubscriptionControllerTest {
	
	@Autowired
	lateinit var repo: SubscriptionRepository
	
	@Autowired
	lateinit var mockMvc: MockMvc
	
	@Test
	fun testCreateMarketSurveySubscription() {
		
		mockMvc.perform(post("/subscriptions")
				.contentType(APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(exampleSubscription)))
				.andExpect(status().isCreated)
		
		assertEquals(exampleSubscription, repo.findById(exampleSubscription.id))
	}
	
	@Test
	fun testGetAllSubscribers() {
		
		for (subscription in exampleSubscriptionList) repo.create(subscription)
		val content = mockMvc.perform(get("/subscriptions"))
				.andExpect(status().isOk)
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andReturn().response.contentAsString
		
		assertEquals(exampleSubscriptionList, mapper.readValue(content, subscriptionListType))
	}
	
	@Test
	fun testGetSubscriptionById() {
		
		repo.create(exampleSubscription)
		val content = mockMvc.perform(get("/subscriptions/id")
				.param("id", exampleSubscription.id))
				.andExpect(status().isOk)
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andReturn().response.contentAsString
		
		assertEquals(exampleSubscription, mapper.readValue(content, subscriptionType))
	}
	
	@Test
	fun getAllMarketSurveySubscribersInFrequency() {
		
		for (subscription in exampleSubscriptionList) repo.create(subscription)
		val frequency = WEEKLY
		val content = mockMvc.perform(get("/subscriptions/frequency")
				.param("frequency", "$frequency"))
				.andExpect(status().isOk)
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andReturn().response.contentAsString
		
		assertEquals(listOf(exampleSubscription), mapper.readValue(content, subscriptionListType))
	}
	
	@Test
	fun testDeleteById() {
		
		repo.create(exampleSubscription)
		mockMvc.perform(delete("/subscriptions")
				.param("id", exampleSubscription.id))
				.andExpect(status().isOk)
		assertNull(repo.findById(exampleSubscription.id))
	}
	
	@Before
	fun cleanDb() {
		repo.findAll().forEach { repo.delete(it.id) }
	}
}