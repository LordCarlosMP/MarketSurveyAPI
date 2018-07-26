package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.Gender.ASEXUAL
import es.lordcarlosmp.marketsurveyapi.Gender.FEMALE
import es.lordcarlosmp.marketsurveyapi.Gender.MALE
import es.lordcarlosmp.marketsurveyapi.database.MarketSurveyRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mongodb.morphia.Datastore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.lang.Thread.sleep
import java.util.*
import kotlin.collections.ArrayList

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class Tsaf {
	
	@Autowired
	private lateinit var datastore: Datastore
	
	@Autowired
	lateinit var repo: MarketSurveyRepository
	
	@Autowired
	lateinit var mockMvc: MockMvc
	
	@Test
	fun cre() {
//		repo.create(exampleSurvey.copy(id = null))
//		repo.delete("5b5909cd0abd4b7f978e98de")
		repo.findAll().forEach { datastore.delete(it).apply { println(this.n) } }
//		datastore.save()
	}
	
	@Test
	fun testCreateMarketSurvey() {
		mockMvc.perform(post("/marketsurveys")
				.contentType(APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(exampleSurvey)))
				.andExpect(status().isCreated)
		
		assertEquals(exampleSurvey, repo.findById(exampleSurvey.id!!))
	}
	
	@Test
	fun testFindMatchingMarketSurveys() {
		
		val surveysInTheDatabase = ArrayList<MarketSurvey>()
		
		fun createSurveys(vararg surveys: MarketSurvey) {
			for (ms in surveys) {
				repo.create(ms)
				surveysInTheDatabase.add(ms)
			}
		}
		
		fun clear() {
//			for (s in surveysInTheDatabase) repo.delete(s.id!!)
			cleanDb()
			surveysInTheDatabase.clear()
		}
		
		fun search(request: Request) = mockMvc.perform(put("/marketsurveys")
				.contentType(APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk)
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andReturn().response.contentAsString
		
		val date = Date().apply { time = 0L }
		
		val baseTarget = Target(
				genders = listOf(FEMALE, MALE),
				age = Range(1, 100),
				income = Range(10000, 50000)
		)
		
		val baseSurvey = MarketSurvey(
				id = null,
				subject = 10,
				date = date,
				country = "ES",
				provider = "A Provider",
				target = baseTarget
		)
		
		
		
		baseSurvey.run {
			
			//test subject filter
			
			createSurveys(baseSurvey, copy(subject = 11))
			
			var content = search(Request(subject = 10))
			
			assertEquals(listOf(baseSurvey), mapper.readValue(content, marketSurveyListType))
			
			clear()
			//test date filter
			
			//test country filter
			createSurveys(baseSurvey, copy(country = "CHL"))
			
			content = search(Request(subject = 10, countries = listOf("ES")))
			
			assertEquals(listOf(baseSurvey), mapper.readValue(content, marketSurveyListType))
			
			clear()
			//test genders filter
			sleep(1000)
			val v = 0
			
			createSurveys(baseSurvey, copy(target = baseTarget.copy(genders = listOf(MALE, ASEXUAL, FEMALE))))
			content = search(Request(subject = 10))
			
			assertEquals(listOf(baseSurvey), mapper.readValue(content, marketSurveyListType))
			clear()
			//test age filter
			val vurr = 0
			
		}
	}
	
	@Test
	fun testDeleteMarketSurvey() {
		
		mockMvc.perform(delete("/marketsurveys")
				.param("id", exampleSurvey.id)
				.content(mapper.writeValueAsString(exampleSurvey)))
				.andExpect(status().isOk)
		
		assertNull(repo.findById(exampleSurvey.id!!))
	}
	
	@Test
	fun testMarketSurvey() {
		
		mockMvc.perform(delete("/marketsurveys")
				.param("id", exampleSurvey.id)
				.content(mapper.writeValueAsString(exampleSurvey)))
				.andExpect(status().isOk)
		
		assertNull(repo.findById(exampleSurvey.id!!))
	}
	
	//	@Before
	fun cleanDb() {
		repo.findAll().forEach { repo.delete(it.id!!) }
//		datastore.mongo.dropDatabase("testmarketsurveyapi")
	}
}

//val exampleSurvey = MarketSurvey(
//		System.currentTimeMillis().toString(),
//		10,
//		Date().apply { time = 0L },
//		"ES",
//		"Surveys Corporation SA",
//		Target(
//				listOf(Gender.MALE, Gender.FEMALE, Gender.ASEXUAL),
//				Range(30, 80),
//				Range(10000, 50000)
//		)
//)

//val request = Request(
//		subject = 10,
//		date = null,
//		countries = listOf("ES", "ARG"),
//		target = Target(
//				genders = listOf(MALE, FEMALE),
//				age = Range(1, 100),
//				income = Range(0, 1000000)
//		)
//)