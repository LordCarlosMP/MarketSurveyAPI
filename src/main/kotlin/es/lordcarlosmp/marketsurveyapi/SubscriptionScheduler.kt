package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.DAILY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.MONTHLY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.WEEKLY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.YEARLY
import es.lordcarlosmp.marketsurveyapi.database.MarketSurveyRepository
import es.lordcarlosmp.marketsurveyapi.database.SubscriptionRepository
import it.sauronsoftware.cron4j.Scheduler

/**
 * Schedules the notification-sending tasks.
 */
class SubscriptionScheduler(
		private val subscriptionsRepository: SubscriptionRepository,
		private val marketSurveySubscription: MarketSurveyRepository,
		private val notifier: Notifier) {
	
	
	
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
			notifier.notifySubscriber(subscriber, marketSurveySubscription.findAllMatching(subscriber.request))
		}
	}
}

/**
 * Notifies subscribers about available market surveys.
 */
object Notifier {
	@Suppress("UNUSED_PARAMETER")
			/**
	 * Sends a messago to the subscribers whoose subscription is
	 * @param subscription
	 * with the content of
	 * @param marketSurveys
	 *
	 * is not implemented because providing market survey results
	 * fall outside the scope of this project.
	 */
	fun notifySubscriber(subscription: Subscription, marketSurveys: List<MarketSurvey>): Nothing = TODO("Not Implemented")
}