package com.v1.parkmini.shared

import kotlinx.serialization.Serializable

/*
 * Task 1:
 * Every parking spot has:
 *   - an id
 *   - a zone A, B, or C
 *   - a label (e.g., A3)
 *   - a status: FREE, OCCUPIED (a car with a known license plate is parked there) or UNAVAILABLE
 * A spot is FREE by default.
 */
@Serializable
data class ParkingSpot(
    val id: Int,
    val label: String,
    val zone: Zone,
    val status: SpotStatus = SpotStatus.Free,
)
