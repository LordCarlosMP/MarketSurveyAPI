package es.lordcarlosm.marketsurveyapi

import es.lordcarlosm.marketsurveyapi.SubscriptionType.FTP
import es.lordcarlosm.marketsurveyapi.SubscriptionType.MAIL
import es.lordcarlosm.marketsurveyapi.SubscriptionType.PHONE
import es.lordcarlosm.marketsurveyapi.SubscriptionType.POSTAL
import java.util.*

/**
 * The class we are using to manage market survey's
 * subscriptions.
 */
data class MarketSurveySubscription(

		/**
		 * The request the subscriber is interested in.
		 */
		val marketSurveyRequest: MarketSurveyRequest,

		/**
		 * The subscription type.
		 */
		private val subscriptionType: Array<SubscriptionType>,

		/**
		 * The subscription frequency.
		 */
		val frequency: SubscriptionFrequency,

		/**
		 * A class for storing the delivery data.
		 */
		private val sendData: SendData) {

	/**
	 * Notifies the subscriber about the market surveys available,
	 * it is not implemented because providing market survey results
	 * fall outside the scope of this assignment.
	 *
	 * The function below just prints its json in the application terminal.
	 */
	fun notifySubscriber(requests: List<MarketSurvey>) {
		for (type in subscriptionType) {
			println("Sending ${requests.serialize()} to ${sendData.dataToSend(type)} by $type")
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
		 * The ftp where to send the notification.
		 */
		private val ftp: String?) {

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
data class MarketSurveyRequest(

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
		val countries: Array<String>?,

		/**
		 * The target of the desired market survey.
		 * null means no filtering.
		 */
		val target: Target?
)

data class MarketSurvey(

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

data class Target(

		/**
		 * The genders of the market survey respondents,
		 * null means unknown.
		 */
		val genders: Array<Gender>?,

		/**
		 * The age range of the market survey respondents,
		 * null means unknown.
		 */
		val age: Range<Int>?,

		/**
		 * The annual income range of the market survey respondents,
		 * null means unknown.
		 */
		val income: Range<Int>?
)

/**
 * The class we are using to store and compare ranges.
 */
data class Range<T : Comparable<T>>(val start: T, val endInclusive: T) {

	/**
	 * @param  range The range to compare with.
	 * @Return If the range is contained inside this range.
	 */
	operator fun contains(range: Range<T>) = start <= range.start && endInclusive >= range.endInclusive
}

/**
 * The Genders supported, to add new ones, just add them  to
 * the enum, there's no further action required.
 */
enum class Gender { MALE, FEMALE, HERMAPHRODITE, ASEXUAL }