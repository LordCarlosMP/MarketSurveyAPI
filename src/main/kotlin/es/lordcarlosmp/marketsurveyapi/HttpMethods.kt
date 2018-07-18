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
import org.springframework.web.bind.annotation.RestController

//todo: mejorar este comentario
/**
 * The class where market survey requests are made.
 */
@RestController
@RequestMapping("marketsurveys")
class MarketSurveyController {
	
	@Autowired
	lateinit var marketSurveyRepo: MarketSurveyRepository
	
	/**
	 * This function stores the MarketSurvey json in
	 * the database.
	 *
	 * @return If the MarketSurvey was created.
	 *
	 * @param ms The MarketSurvey.
	 */
	//todo: los responses
	@RequestMapping(method = [RequestMethod.POST])
	fun createMarketSurvey(@RequestBody ms: MarketSurvey): ResponseEntity<Any> {
		marketSurveyRepo.create(ms)
		return ResponseEntity(HttpStatus.CREATED)
	}
	
	/**
	 * @return A json array with all the market surveys that
	 * fit the request.
	 *
	 * @param msr The Request.
	 */
	@RequestMapping(method = [RequestMethod.PUT])
	fun getMatchingMarketSurveys(@RequestBody msr: Request) = marketSurveyRepo.findAllMatching(msr)

	/**
	 * @return all the market surveys in the database.
	 */
	@RequestMapping(method = [RequestMethod.GET])
	fun getAllMarketSurveys() = marketSurveyRepo.findAll()
	
	/**
	 * @return The MarketSurvey whose id is [id], null if there's no MarketSurvey with that id.
	 */
	@RequestMapping(value = ["/id"], method = [RequestMethod.GET])
	fun getMarketSurvey(id: String) = marketSurveyRepo.findById(id)
	
	/**
	 * Deletes the MarketSurvey whose id is [id]
	 */
	@RequestMapping(method = [RequestMethod.DELETE])
	fun deleteMarketSurvey(id: String) = marketSurveyRepo.delete(id)
}

/**
 * The controller where market survey subscriptions are submitted.
 */
@RestController
@RequestMapping("subscriptions")
class SubscriptionController {
	
	@Autowired
	lateinit var subscriptionRepo: SubscriptionRepository
	
	/**
	 * This function stores the Subscription in
	 * the database.
	 *
	 * @return If the Subscription was created.
	 *
	 * @param subscription The Subscription.
	 */
	@RequestMapping(method = [RequestMethod.POST])
	fun createMarketSurveywithSubscription(@RequestBody subscription: Subscription): ResponseEntity<Any> {
		subscriptionRepo.create(subscription)
		return ResponseEntity(HttpStatus.CREATED)
	}
	
	/**
	 * @return All Subscription in the Database.
	 */
	@RequestMapping(method = [RequestMethod.GET])
	fun getAllMarketSurveySubscribers() = subscriptionRepo.findAll()
	
	/**
	 * @return all market surveys in the whose frecuency is [frequency]
	 */
	@RequestMapping(method = [RequestMethod.PUT])
	fun getAllMarketSurveySubscribersInFrecuency(frequency: SubscriptionFrequency) = subscriptionRepo.findAllInFrecuency(frequency)
	
	/**
	 * @return The subscription with the given ID, null if there's no MarketSurvey with that id.
	 */
	@RequestMapping(value = "/id", method = [RequestMethod.GET])
	fun getSubscriberById(id: String) = subscriptionRepo.findById(id)
	
	/**
	 * This function deletes the subscription with [id].
	 *
	 * //todo: explicar return.
	 */
	@RequestMapping(method = [RequestMethod.DELETE])
	fun deleteById(id: String) = subscriptionRepo.delete(id)
}

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	SpringApplication.run(Application::class.java, *args)
	SubscriptionScheduler()
}