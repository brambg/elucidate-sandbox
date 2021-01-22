package huc.di.tt.elucidate

import kotlinx.coroutines.runBlocking
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.junit.Test
import kotlin.random.Random

class ElucidateClientTest {

    @Test
    fun create_a_container() {
        val cf = ElucidateClient(ELUCIDATE_BASE)
        val label = "A Container for Web Annotations"
        val name = "container-" + Random.nextInt()
        val container = runBlocking { cf.createContainer(name, label) }
        cf.close()
        println(container.id)
        println(container.label)
        assert(container.id.startsWith(ELUCIDATE_BASE))
    }


    @Test
    fun test() {
        val httpClient = HttpClients.createDefault()

        httpClient.use { httpClient ->
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

    companion object {
        // When accessing dockerized server on WSL2, localhost does not work in ktor, see https://github.com/microsoft/WSL/issues/4983
        // use the ipv6 url:
        const val ELUCIDATE_BASE = "http://[::1]:8080/annotation"

        // or use `ifconfig eth0` in wsl and enter the inet ip here
//        const val ELUCIDATE_BASE = "http://172.23.32.40:8080/annotation"
    }

}