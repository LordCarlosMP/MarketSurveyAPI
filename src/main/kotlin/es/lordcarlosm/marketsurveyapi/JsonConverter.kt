package es.lordcarlosm.marketsurveyapi

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import javax.ws.rs.BadRequestException

/**
 * The default Gson instance to use in this api, with its date format.
 */
val defaultGsonConverter: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

/**
 * This function deserialize a json into a market survey.
 */
fun String.deserializeMarketSurvey(): MarketSurvey {
	try {
		return defaultGsonConverter.fromJson(this, MarketSurvey::class.java)!!
	} catch (ex: Exception) {
		throw BadRequestException("Wrong JSON")
	}
}

/**
 * This function deserialize a json into a market survey.
 */
fun String.deserializeMarketSurveyRequest(): MarketSurveyRequest {
	try {
		return defaultGsonConverter.fromJson(this, MarketSurveyRequest::class.java)!!
	} catch (ex: Exception) {
		throw BadRequestException("Wrong JSON")
	}
}

/**
 * This function deserialize a json into a market survey.
 */
fun String.deserializeMarketSurveySubscription(): MarketSurveySubscription {
	try {
		return defaultGsonConverter.fromJson(this, MarketSurveySubscription::class.java)!!
	} catch (ex: Exception) {
		throw BadRequestException("Wrong JSON")
	}
}

/**
 * This function will serialize every object into a json using the default Gson converter.
 */
fun <T> T.serialize() = defaultGsonConverter.toJson(this)!!