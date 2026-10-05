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


// Task 3
fun main() {
    val repository = ParkingRepository(sampleSpots(occupiedShare = 0.4))

    // starting a server inside our project
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() } // no handwriting of JSON

        routing {
            get("/") { call.respondText("ParkMini API works.") }

            /**
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

            /**
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
        }
    }.start(wait = true) // stay and listen
}
