package es.lordcarlosmp.marketsurveyapi.database

import com.mongodb.WriteResult
import es.lordcarlosmp.marketsurveyapi.Gender
import es.lordcarlosmp.marketsurveyapi.MarketSurvey
import es.lordcarlosmp.marketsurveyapi.Request
import es.lordcarlosmp.marketsurveyapi.Subscription
import es.lordcarlosmp.marketsurveyapi.SubscriptionFrequency
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Key
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.text.SimpleDateFormat
import java.util.*

//todo: mover los interfaces y documentar + superinterfaz

interface MarketSurveyRepository {
	fun create(survey: MarketSurvey): Key<MarketSurvey>
	fun findAllMatching(request: Request): List<MarketSurvey>
	fun findById(id: String): MarketSurvey?
	fun findAll(): List<MarketSurvey>?
	fun delete(id: String): WriteResult
}

interface SubscriptionRepository {
	fun create(subscription: Subscription): Key<Subscription>
	fun findAllInFrecuency(frequency: SubscriptionFrequency): List<Subscription>
	fun findById(id: String): Subscription?
	fun findAll(): List<Subscription>
	fun delete(id: String): WriteResult
}

@Repository
class MongoMarketSurveyRepository : MarketSurveyRepository {
	
	@Autowired
	private lateinit var datastore: Datastore
	
	override fun findById(id: String) =
			datastore.get<MarketSurvey, Any>(MarketSurvey::class.java, id)
	
	override fun findAll() = datastore.find(MarketSurvey::class.java).asList()
	
	override fun delete(id: String) = datastore.delete(MarketSurvey::class.java, id)!!
	
	override fun create(survey: MarketSurvey) = datastore.save(survey)!!
	
	override fun findAllMatching(request: Request): List<MarketSurvey> {
		
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
					field("date").greaterThan(formatToyyyymmdd())
				}
				
				//Add country filters if the countries array is not null.
				countries?.run { field("country").`in`(countries) }
				
				return fetch().toList()
			}
		}
	}
}

@Repository
class MongoSubscriptionRepository : SubscriptionRepository {
	@Autowired
	private lateinit var datastore: Datastore
	
	override fun findAllInFrecuency(frequency: SubscriptionFrequency) =
			datastore.createQuery(Subscription::class.java).field("frequency").equal(frequency).fetch().toList()
	
	override fun create(subscription: Subscription) = datastore.save(subscription)
	
	override fun findById(id: String) = datastore.get<Subscription, Any>(Subscription::class.java, id)
	
	override fun findAll() = datastore.find(Subscription::class.java).asList()
	
	override fun delete(id: String) = datastore.delete(Subscription::class.java, id)
	
}

fun Date.formatToyyyymmdd() = SimpleDateFormat("yyyy-MM-dd").format(this)!!