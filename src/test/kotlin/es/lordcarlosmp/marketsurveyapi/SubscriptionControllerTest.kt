package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.database.MarketSurveyRepository
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

class SubscriptionControllerTest {
	@InjectMocks
	lateinit var marketSurveyController: MarketSurveyController
	
	@Mock
	lateinit var repo: MarketSurveyRepository
	
	@Autowired
	lateinit var mockMvc: MockMvc
	
	@Test
	fun createMarketSurveySubscription() {
	
	}
	
	@Test
	fun getMarketSurveySubscribers() {
	}
}