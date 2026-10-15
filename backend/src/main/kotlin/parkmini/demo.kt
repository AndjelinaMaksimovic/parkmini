package com.v1.parkmini

import com.v1.parkmini.models.sampleSpots
import com.v1.parkmini.shared.ParkingSpot
import com.v1.parkmini.shared.SpotStatus
import com.v1.parkmini.shared.Zone

// trying out the model
fun main() {
    task1()
    task2()
}

private fun task1() {
    println("\n-------------- TASK 1 --------------")
    val spot = ParkingSpot(id = 1, label = "A1", zone = Zone.A)
    val occupied = spot.copy(status = SpotStatus.Occupied("KG-123-AB"))

    println("original: $spot")
    println("copy:     $occupied")
}


private fun task2() {
    println("\n-------------- TASK 2 --------------")
    val spots = sampleSpots()
    println(spots.size)
    println(spots.map { it.label })
    println(spots.last())
    println(spots.count { it.status is SpotStatus.Unavailable })

    val busy = sampleSpots(occupiedShare = 0.4)
    println("about 40% occupied: ${busy.count { it.status is SpotStatus.Occupied }} of ${busy.size}")
}
