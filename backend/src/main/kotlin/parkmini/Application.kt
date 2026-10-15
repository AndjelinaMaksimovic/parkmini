package com.v1.parkmini

import com.v1.parkmini.models.sampleSpots
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import com.v1.parkmini.shared.LicensePlateRequest
import com.v1.parkmini.shared.isValidLicensePlate
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.launch

// Task 3
fun main() {

    /* Start from here for Task 3, and then built up to current version.
    val spots = sampleSpots(occupiedShare = 0.4)

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }

        routing {
            get("/") { call.respondText("ParkMini API works.") }
            get("/api/spots") { call.respond(spots) }
        }
    }.start(wait = true)
    */

    val repository = ParkingRepository(sampleSpots(occupiedShare = 0.4))

    // starting a server inside our project
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() } // no handwriting of JSON

        install(WebSockets) { contentConverter = KotlinxWebsocketSerializationConverter(DefaultJson) }

        launch { sensorSimulator(repository) }      // Task 5: the sensors work in the background

        routing {
            get("/") { call.respondText("ParkMini API works.") }

            post("/api/spots/{id}/occupy") {
                call.handleAction { id, licensePlate -> repository.occupy(id, licensePlate) }
            }

            post("/api/spots/{id}/release") {
                call.handleAction { id, licensePlate -> repository.release(id, licensePlate) }
            }

            webSocket("/ws/spots") {
                repository.spots.collect { spots -> sendSerialized(spots) }
            }
        }
    }.start(wait = true) // stay and listen
}

/* Shared part of both POST routes: read the id and the license plate, run the action, send the answer. */
private suspend fun ApplicationCall.handleAction(action: (id: Int, licensePlate: String) -> ActionResult) {
    val id = parameters["id"]?.toIntOrNull()
        ?: return respondText("Invalid id", status = HttpStatusCode.BadRequest)
    val request = receive<LicensePlateRequest>()
    if (!request.licensePlate.isValidLicensePlate()) {
        return respondText("Invalid license plate: ${request.licensePlate}", status = HttpStatusCode.BadRequest)
    }
    respondResult(action(id, request.licensePlate))
}

/* when over a sealed type: the compiler checks that we handled every outcome. */
private suspend fun ApplicationCall.respondResult(result: ActionResult) = when (result) {
    is ActionResult.Success -> respond(result.spot)
    is ActionResult.NotFound -> respondText("Spot ${result.id} does not exist", status = HttpStatusCode.NotFound)
    is ActionResult.Conflict -> respondText(result.reason, status = HttpStatusCode.Conflict)
}
