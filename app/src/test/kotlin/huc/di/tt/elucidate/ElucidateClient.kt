package huc.di.tt.elucidate

import com.beust.klaxon.Klaxon
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import java.io.StringReader

class ElucidateClient(private val elucidateBaseURL: String) {

    private val httpClient = HttpClient()

    fun deleteContainer(name: String) {

    }

    fun close() {
        httpClient.close()
    }

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
        val contentType = ContentType("application", "ld+json").withParameter(
            "profile",
            "http://www.w3.org/ns/anno.jsonld"
        )
        val textContent = TextContent(requestBody, contentType)
        val responseJson = httpClient.post<String> {
            url("$elucidateBaseURL/w3c/")
            accept(contentType)
//            header("Slug", name)
            body = textContent
        }
        val responseMap = Klaxon().parseJsonObject(StringReader(responseJson))
        return AnnotationContainer(responseMap["id"]!! as String, responseMap["label"]!! as String)
    }

}
