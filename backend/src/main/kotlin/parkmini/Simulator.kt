package com.v1.parkmini

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/** The "sensors": a coroutine that keeps changing the state of the parking spots in the background. */
fun CoroutineScope.launchSensorSimulator(repository: ParkingRepository): Job = launch {
    while (isActive) {
        delay(Random.nextLong(2_000, 6_000))    // wait 2-6 seconds without blocking a thread
        repository.simulateSensor()
        repository.releaseExpired()
    }
}
