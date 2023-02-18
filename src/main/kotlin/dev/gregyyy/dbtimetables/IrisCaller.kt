package dev.gregyyy.dbtimetables

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.gregyyy.dbtimetables.model.IrisTimetable
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class IrisCaller(private val baseUrl: String) {

    private val client = OkHttpClient()
    private val mapper = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }).registerKotlinModule()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getCurrentTimetable(stationEva: String, dateTime: LocalDateTime): IrisTimetable {
        val date = dateTime.format(DateTimeFormatter.ofPattern("yyMMdd"))
        val hour = dateTime.format(DateTimeFormatter.ofPattern("HH"))

        val url = "$baseUrl/plan/$stationEva/$date/$hour"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()

        return mapper.readValue(body, IrisTimetable::class.java)
    }

    fun getFullChangesTimetable(stationEva: String): IrisTimetable {
        val url = "$baseUrl/fchg/$stationEva"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()

        return mapper.readValue(body, IrisTimetable::class.java)
    }

}