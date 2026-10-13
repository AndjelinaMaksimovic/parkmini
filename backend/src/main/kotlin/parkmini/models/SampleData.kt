package com.v1.parkmini.models

import com.v1.parkmini.models.enums.SpotStatus
import com.v1.parkmini.models.enums.Zone
import kotlin.random.Random

/* Task 2:
* The parking lot has
*	- 18 spots
*	- 3 zones (A, B, C)
*	- 6 spots per zone, labeled A1-A6, B1-B6, and C1-C6
*	- spot C6 is unavailable
* When the server starts, about 40% of the other spots are already OCCUPIED, picked at random.
*/
fun sampleSpots(occupiedShare: Double = 0.0): List<ParkingSpot> {
    val spots = mutableListOf<ParkingSpot>()
    var id = 1
    for (zone in Zone.entries) {
        for (num in 1..6) {
            val status = when {
                zone == Zone.C && num == 6 -> SpotStatus.UNAVAILABLE
                Random.nextDouble() < occupiedShare -> SpotStatus.OCCUPIED
                else -> SpotStatus.FREE
            }
            spots.add(ParkingSpot(id, "$zone$num", zone, status))
            id++
        }
    }
    return spots
}

// If I have enough time show shuffle with take:
fun sampleSpots2(occupiedShare: Double = 0.0): List<ParkingSpot> {
    val spots = mutableListOf<ParkingSpot>()
    var id = 1
    for (zone in Zone.entries) {
        for (num in 1..6) {
            val status = if (zone == Zone.C && num == 6) SpotStatus.UNAVAILABLE
            else SpotStatus.FREE
            spots.add(ParkingSpot(id, "$zone$num", zone, status))
            id++
        }
    }
    // 1. candidates: spots that can become occupied (every one of them without C6)
    val candidates = spots.filter { it.status == SpotStatus.FREE }

    // 2. how many spots should be taken
    val howMany = (spots.size * occupiedShare).toInt()

    // 3. shuffle candidates and take first howMany
    val chosen = candidates.shuffled().take(howMany)

    // 4. we exchange chosen spots with occupied copies, the rest of them stay the same
    return spots.map { if (it in chosen) it.copy(status = SpotStatus.OCCUPIED) else it }
    return spots
}