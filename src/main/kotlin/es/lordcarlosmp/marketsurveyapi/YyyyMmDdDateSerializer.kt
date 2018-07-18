package es.lordcarlosmp.marketsurveyapi

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//YyyyMmDdDateSerializer class
class YyyyMmDdDateSerializer : JsonSerializer<Date>() {
	@Throws(IOException::class, JsonProcessingException::class)
	
	override fun serialize(date: Date, gen: JsonGenerator, arg2: SerializerProvider) {
		gen.writeString(SimpleDateFormat("yyyy-MM-dd").format(date))
	}
}