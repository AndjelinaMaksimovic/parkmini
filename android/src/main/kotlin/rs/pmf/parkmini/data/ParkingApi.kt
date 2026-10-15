package rs.pmf.parkmini.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import com.v1.parkmini.shared.LicensePlateRequest
import com.v1.parkmini.shared.ParkingSpot

// Task 10 | GIVEN (ready code)
/** An error returned by our backend (e.g. 409: the spot is already taken). */
class ApiException(message: String) : Exception(message)

class ParkingApi(private val baseUrl: String = BASE_URL, private val wsUrl: String = WS_URL) {

    // Task 9: the client with WebSockets and the list of spots as a Flow (typed)
    private val json = Json { ignoreUnknownKeys = true }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(json) }
        install(WebSockets) { contentConverter = KotlinxWebsocketSerializationConverter(json) }
    }

    /**
     * Live list of spots: the server sends the full list when we connect and again after every change.
     * The flow ends with an exception when the connection is lost.
     */
    fun spots(): Flow<List<ParkingSpot>> = flow {
        client.webSocket("$wsUrl/ws/spots") {
            while (true) {
                emit(receiveDeserialized<List<ParkingSpot>>())
            }
        }
    }

    // Task 10: occupy and release (typed)
    suspend fun occupy(id: Int, licensePlate: String): ParkingSpot = postAction(id, "occupy", licensePlate)

    suspend fun release(id: Int, licensePlate: String): ParkingSpot = postAction(id, "release", licensePlate)

    // Task 10 | GIVEN (ready code)
    private suspend fun postAction(id: Int, action: String, licensePlate: String): ParkingSpot =
        client.post("$baseUrl/api/spots/$id/$action") {
            contentType(ContentType.Application.Json)
            setBody(LicensePlateRequest(licensePlate))
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
        const val WS_URL = "ws://10.0.2.2:8080"
    }
}
