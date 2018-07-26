package es.lordcarlosmp.marketsurveyapi

import com.fasterxml.jackson.databind.ObjectMapper
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.WEEKLY
import org.bson.types.ObjectId
import java.util.*

val mapper = ObjectMapper()

val marketSurveyType = mapper.typeFactory.constructType(MarketSurvey::class.java)

val marketSurveyListType = mapper.typeFactory.constructCollectionType(List::class.java, MarketSurvey::class.java)

val subscriptionType = mapper.typeFactory.constructType(Subscription::class.java)

val subscriptionListType = mapper.typeFactory.constructCollectionType(List::class.java, Subscription::class.java)

val exampleSurvey = MarketSurvey(
		ObjectId(),
		10,
		Date().apply { time = 0L },
		"ES",
		"Surveys Corporation SA",
		Target(
				listOf(Gender.MALE, Gender.FEMALE, Gender.ASEXUAL),
				Range(30, 80),
				Range(10000, 50000)
		)
)

val exampleRequest = Request(
		10,
		null,
		listOf("ES", "ARG"),
		Target(
				listOf(Gender.MALE, Gender.FEMALE),
				Range(1, 100),
				Range(0, 1000000)
		)
)

val exampleSurveyList = listOf(
		exampleSurvey,
		MarketSurvey(
				ObjectId(),
				10,
				Date().apply { time = 0L },
				"ARG",
				"Market Surveys Inc",
				Target(
						listOf(Gender.MALE, Gender.FEMALE),
						Range(18, 50),
						Range(10000, 50000)
				)
		)
)

val exampleSubscription = Subscription(
		"abcabc123123",
		exampleRequest,
		WEEKLY,
		SendData(
				mail = "amail@mail.com",
				phone = "+34 123 123 123"
		)
)

val exampleSubscriptionList = listOf(
		exampleSubscription,
		Subscription(
				"xyzxyz987987",
				exampleRequest,
				WEEKLY,
				SendData(
						postalDirection = "Av. de Concha Espina, 1, 28036 Madrid"
				)
		)
)