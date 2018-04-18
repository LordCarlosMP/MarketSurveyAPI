package es.lordcarlosmp.marketsurveyapi.database

import com.google.gson.Gson
import java.io.File
import java.util.*

/**
 * A class for storing database credentials.
 */
data class DatabaseCredentials(
		val host: String,
		val port: Int,
		val user: String,
		val pass: String,
		val database: String,
		val authDatabase: String
)

/**
 *  Not the proper way to do it, but
 *  just a really fast way to manage the
 *  MongoDB database credentials.
 *
 *  This is the database credentials, stored
 *  in the mongo_config.json file inside
 *  the .war.
 *
 *  The function just takes database_config.json
 *  file inside the .war and translates it to a
 *  DatabaseCredentials.
 */
val CREDENTIALS: DatabaseCredentials by lazy {

	val classloader = Thread.currentThread().contextClassLoader

	val result = StringBuilder()

	val file = File(classloader.getResource("mongo_config.json")!!.file)

	try {
		Scanner(file).use({ scanner ->

			while (scanner.hasNextLine()) {
				result.append(scanner.nextLine())
			}

			scanner.close()

		})
	} catch (e: Exception) {
		e.printStackTrace()
	}

	Gson().fromJson(result.toString(), DatabaseCredentials::class.java)
}