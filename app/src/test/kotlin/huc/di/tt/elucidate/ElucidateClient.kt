package huc.di.tt.elucidate

import com.beust.klaxon.Klaxon
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*

class ElucidateClient(private val elucidateBaseURL: String) {

    private val searchBaseURL = "$elucidateBaseURL/w3c/services/search"

    private val httpClient = HttpClient()

    suspend fun createContainer(
        name: String,
        label: String = ""
    ): AnnotationContainer {
        val requestBody = """
            {
              "@context": [
                "http://www.w3.org/ns/anno.jsonld",
                "http://www.w3.org/ns/ldp.jsonld"
              ],
              "type": [
                "BasicContainer",
                "AnnotationCollection"
              ],
              "label": "$label"
            }""".trimIndent()
        val textContent = TextContent(requestBody, annoJsonLdContentType)
        val responseJson = httpClient.post<String> {
            url("$elucidateBaseURL/w3c/")
            accept(annoJsonLdContentType)
            header("Slug", name)
            body = textContent
        }
        val responseMap = Klaxon().parseJsonObject(StringReader(responseJson))
        return AnnotationContainer(responseMap["id"]!! as String, responseMap["label"]!! as String)
    }

    suspend fun getContainer(name: String): String =
        httpClient.get() {
            url("$elucidateBaseURL/w3c/$name/")
            accept(annoJsonLdContentType)
        }

    suspend fun deleteContainer(name: String) {
        // Not supported
//        val responseJson = httpClient.delete<String>() {
//            url("$elucidateBaseURL/w3c/$name/")
//        }
//        println(responseJson)
    }

    fun close() {
        httpClient.close()
    }

    suspend fun addAnnotation(containerName: String, annotationName: String, annotationJson: String) {
        val textContent = TextContent(annotationJson, annoJsonLdContentType)
        val responseJson = httpClient.post<String> {
            url("$elucidateBaseURL/w3c/$containerName/")
            header("Slug", annotationName)
            body = textContent
        }
        println(responseJson)
    }

    enum class SearchByBodyFieldsValues {
        ID, SOURCE
    }

    suspend fun searchByBody(
        fields: SearchByBodyFieldsValues,
        value: String,
        strict: Boolean = false,
        xywh: MediaFragmentsSpatialSelector? = null,
        t: MediaFragmentsTemporalSelector? = null,
        creator: String? = null,
        generator: String? = null
    ): String = httpClient.get() {
        url("$searchBaseURL/body")
        parameter("fields", fields.name.toLowerCase())
        parameter("value", value)
        parameter("strict", strict)
        xywh?.let { parameter("xywh", xywh) }
        t?.let { parameter("t", t) }
        creator?.let { parameter("creator", creator) }
        generator?.let { parameter("generator", generator) }
    }

    enum class SearchByTargetFieldsValues {
        ID, SOURCE
    }

    suspend fun searchByTarget(
        fields: SearchByTargetFieldsValues,
        value: String,
        strict: Boolean = false,
        xywh: MediaFragmentsSpatialSelector? = null,
        t: MediaFragmentsTemporalSelector? = null,
        creator: String? = null,
        generator: String? = null
    ): String = httpClient.get() {
        url("$searchBaseURL/target")
        parameter("fields", fields.name.toLowerCase())
        parameter("value", value)
        parameter("strict", strict)
        xywh?.let { parameter("xywh", xywh) }
        t?.let { parameter("t", t) }
        creator?.let { parameter("creator", creator) }
        generator?.let { parameter("generator", generator) }
    }

    enum class SearchByCreatorLevelsValues {
        ANNOTATION, BODY, TARGET
    }

    enum class SearchByCreatorTypeValues {
        ID, NAME, NICKNAME, EMAIL, EMAILSHA1
    }

    suspend fun searchByCreator(
        levels: SearchByCreatorLevelsValues,
        type: SearchByCreatorTypeValues,
        value: String,
        strict: Boolean = false
    ): String = httpClient.get() {
        url("$searchBaseURL/creator")
        parameter("levels", levels.name.toLowerCase())
        parameter("type", type.name.toLowerCase())
        parameter("value", value)
        parameter("strict", strict)
    }

    enum class SearchByGeneratorLevelsValues {
        ANNOTATION, BODY, TARGET
    }

    enum class SearchByGeneratorTypeValues {
        ID, NAME, NICKNAME, EMAIL, EMAILSHA1
    }

    suspend fun searchByGenerator(
        levels: SearchByGeneratorLevelsValues,
        type: SearchByGeneratorTypeValues,
        value: String,
        strict: Boolean = false
    ): String = httpClient.get() {
        url("$searchBaseURL/generator")
        parameter("levels", levels.name.toLowerCase())
        parameter("type", type.name.toLowerCase())
        parameter("value", value)
        parameter("strict", strict)
    }

    enum class SearchTemporalLevelsValues {
        ANNOTATION, BODY, TARGET
    }

    private suspend fun searchTemporal(
        levels: SearchTemporalLevelsValues,
        types: String,
        since: Date
    ): String = httpClient.get() {
        url("$searchBaseURL/temporal")
        parameter("levels", levels.name.toLowerCase())
        parameter("types", types)
        parameter("since", since.asISO8601())
    }

    suspend fun searchByCreated(
        levels: SearchTemporalLevelsValues,
        since: Date
    ): String =
        searchTemporal(levels, "created", since)

    suspend fun searchByModified(
        levels: SearchTemporalLevelsValues,
        since: Date
    ): String =
        searchTemporal(levels, "modified", since)

    suspend fun searchByGenerated(
        levels: SearchTemporalLevelsValues,
        since: Date
    ): String =
        searchTemporal(levels, "generated", since)

    private fun Date.asISO8601(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").apply {
            timeZone = TimeZone.getTimeZone("CST")
        }.format(time)

    enum class StatisticsFieldValues {
        ID, SOURCE
    }

    private suspend fun statistics(level: String, field: StatisticsFieldValues): String =
        httpClient.get() {
            url("$elucidateBaseURL/w3c/services/stats/$level")
            parameter("field", field.name.toLowerCase())
        }

    suspend fun bodyStatistics(field: StatisticsFieldValues): String =
        statistics("body", field)

    suspend fun targetStatistics(field: StatisticsFieldValues): String =
        statistics("target", field)

    fun containerExists(containerName: String): Boolean {
        val r = runBlocking {
            httpClient.get<HttpResponse>() {
                url("$elucidateBaseURL/w3c/$containerName/")
                accept(annoJsonLdContentType)
            }
        }
        return r.status.value == 200
    }

    companion object {
        val annoJsonLdContentType = ContentType("application", "ld+json").withParameter(
            "profile",
            "http://www.w3.org/ns/anno.jsonld"
        )
    }

}
