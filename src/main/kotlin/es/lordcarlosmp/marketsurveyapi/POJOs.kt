package es.lordcarlosmp.marketsurveyapi

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import java.util.*

/**
 * The class we are using to manage market survey's
 * subscriptions.
 */

@Entity
data class Subscription(
		//todo: comentario decir que deja de ser null cuando se guarda en la base de datos
		
		/**
		 * The Id of the subscription, null until it's stored in the database.
		 */
		@Id
		val id: String? = null,
		
		/**
		 * The request the subscriber is interested in.
		 */
		val request: Request,
		
		/**
		 * The subscription frequency.Array
		 */
		val frequency: SubscriptionFrequency,
		
		/**
		 * A class for storing the delivery data.
		 */
		val sendData: SendData)

/**
 * SendData is the class who stores the necessary
 * data for sending notifications to the subscribers.
 */
data class SendData(
		
		/**
		 *  The e-mail where to send the notification.
		 */
		val mail: String? = null,
		
		/**
		 * The phone number where to send the notification.
		 */
		val phone: String? = null,
		
		/**
		 * The postal direction where to send the notification.
		 */
		val postalDirection: String? = null,
		
		/**
		 * The ftp where to send the notification.
		 */
		val ftp: String? = null)

/**
 * The subscription frequencies.
 */
enum class SubscriptionFrequency {
	DAILY, WEEKLY, MONTHLY, YEARLY
}

/**
 * The class we are using to manage MarketSurveys,
 * and will provide all functions to convert them to json.
 */
@Embedded
data class Request(
		
		/**
		 * The subject of the desired market survey.
		 */
		val subject: Int,
		
		/**
		 * The date where the market survey was made,
		 * null means no filtering.
		 */
		@get:JsonSerialize(using = YyyyMmDdDateSerializer::class)
		val date: Date? = null,
		
		/**
		 * The country of the market survey request,
		 * null means no filtering.
		 */
		val countries: List<String>? = null,
		
		/**JsonConverter
		 * The target of the desired market survey.
		 * null means no filtering.
		 */
		val target: Target? = null
)

@Entity
data class MarketSurvey(
		//todo: comentario
		@Id
		val id: ObjectId,
		/**
		 * The subject of the survey, represented by a number
		 * as the example in the API instructions.
		 */
		val subject: Int,
		
		/**
		 * The date when the market survey was made.
		 */
		//todo: explica anotacion
		@get:JsonSerialize(using = YyyyMmDdDateSerializer::class)
		val date: Date,
		
		/**
		 * The Country where the market survey was made.
		 */
		
		val country: String,
		
		/**
		 * Provider, the provider of the market survey.
		 */
		val provider: String,
		
		/**
		 * The target of the market survey.
		 */
		val target: Target)

@Embedded
data class Target(
		
		/**
		 * The genders of the market survey respondents,
		 * null means unknown.
		 */
		val genders: List<Gender>?,
		
		/**
		 * The age range of the market survey respondents,
		 * null means unknown.
		 */
		val age: Range?,
		
		/**
		 * The annual income range of the market survey respondents,
		 * null means unknown.
		 */
		val income: Range?
)

/**
 * The class we are using to store and compare ranges.
 */
@Embedded
data class Range(val start: Int, val endInclusive: Int) {
	
	/**
	 * @param  range The range to compare with.
	 * @Return If the range is contained inside this range.
	 */
	operator fun contains(range: Range) = start <= range.start && endInclusive >= range.endInclusive
}

/**
 * The Genders supported, to add new ones, just add them  to
 * the enum, there's no further action required.
 */
enum class Gender { MALE, FEMALE, HERMAPHRODITE, ASEXUAL }