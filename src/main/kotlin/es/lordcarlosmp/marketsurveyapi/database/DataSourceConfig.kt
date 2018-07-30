package es.lordcarlosmp.marketsurveyapi.database

import com.mongodb.MongoClient
import es.lordcarlosmp.marketsurveyapi.MarketSurvey
import es.lordcarlosmp.marketsurveyapi.Subscription
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration



/**
 * Provides the datastore bean used by [MarketSurveyRepository]
 * and [SubscriptionRepository]
 */
@Configuration
class DataSourceConfig(var mongoProperties: MongoProperties) {
	
	/**
	 * Creates a morphia instance with [MarketSurvey]
	 * and [Subscription] already mapped
	 * */
	private fun morphia(): Morphia {
		val morphia = Morphia()
		morphia.map(
				MarketSurvey::class.java,
				Subscription::class.java
		)
		return morphia
	}
	
	/**
	 * Creates a datastore with the credentials defined in the
	 * Application.yml
	 */
	@Bean
	fun datastore(mongoClient: MongoClient): Datastore {
		val datastore = morphia().createDatastore(mongoClient, mongoProperties.database)
		datastore.ensureIndexes()
		return datastore
	}
}