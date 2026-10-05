package com.v1.parkmini

import com.v1.parkmini.models.ParkingSpot

/**
 * The list of parking spots lives in one class, ParkingRepository, which is created once when the server starts.
 * The class offers two read operations: get all spots, and find one spot by its id (the result is empty if the spot does not exist).
 */
class ParkingRepository(initial: List<ParkingSpot>) {

    private val spots = initial.toMutableList()

    fun all(): List<ParkingSpot> = spots.toList()

    fun find(id: Int): ParkingSpot? = spots.find { it.id == id }
}
