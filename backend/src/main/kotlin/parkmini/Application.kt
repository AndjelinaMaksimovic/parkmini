package com.v1.parkmini

import com.v1.parkmini.models.sampleSpots
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
    val spots = sampleSpots(occupiedShare = 0.4)

    // starting a server inside our project
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() } // no handwriting of JSON

        routing {
            get("/") { call.respondText("ParkMini API works.") }

            get("/api/spots") { call.respond(spots) }
        }
    }.start(wait = true) // stay and listen
}
