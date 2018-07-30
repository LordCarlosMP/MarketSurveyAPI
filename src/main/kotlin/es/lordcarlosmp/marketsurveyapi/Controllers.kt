package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.database.MarketSurveyRepository
import es.lordcarlosmp.marketsurveyapi.database.SubscriptionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * The controller responsible of creating, reading and deleting MarketSurveys.
 */
@RestController
@RequestMapping("marketsurveys")
class MarketSurveyController {
	
	@Autowired
	lateinit var marketSurveyRepo: MarketSurveyRepository
	
	/**
	 * Stores the MarketSurvey in the database.
	 */
	@RequestMapping(method = [RequestMethod.POST])
	fun createMarketSurvey(@RequestBody survey: MarketSurvey): ResponseEntity<Any> {
		@Suppress("SENSELESS_COMPARISON")
		if (survey.id == null) {
			survey.id = createDatabaseId()
		}
		marketSurveyRepo.create(survey)
		return ResponseEntity(HttpStatus.CREATED)
	}
	
	/**
	 * @return All the market surveys who fit in the [request].
	 */
	@RequestMapping(method = [RequestMethod.PUT])
	fun getMatchingMarketSurveys(@RequestBody request: Request) = marketSurveyRepo.findAllMatching(request)
	
	/**
	 * @return all the market surveys in the database.
	 */
	@RequestMapping(method = [RequestMethod.GET])
	fun getAllMarketSurveys() = marketSurveyRepo.findAll()
	
	/**
	 * @return The MarketSurvey whose id is [id], null if there's no MarketSurvey with that id.
	 */
	@RequestMapping(value = ["id"], method = [RequestMethod.GET])
	fun getMarketSurveyById(@RequestParam id: String): Any? {
		//todo: fix this
		if (id == "atajo") return marketSurveyRepo.findById("5b5d076e2881ac78446b8293") ?: "ABC"
		
		return marketSurveyRepo.findById(id)
	}
	
	/**
	 * Deletes the MarketSurvey whose id is [id]
	 */
	@RequestMapping(method = [RequestMethod.DELETE])
	fun deleteMarketSurvey(@RequestParam id: String) = marketSurveyRepo.delete(id)
}

/**
 * The controller responsible of creating, reading and deleting Subscriptions.
 */
@RestController
@RequestMapping("subscriptions")
class SubscriptionController {
	
	@Autowired
	lateinit var subscriptionRepo: SubscriptionRepository
	
	/**
	 * Stores the [subscription] in the database.
	 */
	@RequestMapping(method = [RequestMethod.POST])
	fun createMarketSurveywithSubscription(@RequestBody subscription: Subscription): ResponseEntity<Any> {
		@Suppress("SENSELESS_COMPARISON")
		if (subscription.id == null) {
			subscription.id = createDatabaseId()
		}
		subscriptionRepo.create(subscription)
		return ResponseEntity(HttpStatus.CREATED)
	}
	
	/**
	 * @return All Subscriptions in the Database.
	 */
	@RequestMapping(method = [RequestMethod.GET])
	fun getAllMarketSurveySubscribers() = subscriptionRepo.findAll()
	
	/**
	 * @return all market surveys in the database whose frecuency is [frequency]
	 */
	@RequestMapping(value = ["frequency"], method = [RequestMethod.GET])
	fun getAllMarketSurveySubscribersInFrecuency(@RequestParam frequency: SubscriptionFrequency) = subscriptionRepo.findAllInFrecuency(frequency)
	
	/**
	 * @return The subscription with the given ataja[id], null if there's no MarketSurvey with that id.
	 */
	@RequestMapping(value = ["id"], method = [RequestMethod.GET])
	fun getSubscriptionById(id: String) = subscriptionRepo.findById(id)
	
	/**
	 * This function deletes the subscription whose id is [id].
	 *
	 * //todo: explain return.
	 */
	@RequestMapping(method = [RequestMethod.DELETE])
	fun deleteById(id: String) = subscriptionRepo.delete(id)
}

@SpringBootApplication
class Application

/**
 * Starts the Application and the SubscriptionScheduler.
 */
fun main(args: Array<String>) {
	val wac = SpringApplication.run(Application::class.java, *args)
	SubscriptionScheduler(wac.getBean(SubscriptionRepository::class.java), wac.getBean(MarketSurveyRepository::class.java), Notifier)
}