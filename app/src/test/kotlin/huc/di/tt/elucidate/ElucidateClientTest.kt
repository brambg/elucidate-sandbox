package huc.di.tt.elucidate

import com.digirati.elucidate.common.model.annotation.w3c.W3CAnnotation
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jsonldjava.core.JsonLdOptions
import com.github.jsonldjava.core.JsonLdProcessor
import com.github.jsonldjava.utils.JsonUtils
import huc.di.tt.elucidate.ElucidateClient.*
import kotlinx.coroutines.runBlocking
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.junit.Test
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class ElucidateClientTest {

    @Test
    fun common_lib() {
        val w3ca = W3CAnnotation()
        println(w3ca)
        println(w3ca.jsonMap)
        val j = ObjectMapper().writeValueAsString(w3ca)
        println(j)
    }

    @Test
    fun statistics() {
        val cf = ElucidateClient(ELUCIDATE_BASE)

        val bodyIdStats = runBlocking { cf.bodyStatistics(StatisticsFieldValues.ID) }
        println("body.id stats: $bodyIdStats")
        val bodySourceStats = runBlocking { cf.bodyStatistics(StatisticsFieldValues.SOURCE) }
        println("body.source stats: $bodySourceStats")
        val targetIdStats = runBlocking { cf.targetStatistics(StatisticsFieldValues.ID) }
        println("target.id stats: $targetIdStats")
        val targetSourceStats = runBlocking { cf.targetStatistics(StatisticsFieldValues.SOURCE) }
        println("target.source stats: $targetSourceStats")

        cf.close()
    }

    @Test
    fun search_by_body() {
        val cf = ElucidateClient(ELUCIDATE_BASE)
        val containerName = "search_by_body_container"
        if (!cf.containerExists(containerName)) {
            runBlocking {
                cf.createContainer(containerName, "container for annotations to search by body")
                cf.addAnnotation(
                    containerName,
                    "annotation-01",
                    annotationJsonLD("This is good", "http://example.org/good")
                )
                cf.addAnnotation(
                    containerName,
                    "annotation-02",
                    annotationJsonLD("This is better", "http://example.org/better")
                )
                cf.addAnnotation(
                    containerName,
                    "annotation-03",
                    annotationJsonLD("This is best", "http://example.org/best")
                )
            }
        }

        val value = "this"
        val result = runBlocking { cf.searchByBody(SearchByBodyFieldsValues.ID, value) }
        println(result)

        val value2 = "good"
        val result2 = runBlocking { cf.searchByBody(SearchByBodyFieldsValues.SOURCE, value2) }
        println(result2)
        cf.close()
    }

    @Test
    fun search_by_target() {
        val cf = ElucidateClient(ELUCIDATE_BASE)
        val containerName = "search_by_target_container"
        if (!cf.containerExists(containerName)) {
            runBlocking {
                cf.createContainer(containerName, "container for annotations to search by target")
                cf.addAnnotation(
                    containerName,
                    "annotation-01",
                    annotationJsonLD("This is good", "http://example.org/good")
                )
                cf.addAnnotation(
                    containerName,
                    "annotation-02",
                    annotationJsonLD("This is better", "http://example.org/better")
                )
                cf.addAnnotation(
                    containerName,
                    "annotation-03",
                    annotationJsonLD("This is best", "http://example.org/best")
                )
            }
        }

        val value = "good"
        val result = runBlocking { cf.searchByTarget(SearchByTargetFieldsValues.ID, value) }
        println(result)

        val value2 = "http://example.org/better"
        val result2 = runBlocking { cf.searchByTarget(SearchByTargetFieldsValues.SOURCE, value2) }
        println(result2)

        cf.close()
    }

    @Test
    fun search_by_creator() {
        val cf = ElucidateClient(ELUCIDATE_BASE)
        val containerName = "search_by_creator_container"
        if (!cf.containerExists(containerName)) {
            runBlocking {
                cf.createContainer(containerName, "container for annotations to search by creator")
                cf.addAnnotation(
                    containerName,
                    "annotation-01",
                    annotationJsonLD("This is good", "http://example.org/good")
                )
                cf.addAnnotation(
                    containerName,
                    "annotation-02",
                    annotationJsonLD("This is better", "http://example.org/better")
                )
                cf.addAnnotation(
                    containerName,
                    "annotation-03",
                    annotationJsonLD("This is best", "http://example.org/best")
                )
            }
        }
        val levels = SearchByCreatorLevelsValues.ANNOTATION
        val type = SearchByCreatorTypeValues.NAME

        val value = "good"
        val result = runBlocking { cf.searchByCreator(levels, type, value) }
        println(result)

        val value2 = "http://example.org/better"
        val result2 = runBlocking { cf.searchByCreator(levels, type, value2) }
        println(result2)

        cf.close()
    }

    @Test
    fun create_a_container() {
        val cf = ElucidateClient(ELUCIDATE_BASE)
        val label = "A Container for Web Annotations"
        val name = "container-" + Random.nextInt()
        val container = runBlocking { cf.createContainer(name, label) }
        runBlocking {
            val containerJsonLd = cf.getContainer(name)
            println(containerJsonLd)
            cf.deleteContainer(name)
        }
        cf.close()
        println(container.id)
        println(container.label)
        assert(container.id.startsWith(ELUCIDATE_BASE))
    }

    @Test
    fun create_an_annotation() {
        val cf = ElucidateClient(ELUCIDATE_BASE)
        val containerLabel = "A Container for Web Annotations"
        val containerName = "container-" + Random.nextInt()
        val annotationName = "annotation-" + Random.nextInt()
        val annotationJson = annotationJsonLD("This is a test", "http://example.org/example-target")
        runBlocking {
            val container = cf.createContainer(containerName, containerLabel)
            println(container)
            cf.addAnnotation(containerName, annotationName, annotationJson)
            val containerJsonLd = cf.getContainer(containerName)
            println(containerJsonLd)
            cf.deleteContainer(containerName)
        }
        cf.close()
    }

    private fun annotationJsonLD(bodyValue: String, targetURL: String): String =
        """
        {
          "@context": "http://www.w3.org/ns/anno.jsonld",
          "type": "Annotation",
          "body": {
            "type": "TextualBody",
            "value": "$bodyValue"
          },
          "target": "$targetURL"
        }
        """.trimIndent()

    @Test
    fun create_multiple_annotations() {
        val cf = ElucidateClient(ELUCIDATE_BASE)
        val containerLabel = "A Container for Web Annotations"
        val containerName = "container-" + Random.nextInt()
        runBlocking {
            val time = measureTimeMillis {
                val container = cf.createContainer(containerName, containerLabel)
                println(container)
                for (n in 1..1000) {
                    val annotationName = "annotation-$n"
                    val annotationJson =
                        annotationJsonLD("This is test $n/1000", "http://example.org/annotation-target-$n")
//                    async {
                    cf.addAnnotation(containerName, annotationName, annotationJson)
//                    }
                }
                val containerJsonLd = cf.getContainer(containerName)
                println(containerJsonLd)
                cf.deleteContainer(containerName)
            }
            println("Completed in $time ms")
        }
        cf.close()
    }

    @Test
    fun test() {
        HttpClients.createDefault().use { httpClient ->
            val request = HttpGet(ELUCIDATE_BASE)

            val response = httpClient.execute(request)
            response.use { response ->
                // Get HttpResponse Status
                println(response.protocolVersion) // HTTP/1.1
                println(response.statusLine.statusCode) // 200
                println(response.statusLine.reasonPhrase) // OK
                println(response.statusLine.toString()) // HTTP/1.1 200 OK
                val entity = response.entity
                if (entity != null) {
                    // return it as a String
                    val result = EntityUtils.toString(entity)
                    println(result)
                }
            }
        }
    }

    @Test
    fun test_jsonld() {
        val json = """
            {
                "a":"A",
                "b":2
            }""".trimIndent()
        val inputStream = ByteArrayInputStream(json.toByteArray())
//        val jsonObject = JsonUtils.fromInputStream(inputStream)

        // Open a valid json(-ld) input file
//        val inputStream: InputStream = FileInputStream("input.json")
        // Read the file into an Object (The type of this object will be a List, Map, String, Boolean,
        // Number or null depending on the root object in the file).
        val jsonObject = JsonUtils.fromInputStream(inputStream)
        // Create a context JSON map containing prefixes and definitions
        val context: Map<*, *> = HashMap<Any?, Any?>()
        // Customise context...
        // Create an instance of JsonLdOptions with the standard JSON-LD options
        val options = JsonLdOptions()
        // Customise options...

        // Call whichever JSONLD function you want! (e.g. compact)
        val compact = JsonLdProcessor.compact(jsonObject, context, options)
        // Print out the result (or don't, it's your call!)
        println(JsonUtils.toPrettyString(compact))
    }

    companion object {
//        const val ELUCIDATE_BASE = "http://localhost:8080"
//        const val ELUCIDATE_BASE = "http://[::1]:8080"
        // When accessing dockerized server on WSL2, localhost does not work in ktor, see https://github.com/microsoft/WSL/issues/4983
        // use the ipv6 url:
//        const val ELUCIDATE_BASE = "http://[::1]:8080/annotation"

        // or use `ifconfig eth0` in wsl and enter the inet ip here
        const val ELUCIDATE_BASE = "http://172.22.251.78:8080/annotation"
    }

}