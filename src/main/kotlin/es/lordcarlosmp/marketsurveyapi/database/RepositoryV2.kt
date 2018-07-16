package es.lordcarlosmp.marketsurveyapi.database

import com.mongodb.WriteResult
import es.lordcarlosmp.marketsurveyapi.Gender
import es.lordcarlosmp.marketsurveyapi.MarketSurvey
import es.lordcarlosmp.marketsurveyapi.Request
import es.lordcarlosmp.marketsurveyapi.Subscription
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Key
import org.mongodb.morphia.query.UpdateOperations
import org.mongodb.morphia.query.UpdateResults
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.text.SimpleDateFormat
import java.util.*

//todo: mover los interfaces y documentar + superinterfaz

interface MarketSurveyRepository {
	fun create(survey: MarketSurvey): Key<MarketSurvey>
	fun readMatchingRequest(request: Request): List<MarketSurvey>
}

interface SubscriptionRepository {
	fun create(book: Subscription): Key<Subscription>
	fun read(id: String): Subscription
	fun findAll(): List<Subscription>
	fun findAllInFrecuency(frequency: SubscriptionFrequency): List<Subscription>
	fun update(subscription: Subscription, operations: UpdateOperations<Subscription>): UpdateResults
	fun delete(subscription: Subscription): WriteResult
	fun createOperations(): UpdateOperations<Subscription>
}

@Repository
class MongoMarketSurveyRepository : MarketSurveyRepository {
	@Autowired
	private lateinit var datastore: Datastore
	
	override fun create(survey: MarketSurvey): Key<MarketSurvey> = datastore.save(survey)
	
	//filter
	override fun readMatchingRequest(request: Request): List<MarketSurvey> {
		with(request) {
			(datastore.createQuery(MarketSurvey::class.java)).run {
				field("subject").equal(subject)
				target?.run {
					//Add agquerye filters if the age range is not null.
					age?.run {
						field("target.age.start").greaterThan(start)
						field("target.age.endInclusive").greaterThan(endInclusive)
					}
					//Add income filters if the income range is not null.
					income?.run {
						field("target.income.start").greaterThan(start)
						field("target.income.endInclusive").greaterThan(endInclusive)
					}
					//Add gender filters if the gender array is not null.
					genders?.run {
						
						//The best way to provide market surveys
						//containing only the requested genders (or subsets)
						//is ensuring that no "no required" gender is INSIDE our
						//required genders array.
						
						val missingGenders = Gender.values().filter { it !in genders }
						
						field("target.genders").notIn(missingGenders.map { it.toString() })
					}
				}
				
				//Add date filters if the date is not null.
				date?.run {
					field("date").greaterThan(toSql())
				}
				
				//Add country filters if the countries array is not null.
				countries?.run { field("country").`in`(countries) }
				
				return fetch().all()
			}
		}
	}
}

@Repository
class MongoSubscriptionRepository : SubscriptionRepository {
	@Autowired
	private lateinit var datastore: Datastore
	
	override fun findAllInFrecuency(frequency: SubscriptionFrequency): List<Subscription> =
			datastore.createQuery(Subscription::class.java).field("frequency").equal(frequency).fetch().all()
	
	override fun create(book: Subscription): Key<Subscription> = datastore.save(book)
	
	override fun read(id: String): Subscription =
			datastore.get<Subscription, Any>(Subscription::class.java, id)
	
	override fun findAll(): List<Subscription> = datastore.find(Subscription::class.java).asList()
	
	override fun update(subscription: Subscription, operations: UpdateOperations<Subscription>): UpdateResults =
			datastore.update(subscription, operations)
	
	override fun delete(subscription: Subscription): WriteResult = datastore.delete(subscription)
	
	override fun createOperations(): UpdateOperations<Subscription> =
			datastore.createUpdateOperations(Subscription::class.java)
}

fun <T> Iterable<T>.all() = map { it }

fun Date.toSql() = SimpleDateFormat("yyyy-MM-dd").format(this)!!