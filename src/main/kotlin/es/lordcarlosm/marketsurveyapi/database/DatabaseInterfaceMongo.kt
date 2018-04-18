package es.lordcarlosm.marketsurveyapi.database

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.`in`
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.lte
import com.mongodb.client.model.Filters.nin
import es.lordcarlosm.marketsurveyapi.Gender
import es.lordcarlosm.marketsurveyapi.MarketSurvey
import es.lordcarlosm.marketsurveyapi.MarketSurveyRequest
import es.lordcarlosm.marketsurveyapi.MarketSurveySubscription
import es.lordcarlosm.marketsurveyapi.SubscriptionFrequency
import es.lordcarlosm.marketsurveyapi.deserializeMarketSurvey
import es.lordcarlosm.marketsurveyapi.deserializeMarketSurveySubscription
import es.lordcarlosm.marketsurveyapi.serialize
import org.bson.Document
import org.bson.conversions.Bson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * The mongo database the api will use.
 */
val db: MongoDatabase = CREDENTIALS.run { MongoClient(host, port).getDatabase(database) }

/**
 * The marketsurveysCollection.
 */
val marketsurveysCollection: MongoCollection<Document> = db.getCollection("marketsurveys")

/**
 * The subscriptionsCollection.
 */
val subscriptionsCollection: MongoCollection<Document> = db.getCollection("subscriptions")

/**
 * Saves the [MarketSurvey] in the database.
 */
fun MarketSurvey.saveToDatabase() {
	marketsurveysCollection.insertOne(Document.parse(serialize()))
}

/**
 * Saves [MarketSurveySubscription] in the database.
 */
fun MarketSurveySubscription.saveToDatabase() {
	subscriptionsCollection.insertOne(Document.parse(serialize()))
}

/**
 * @return The market surveys that fit the [MarketSurveyRequest].
 */
fun MarketSurveyRequest.getMatchingMarketSurveys(): List<MarketSurvey> {

	val filters = ArrayList<Bson>().apply {

		//Add subject filter (in any case).
		add(eq("subject", subject))

		//Add age, income and gender filters if the target is not null.
		target?.run {
			//Add age filters if the age range is not null.
			age?.run {
				add(and(
						gte("target.age.start", start),
						lte("target.age.endInclusive", endInclusive)
				))
			}

			//Add income filters if the income range is not null.
			income?.run {
				add(and(
						gte("target.income.start", start),
						lte("target.income.endInclusive", endInclusive)
				))
			}
			//Add gender filters if the gender array is not null.
			genders?.run {

				//The best way to provide market surveys
				//containing only the requested genders (or subsets)
				//is ensuring that no "no required" gender is INSIDE our
				//required genders array.

				val missingGenders = Gender.values().filter { it !in genders }

				add(and(nin("target.genders", missingGenders.map { it.toString() })))
			}
		}

		//Add date filters if the date is not null.
		date?.run {
			add(gte("date", toSql()))
		}

		//Add country filters if the country array is not null.
		countries?.run { add(`in`("country", countries.toList())) }
	}

	//Seems strange, but it is the fastest way to do it, MongoDB stores data just like a JSON, but the
	//driver is not as efficient as Google's Gson and does not "translate" the query results in POJOs,
	//but it can still bring us the json, an then, we can translate it to a POJO with Gson.
	return marketsurveysCollection.find(and(filters))

			//Retrieve all the results from the query.
			.all()

			//Map the document json to the market survey POJO with Gson.
			.map { it.toJson().deserializeMarketSurvey() }
}

/**
 * @return All the subscribers stored in the database whose subscription frequency is [frequency].
 */
fun getSubscribers(frequency: SubscriptionFrequency) =
		subscriptionsCollection.find(eq("frequency", frequency.toString())).all().map { it.toJson().deserializeMarketSurveySubscription() }

/**
 * @return All the subscribers stored in the database.
 */
fun getAllSubscribers() =
		subscriptionsCollection.find().all().map { it.toJson().deserializeMarketSurveySubscription() }

fun Date.toSql() = SimpleDateFormat("yyyy-MM-dd").format(this)!!

/**
 * This function iterates an iterable and returns
 * all its values.
 */
fun <T> Iterable<T>.all() = map { it }