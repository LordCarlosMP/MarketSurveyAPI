package es.lordcarlosm.marketsurveyapi

import es.lordcarlosm.marketsurveyapi.database.getAllSubscribers
import es.lordcarlosm.marketsurveyapi.database.getMatchingMarketSurveys
import es.lordcarlosm.marketsurveyapi.database.saveToDatabase
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * The class where market survey request are made.
 */
@Path("marketsurveys")
class MarketSurveys {

	/**
	 * @return A json array with all the market surveys that
	 * fit the request.
	 *
	 * @param data The MarketSurveyRequest json.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	fun getMarketSurveys(data: String) = data.deserializeMarketSurveyRequest().getMatchingMarketSurveys().serialize()

	/**
	 * This function stores the MarketSurvey json in
	 * the database.
	 *
	 * @return If the MarketSurvey was created.
	 *
	 * @param data The MarketSurvey's json.
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	fun createMarketSurvey(data: String): Int {
		data.deserializeMarketSurvey().saveToDatabase()
		return 201
	}
}

/**
 * The class where market survey subscriptions are submitted.
 */
@Path("subscriptions")
class MarketSurveySubscriptions {

	/**
	 * This function stores the MarketSurveySubscription json in
	 * the database.
	 *
	 * @return If the MarketSurveySubscription was created.
	 *
	 * @param data The MarketSurveySubscription's json.
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	fun createMarketSurveySubscription(data: String): Int {
		data.deserializeMarketSurveySubscription().saveToDatabase()
		return 201
	}


	/**
	 * This is just a function to verify that
	 *
	 * @See createMarketSurveySubscription(String) works.
	 *
	 * MarketSurveySubscriptions are automatically
	 * sended in SubscriptionScheduler.
	 *
	 * @See scheduleNotificationTasks()
	 *
	 * @return All MarketSurveySubscriptions.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	fun getMarketSurveySubscribers() = getAllSubscribers().serialize()
}