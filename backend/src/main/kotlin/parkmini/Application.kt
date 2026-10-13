package com.v1.parkmini

import com.v1.parkmini.models.enums.SpotStatus
import com.v1.parkmini.models.sampleSpots
import com.v1.parkmini.models.toStats
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
import com.v1.parkmini.models.DriverRequest
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

            /*
             * http://localhost:8080/api/spots?status=free → samo slobodna mesta (oko 10)
             * http://localhost:8080/api/spots?status=OCCUPIED → samo zauzeta
             * http://localhost:8080/api/spots?status=xyz → tekst Unknown status: xyz
             * http://localhost:8080/api/spots → svih 18
             * */
            get("/api/spots") {
                val statusParam = call.request.queryParameters["status"]
                if (statusParam == null) {
                    call.respond(repository.all())
                    return@get
                }
                val wanted = SpotStatus.entries.find { it.name.equals(statusParam, ignoreCase = true) }
                if (wanted == null) {
                    call.respondText("Unknown status: $statusParam", status = HttpStatusCode.BadRequest)
                } else {
                    call.respond(repository.all().filter { it.status == wanted })
                }
            }

            get("/api/spots/stats") { call.respond(repository.all().toStats()) }

            /*
             * http://localhost:8080/api/spots/5 → mesto A5
             * http://localhost:8080/api/spots/999 → Spot 999 does not exist
             * http://localhost:8080/api/spots/abc → Invalid id
             */
            get("/api/spots/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                    return@get
                }
                val spot = repository.find(id)
                if (spot == null) {
                    call.respondText("Spot $id does not exist", status = HttpStatusCode.NotFound)
                } else {
                    call.respond(spot)
                }
            }

            post("/api/spots/{id}/reserve") {
                call.handleAction { id, driver -> repository.reserve(id, driver) }
            }

            post("/api/spots/{id}/confirm") {
                call.handleAction { id, driver -> repository.confirmArrival(id, driver) }
            }

            post("/api/spots/{id}/release") {
                call.handleAction { id, driver -> repository.release(id, driver) }
            }

            webSocket("/ws/spots") {
                repository.spots.collect { spots -> sendSerialized(spots) }
            }
        }
    }.start(wait = true) // stay and listen
}

/* Shared part of all three POST routes: read the id and the driver, run the action, send the answer. */
private suspend fun ApplicationCall.handleAction(action: (id: Int, driver: String) -> ActionResult) {
    val id = parameters["id"]?.toIntOrNull()
        ?: return respondText("Invalid id", status = HttpStatusCode.BadRequest)
    val request = receive<DriverRequest>()
    respondResult(action(id, request.driver))
}

/* when over a sealed type: the compiler checks that we handled every outcome. */
private suspend fun ApplicationCall.respondResult(result: ActionResult) = when (result) {
    is ActionResult.Success -> respond(result.spot)
    is ActionResult.NotFound -> respondText("Spot ${result.id} does not exist", status = HttpStatusCode.NotFound)
    is ActionResult.Conflict -> respondText(result.reason, status = HttpStatusCode.Conflict)
}