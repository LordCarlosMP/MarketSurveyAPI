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
	 * @return A json array with all the market surveys that
	 * fit the request.
	 *
	 * @param msr The Request.
	 */
	@RequestMapping(method = [RequestMethod.PUT])
	fun getMarketSurveys(@RequestBody msr: Request): List<MarketSurvey> {
		return marketSurveyRepo.readMatchingRequest(msr)
	}
	
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
}

/**
 * The class where market survey subscriptions are submitted.
 */
@RestController
@RequestMapping("subscriptions")
class SubscriptionController(val subscriptionRepo: SubscriptionRepository) {
	
	/**
	 * This function stores the Subscription json in
	 * the database.
	 *Int
	 * @return If the Subscription was created.
	 *
	 * @param mss The Subscription.
	 */
	@RequestMapping(method = [RequestMethod.PUT])
	fun createMarketSurveySubscription(@RequestBody mss: Subscription): ResponseEntity<Any> {
		subscriptionRepo.create(mss)
		return ResponseEntity(HttpStatus.CREATED)
	}
	
	/**
	 * This is just a function to verify that
	 *
	 * @See create Subscription(String) works.
	 *
	 * SubscriptionController are automatically
	 * sended in SubscriptionScheduler.json@RequestBody
	 *
	 * @See scheduleNotificationTasks()
	 *
	 * @return All SubscriptionController.
	 */
	@RequestMapping(method = [RequestMethod.GET])
	fun getMarketSurveySubscribers() = subscriptionRepo.findAll()
}

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	SpringApplication.run(Application::class.java, *args)
	SubscriptionScheduler()
}