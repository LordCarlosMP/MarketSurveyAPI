package es.lordcarlosmp.marketsurveyapi

import com.fasterxml.jackson.databind.ObjectMapper
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.WEEKLY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.YEARLY
import java.util.*

val mapper = ObjectMapper()

val marketSurveyType = mapper.typeFactory.constructType(MarketSurvey::class.java)

val marketSurveyListType = mapper.typeFactory.constructCollectionType(List::class.java, MarketSurvey::class.java)

val subscriptionType = mapper.typeFactory.constructType(Subscription::class.java)

val subscriptionListType = mapper.typeFactory.constructCollectionType(List::class.java, Subscription::class.java)

val exampleSurvey = MarketSurvey(
		subject = 10,
		date = Date().apply { time = 0L },
		country = "ES",
		provider = "Surveys Corporation SA",
		target = Target(
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
				subject = 10,
				date = Date().apply { time = 0L },
				country = "ARG",
				provider = "Market Surveys Inc",
				target = Target(
						listOf(Gender.MALE, Gender.FEMALE),
						Range(18, 50),
						Range(10000, 50000)
				)
		)
)

val exampleSubscription = Subscription(
		request = exampleRequest,
		frequency = WEEKLY,
		deliveryData = DeliveryData(
				mail = "amail@mail.com",
				phone = "+34 123 123 123"
		)
)

val exampleSubscriptionList = listOf(
		exampleSubscription,
		Subscription(
				request = exampleRequest,
				frequency = YEARLY,
				deliveryData = DeliveryData(
						postalDirection = "Av. de Concha Espina, 1, 28036 Madrid"
				)
		)
)