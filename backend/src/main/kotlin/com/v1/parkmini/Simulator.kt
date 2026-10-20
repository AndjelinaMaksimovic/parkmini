package com.v1.parkmini

import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

// The "sensors": a coroutine that keeps changing the state of the parking spots in the background.
suspend fun sensorSimulator(repository: ParkingRepository): Nothing {
    while (true) {
        delay(Random.nextDouble(2.0, 6.0).seconds) // wait 2-6 seconds without blocking a thread
        repository.simulateSensor()
    }
}
