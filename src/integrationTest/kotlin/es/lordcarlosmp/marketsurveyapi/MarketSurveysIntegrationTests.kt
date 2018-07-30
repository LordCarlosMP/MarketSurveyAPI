package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.Gender.ASEXUAL
import es.lordcarlosmp.marketsurveyapi.Gender.FEMALE
import es.lordcarlosmp.marketsurveyapi.Gender.MALE
import es.lordcarlosmp.marketsurveyapi.database.MarketSurveyRepository
import org.bson.types.ObjectId
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.text.SimpleDateFormat
import java.util.*

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class MarketSurveysIntegrationTest {
	
	@Autowired
	lateinit var repo: MarketSurveyRepository
	
	@Autowired
	lateinit var mockMvc: MockMvc
	
	@Test
	fun testCreateMarketSurvey() {
		mockMvc.perform(post("/marketsurveys")
				.contentType(APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(exampleSurvey)))
				.andExpect(status().isCreated)
		
		assertEquals(exampleSurvey, repo.findById(exampleSurvey.id))
	}
	
	@Test
	fun testGetMarketSurveyById() {
		
		repo.create(exampleSurvey)
		
		val content = mockMvc.perform(get("/marketsurveys/id")
				.param("id", exampleSurvey.id))
				.andExpect(status().isOk).andReturn().response.contentAsString
		
		assertEquals(exampleSurvey, mapper.readValue(content, marketSurveyType))
	}
	
	@Test
	fun testGetAllMarketSurveys() {
		for (survey in exampleSurveyList) repo.create(survey)
		val content = mockMvc.perform(get("/marketsurveys"))
				.andExpect(status().isOk).andReturn().response.contentAsString
		
		assertEquals(exampleSurveyList, mapper.readValue(content, marketSurveyListType))
	}
	
	@Test
	fun testDeleteMarketSurvey() {
		
		mockMvc.perform(delete("/marketsurveys")
				.param("id", exampleSurvey.id)
				.content(mapper.writeValueAsString(exampleSurvey)))
				.andExpect(status().isOk)
		
		assertNull(repo.findById(exampleSurvey.id))
	}
	
	/**
	 * The way this test works is by introducing 2 MarketSurveys in the database,
	 * 1 will fit the Request and the other one won't, the survey who will always pass is
	 * fittingSurvey, it will remain unmodified, and the one who won't pass them
	 * will be created as a copy of notFittingSurvey but with the
	 * necessary modifications to prevent it from passing the filter.
	 */
	@Test
	fun testFindMatchingMarketSurveys() {
		
		fun createSurveys(vararg surveys: MarketSurvey) {
			for (ms in surveys) repo.create(ms)
		}
		
		/**
		 * Searchs with [mockMvc] the tests matching [request].
		 *
		 * It also verifies that the content returned is [APPLICATION_JSON_UTF8]
		 * and the status is Ok.
		 *
		 * @return the survey returned, if the amount is more than one, the test
		 * will fail because this will mean that both of the surveys passed the filter.
		 */
		fun search(request: Request): MarketSurvey {
			val content = mockMvc.perform(put("/marketsurveys")
					.contentType(APPLICATION_JSON_UTF8)
					.content(mapper.writeValueAsString(request)))
					.andExpect(status().isOk)
					.andExpect(content().contentType(APPLICATION_JSON_UTF8))
					.andReturn().response.contentAsString
			
			val list: List<MarketSurvey> = mapper.readValue(content, marketSurveyListType)
			assertEquals(1, list.size)
			return list.first()
		}
		
		/**
		 * Creates a copy of the receiver but with a different id,
		 * This is necessary for storing copies in the database.
		 */
		fun MarketSurvey.changeId() = copy(id = ObjectId().toHexString())
		
		fun dateFromString(date: String) = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(date)
		
		val date = dateFromString("July 2, 2018")
		
		val filterPassingTarget = Target(
				genders = listOf(FEMALE, MALE),
				age = Range(1, 100),
				income = Range(10000, 50000)
		)
		
		val fittingSurvey = MarketSurvey(
				subject = 10,
				date = date,
				country = "ES",
				provider = "A Provider",
				target = filterPassingTarget
		)
		
		val notFittingSurvey = fittingSurvey.changeId()
		
		//test subject filter
		createSurveys(fittingSurvey, notFittingSurvey.copy(subject = 11))
		var surveyReturned = search(Request(subject = 10))
		assertEquals(fittingSurvey, surveyReturned)
		
		//test date filter
		createSurveys(fittingSurvey, notFittingSurvey.copy(date = dateFromString("July 0, 2018")))
		surveyReturned = search(Request(subject = 10, date = dateFromString("July 1, 2018")))
		assertEquals(fittingSurvey, surveyReturned)
		
		//test country filter
		createSurveys(fittingSurvey, notFittingSurvey.copy(country = "CHL"))
		surveyReturned = search(Request(subject = 10, countries = listOf("ES")))
		assertEquals(fittingSurvey, surveyReturned)
		
		//test genders filter
		createSurveys(fittingSurvey, notFittingSurvey.copy(target = Target(genders = listOf(MALE, ASEXUAL, FEMALE))))
		surveyReturned = search(Request(subject = 10, target = Target(genders = listOf(MALE, FEMALE))))
		assertEquals(fittingSurvey, surveyReturned)
		
		//test age filter
		createSurveys(fittingSurvey, notFittingSurvey.copy(target = Target(age = Range(90, 101))))
		surveyReturned = search(Request(subject = 10, target = Target(age = Range(1, 100))))
		assertEquals(fittingSurvey, surveyReturned)
		
		//test income filter
		createSurveys(fittingSurvey, notFittingSurvey.copy(target = Target(income = Range(0, 70000))))
		surveyReturned = search(Request(subject = 10, target = Target(income = Range(0, 50000))))
		assertEquals(fittingSurvey, surveyReturned)
	}
	
	@Before
	fun cleanDb() {
		repo.findAll().forEach { repo.delete(it.id) }
	}
}