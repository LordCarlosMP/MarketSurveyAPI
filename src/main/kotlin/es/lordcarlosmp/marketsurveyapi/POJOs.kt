package es.lordcarlosmp.marketsurveyapi

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import java.util.*

/**
 * Represents real life MarketSurvey's characteristics,
 * its instances will be seriealized / deserialized
 * by Jackson and stored / readed by morphia.
 */

@Entity
data class MarketSurvey(
		
		/**
		 * The id of the MarketSurvey in the database.
		 */
		@Id
		var id: String = createDatabaseId(),
		
		/**
		 * The subject of the survey, represented by a number.
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
		 * The provider of the market survey.
		 */
		val provider: String,
		
		/**
		 * The target of the market survey.
		 */
		val target: Target)


/**
 * Creates a database Id of Type String with ObjectId.
 */
fun createDatabaseId() = ObjectId().toHexString()

/**
 * The class used to manage market survey's requests,
 * its purpose is to filter market surveys stored in the database,
 * a market survey request will match in the Request if:
 * - Has exact same subject.
 * - Has been done after the [date] of the request.
 * - Has been made in one of the [countries] of the request.
 * - Its [target] matches with the request [target].
 *
 *  The filtering is made in [es.lordcarlosmp.marketsurveyapi.database.MongoMarketSurveyRepository].
 */
@Embedded
data class Request(
		
		/**
		 * The subject of the desired market surveys.
		 */
		val subject: Int,
		
		/**
		 * The oldest dates that the surveys can have.
		 * null means no filter.
		 */
		val date: Date? = null,
		
		/**
		 * The countries of the market survey request,
		 * null means no filter.
		 */
		val countries: List<String>? = null,
		
		/**
		 * The target of the desired market survey.
		 * null means no filter.
		 */
		val target: Target? = null
)

/**
 * Represents market survey subscriptions,
 * its instances will be seriealized / deserialized
 * by Jackson and stored / readed by morphia.
 */
@Entity
data class Subscription(
		
		/**
		 *  The id of the subscription in the database.
		 */
		@Id
		var id: String = createDatabaseId(),
		
		/**
		 * The request the subscriber is interested in.
		 */
		val request: Request,
		
		/**
		 * The subscription frequency.
		 */
		val frequency: SubscriptionFrequency,
		
		/**
		 * The DeliveryDate of the subscription.
		 */
		val deliveryData: DeliveryData)

/**
 * Stores the necessary data for sending
 * notifications to the subscribers.
 */
data class DeliveryData(
		
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
		 * The ftp where to send the notification.
		 */
		val ftp: String? = null)

/**
 * The subscription frequencies.
 */
enum class SubscriptionFrequency {
	DAILY, WEEKLY, MONTHLY, YEARLY
}

/**
 * Represents the target of people where a survey was made.
 */
@Embedded
data class Target(
		
		/**
		 * The genders of the market survey respondents,
		 * null means unknown.
		 */
		val genders: List<Gender>? = null,
		
		/**
		 * The age range of the market survey respondents,
		 * null means unknown.
		 */
		val age: Range? = null,
		
		/**
		 * The annual income range of the market survey respondents,
		 * null means unknown.
		 */
		val income: Range? = null
)

/**
 * Stores and compares ranges.
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