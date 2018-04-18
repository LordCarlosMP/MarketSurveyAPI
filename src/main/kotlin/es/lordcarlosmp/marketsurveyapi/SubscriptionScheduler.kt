package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.DAILY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.MONTHLY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.WEEKLY
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency.YEARLY
import es.lordcarlosmp.marketsurveyapi.database.getMatchingMarketSurveys
import es.lordcarlosmp.marketsurveyapi.database.getSubscribers
import it.sauronsoftware.cron4j.Scheduler
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * This function send the market surveys to all the
 * subscribers stored in the database.
 */
fun sendMarketSurveysToSubscribers(frequency: SubscriptionFrequency) {
	for (subscriber in getSubscribers(frequency)) {
		subscriber.notifySubscriber(subscriber.marketSurveyRequest.getMatchingMarketSurveys())
	}
}

/**
 * Schedules the notification-sending tasks.
 */
fun scheduleNotificationTasks() {
	val s = Scheduler()
	s.schedule("0 0 * * *", { sendMarketSurveysToSubscribers(DAILY) })
	s.schedule("0 0 * * 0", { sendMarketSurveysToSubscribers(WEEKLY) })
	s.schedule("0 0 1 * *", { sendMarketSurveysToSubscribers(MONTHLY) })
	s.schedule("0 0 1 1 *", { sendMarketSurveysToSubscribers(YEARLY) })
	s.start()
}

/**
 * This class is used to have a "main" in the API,
 * currently, almost everything is initialized "on demand"
 * except for the scheduleNotificationTasks() function,
 * that needs to run even if no HTTP method is called.
 */
@WebListener
class StartupListener : ServletContextListener {

	override fun contextInitialized(event: ServletContextEvent) {

		print("""


			Starting Market Survey Api.

		""")

		scheduleNotificationTasks()

		print("""

			Schedule notification task is done.


		""")
	}

	override fun contextDestroyed(event: ServletContextEvent) {
	}
}