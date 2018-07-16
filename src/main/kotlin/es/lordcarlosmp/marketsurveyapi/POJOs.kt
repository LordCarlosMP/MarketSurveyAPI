package es.lordcarlosmp.marketsurveyapi

import es.lordcarlosmp.marketsurveyapi.SubscriptionType.FTP
import es.lordcarlosmp.marketsurveyapi.SubscriptionType.MAIL
import es.lordcarlosmp.marketsurveyapi.SubscriptionType.PHONE
import es.lordcarlosmp.marketsurveyapi.SubscriptionType.POSTAL
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
		 * The subscription type.
		 */
		private val subscriptionType: List<SubscriptionType>,
		
		/**
		 * The subscription frequency.Array
		 */
		val frequency: SubscriptionFrequency,
		
		/**
		 * A class for storing the delivery data.
		 */
		private val sendData: SendData) {
	
	/**
	 * Notifies the sNoSuchMethodExceptionubscriber about the market surveys available,
	 * it is not implemented because providing market survey results
	 * fall outside the scope of this assignment.
	 *
	 * The function below just prints its json in the application terminal.
	 */
	//todo: move
	fun notifySubscriber(requests: List<MarketSurvey>) {
		for (type in subscriptionType) {
			println("Sending $requests to ${sendData.dataToSend(type)} by $type")
		}
	}
}

/**
 * SendData is the class who stores the necessary
 * data for sending notifications to the subscribers.
 */
data class SendData(
		
		/**
		 *  The e-mail where to send the notification.
		 */
		private val mail: String?,
		
		/**
		 * The phone number where to send the notification.
		 */
		private val phone: String?,
		
		/**
		 * The postal direction where to send the notification.
		 */
		private val postalDirection: String?,
		
		/**
		 * The ftp where to send the notification.
		 */
		private val ftp: String?) {
	//todo: move
	fun dataToSend(type: SubscriptionType) = when (type) {
		MAIL -> mail
		POSTAL -> postalDirection
		PHONE -> phone
		FTP -> ftp
	}
}

/**
 * The subscription types.
 */
enum class SubscriptionType {
	MAIL, POSTAL, PHONE, FTP
}

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
		val date: Date?,
		
		/**
		 * The country of the market survey request,
		 * null means no filtering.
		 */
		val countries: List<String>?,
		
		/**JsonConverter
		 * The target of the desired market survey.
		 * null means no filtering.
		 */
		val target: Target?
)

@Entity
data class MarketSurvey(
		//todo: comentario
		@Id
		val id: String?,
		/**
		 * The subject of the survey, represented by a number
		 * as the example in the API instructions.
		 */
		val subject: Int,
		
		/**
		 * The date when the market survey was made.
		 */
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
		 * Th    id "org.jetbrains.kotlin.plugin.noarg"  version "1.2.51" age range of the market survey respondents,
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