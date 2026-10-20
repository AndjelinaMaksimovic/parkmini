package com.v1.parkmini.models

import com.v1.parkmini.shared.ParkingSpot
import com.v1.parkmini.shared.SpotStatus
import com.v1.parkmini.shared.Zone
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
                zone == Zone.C && num == 6 -> SpotStatus.Unavailable
                Random.nextDouble() < occupiedShare -> SpotStatus.Occupied(randomSimulatedPlate())
                else -> SpotStatus.Free
            }
            spots.add(ParkingSpot(id, "$zone$num", zone, status))
            id++
        }
    }
    return spots
}

// The cars that "arrive" in the simulation get a plate like SIM-123. A real driver cannot type it (see isValidLicensePlate),
// so the sensors can tell their cars from the cars of the drivers who use the app.
const val SIMULATED_PREFIX = "SIM-"

fun randomSimulatedPlate(): String = "$SIMULATED_PREFIX${Random.nextInt(100, 1000)}"
