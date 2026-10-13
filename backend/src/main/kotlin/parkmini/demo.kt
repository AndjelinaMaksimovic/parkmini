package com.v1.parkmini

import com.v1.parkmini.models.ParkingSpot
import com.v1.parkmini.models.enums.SpotStatus
import com.v1.parkmini.models.enums.Zone
import com.v1.parkmini.models.sampleSpots
import com.v1.parkmini.models.toStats

// trying out the model
fun main() {
    task1()
    task2()
}

private fun task1() {
    println("\n-------------- TASK 1 --------------")
    val spot = ParkingSpot(id = 1, label = "A1", zone = Zone.A)
    val reserved = spot.copy(status = SpotStatus.RESERVED, reservedBy = "ana")

    println("original: $spot")
    println("copy:     $reserved")
    println(SpotStatus.entries)   // all values of enum SpotStatus
}


private fun task2() {
    println("\n-------------- TASK 2 --------------")
    val spots = sampleSpots()
    println(spots.size)
    println(spots.map { it.label })
    println(spots.last())
    println(spots.count { it.status == SpotStatus.UNAVAILABLE })

    println("statistics, nothing occupied: ${spots.toStats()}")
    println("statistics, about 40% occupied: ${sampleSpots(occupiedShare = 0.4).toStats()}")
}