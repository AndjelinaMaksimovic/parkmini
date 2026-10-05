package com.v1.parkmini

import com.v1.parkmini.models.ParkingSpot
import com.v1.parkmini.models.enums.SpotStatus
import com.v1.parkmini.models.enums.Zone
import com.v1.parkmini.models.sampleSpots
import com.v1.parkmini.models.toStats

// trying out the model
fun main(){
    // Task 1 test
    println("\n-------------- TASK 1 --------------")
    val spot = ParkingSpot(id = 1, label = "A1", zone = Zone.A)
    println(spot)

    val reserved = spot.copy(status = SpotStatus.RESERVED, reservedBy = "ana")
    println(reserved)
    println(SpotStatus.entries)   // all values of enum SpotStatus


    // Task 2 test
    println("\n-------------- TASK 2 --------------")
    val spots = sampleSpots()
    println(spots.size)
    println(spots.map { it.label })
    println(spots.last())
    println(spots.count { it.status == SpotStatus.UNAVAILABLE })

    val spots2 = sampleSpots(occupiedShare = 0.4)
    println(spots2.toStats())
    println(sampleSpots().toStats())
}