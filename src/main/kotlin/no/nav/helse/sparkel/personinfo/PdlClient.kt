package no.nav.helse.sparkel.personinfo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class PdlClient(
    private val baseUrl: String,
    private val stsClient: StsRestClient
) {

    companion object {
        private val objectMapper = ObjectMapper()
        private val httpClient = HttpClient.newHttpClient()
        private val personQuery = this::class.java.getResource("/pdl/hentPerson.graphql").readText().replace(Regex("[\n\r]"), "")
    }

    internal fun hentPersoninfo(
        fødselsnummer: String,
        behovId: String
    ): JsonNode {
        val stsToken = stsClient.token()

        val body = "placeholder"

        val request = HttpRequest.newBuilder(URI.create(baseUrl))
            .header("TEMA", "SYK")
            .header("Authorization", "Bearer $stsToken")
            .header("Nav-Consumer-Token", "Bearer $stsToken")
            .header("Accept", "application/json")
            .header("Nav-Call-Id", behovId)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        val responseHandler = HttpResponse.BodyHandlers.ofString()

        val response = httpClient.send(request, responseHandler)
        response.statusCode().let {
            if(it >= 300) throw RuntimeException("error (responseCode=$it) from PDL")
        }
        println(response.body()?: null)


        val (responseCode, responseBody) = with(URL(baseUrl).openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000

            val stream: InputStream? = if (responseCode < 300) this.inputStream else this.errorStream
            responseCode to stream?.bufferedReader()?.readText()
        }

        if (responseCode >= 300 || responseBody == null) {
            throw RuntimeException("error (responseCode=$responseCode) from PDL")
        }

        return objectMapper.readTree(responseBody)
    }
}
