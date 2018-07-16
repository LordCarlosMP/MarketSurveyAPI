package es.lordcarlosmp.marketsurveyapi.database

import com.mongodb.MongoClient
import es.lordcarlosmp.marketsurveyapi.MarketSurvey
import es.lordcarlosmp.marketsurveyapi.Subscription
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataSourceConfig(var mongoProperties: MongoProperties) {
	
	private fun morphia(): Morphia {
		val morphia = Morphia()
		// tell Morphia where to find your classes
		morphia.map(
				MarketSurvey::class.java,
				Subscription::class.java
		)
		
		return morphia
	}
	
	@Bean
	fun datastore(mongoClient: MongoClient): Datastore {
		// create the Datastore connecting to the default port on the local host
		val datastore = morphia().createDatastore(mongoClient, mongoProperties.database)
		datastore.ensureIndexes()
		return datastore
	}
}