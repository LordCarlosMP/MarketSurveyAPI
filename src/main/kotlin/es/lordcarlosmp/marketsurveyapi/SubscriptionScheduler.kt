package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.DAILY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.MONTHLY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.WEEKLY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.YEARLY
import es.lordcarlosmp.marketsurveyapi.database.MarketSurveyRepository
import es.lordcarlosmp.marketsurveyapi.database.SubscriptionRepository
import it.sauronsoftware.cron4j.Scheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * Schedules the notification-sending tasks.
 */
@Service
class SubscriptionScheduler {

	//todo: documentation
	@Autowired
	private lateinit var subscriptionsRepository: SubscriptionRepository
	
	//todo: documentation
	@Autowired
	private lateinit var marketSurveySubscription: MarketSurveyRepository
	
	init {
		val s = Scheduler()
		s.schedule("0 0 * * *") { sendMarketSurveysToSubscribers(DAILY) }
		s.schedule("0 0 * * 0") { sendMarketSurveysToSubscribers(WEEKLY) }
		s.schedule("0 0 1 * *") { sendMarketSurveysToSubscribers(MONTHLY) }
		s.schedule("0 0 1 1 *") { sendMarketSurveysToSubscribers(YEARLY) }
		s.start()
	}
	
	/**
	 * This function send the market surveys to all the
	 * subscribers stored in the database.
	 */
	private fun sendMarketSurveysToSubscribers(frequency: SubscriptionFrequency) {
		for (subscriber in subscriptionsRepository.findAllInFrecuency(frequency)) {
			subscriber.notifySubscriber(marketSurveySubscription.readMatchingRequest(subscriber.request))
		}
	}
	
}