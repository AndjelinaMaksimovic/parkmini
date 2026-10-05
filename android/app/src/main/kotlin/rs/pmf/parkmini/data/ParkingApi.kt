package rs.pmf.parkmini.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** An error returned by our backend (e.g. 409: the spot is already taken). */
class ApiException(message: String) : Exception(message)

class ParkingApi(private val baseUrl: String = BASE_URL) {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getSpots(): List<ParkingSpot> = client.get("$baseUrl/api/spots").parse()

    suspend fun getStats(): Stats = client.get("$baseUrl/api/spots/stats").parse()

    suspend fun reserve(id: Int, driver: String): ParkingSpot = postAction(id, "reserve", driver)

    suspend fun confirm(id: Int, driver: String): ParkingSpot = postAction(id, "confirm", driver)

    suspend fun release(id: Int, driver: String): ParkingSpot = postAction(id, "release", driver)

    private suspend fun postAction(id: Int, action: String, driver: String): ParkingSpot =
        client.post("$baseUrl/api/spots/$id/$action") {
            contentType(ContentType.Application.Json)
            setBody(DriverRequest(driver))
        }.parse()

    private suspend inline fun <reified T> HttpResponse.parse(): T {
        if (status.isSuccess()) return body()
        // The server answers errors with a short plain text, e.g. "Spot A2 is not free (RESERVED)".
        val message = bodyAsText().ifBlank { "Error ${status.value}" }
        throw ApiException(message)
    }

    companion object {
        // 10.0.2.2 is the address of the computer on which the emulator runs.
        // On a real phone use the IP address of the computer, e.g. "http://192.168.0.15:8080".
        const val BASE_URL = "http://10.0.2.2:8080"
    }
}
