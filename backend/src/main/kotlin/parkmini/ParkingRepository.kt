package com.v1.parkmini

import com.v1.parkmini.models.SIMULATED_PREFIX
import com.v1.parkmini.models.randomSimulatedPlate
import com.v1.parkmini.shared.ParkingSpot
import com.v1.parkmini.shared.SpotStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/* Task 4:
* Start by creating a class ParkingRepository that holds the list of spots.
* It offers:
*   - a way for other parts of the server to follow the list: whenever a spot changes,
*     they are given the new list
*
* Then a driver can park in a free spot (the spot becomes OCCUPIED by the driver's license plate)
* and, later, leave the spot (it becomes FREE again). Nobody else can free that spot.
*/
// SECURITY: this is a simplified example, so these problems are known and left on purpose:
//  - no authentication: the license plate in the request is trusted, so anyone can send anyone's plate and free that spot;
//  - every client receives every license plate (personal data);
//  - the list lives only in memory: when the server restarts, everything is lost (a real system needs a database).
class ParkingRepository(initial: List<ParkingSpot>) {

    private val _spots = MutableStateFlow(initial)
    val spots: StateFlow<List<ParkingSpot>> = _spots.asStateFlow()

    private fun find(id: Int): ParkingSpot? = _spots.value.find { it.id == id }

    // @Synchronized: "check, then change" must not run twice at the same time (two drivers, one spot)
    @Synchronized
    fun occupy(id: Int, licensePlate: String): ActionResult {
        val spot = find(id) ?: return ActionResult.NotFound(id)
        if (spot.status !is SpotStatus.Free) {
            return ActionResult.Conflict("Spot ${spot.label} is not free")
        }
        val occupied = spot.copy(status = SpotStatus.Occupied(licensePlate))
        replace(occupied)
        return ActionResult.Success(occupied)
    }

    @Synchronized
    fun release(id: Int, licensePlate: String): ActionResult {
        val spot = find(id) ?: return ActionResult.NotFound(id)
        val status = spot.status
        if (status !is SpotStatus.Occupied || status.licensePlate != licensePlate) {
            return ActionResult.Conflict("Spot ${spot.label} is not occupied by $licensePlate")
        }
        val free = spot.copy(status = SpotStatus.Free)
        replace(free)
        return ActionResult.Success(free)
    }

    /*
    * The server changes the state of the parking lot by itself, as if real sensors were installed.
    * Every 2 to 6 seconds, one spot changes:
    * 	- a free spot becomes OCCUPIED (a car arrives) or
    *   - an occupied spot becomes FREE (a car leaves).
    *
    * The sensors keep the occupancy between 20% and 70%:
    * when the lot is too empty, cars arrive; when it is too full, cars leave.
    * The sensors never touch an unavailable spot, or a spot occupied by a driver of the app.
    * */
    @Synchronized
    fun simulateSensor() {
        val usable = _spots.value.filter { it.status !is SpotStatus.Unavailable }
        val busy = usable.count { it.status is SpotStatus.Occupied }
        val ratio = busy.toDouble() / usable.size

        val shouldOccupy = when {
            ratio < 0.2 -> true      // too empty: a car arrives
            ratio > 0.7 -> false     // too full: a car leaves
            else -> Random.nextBoolean()
        }
        val candidate =
            if (shouldOccupy) {
                usable.filter { it.status is SpotStatus.Free }.randomOrNull()
            } else {
                usable.filter { it.isSimulatedCar() }.randomOrNull()
            }
        if (candidate != null) {
            val newStatus = if (shouldOccupy) SpotStatus.Occupied(randomSimulatedPlate()) else SpotStatus.Free
            replace(candidate.copy(status = newStatus))
        }
    }

    // the cars of the sensors have a plate like SIM-123, the drivers of the app never do
    private fun ParkingSpot.isSimulatedCar(): Boolean {
        val status = status
        return status is SpotStatus.Occupied && status.licensePlate.startsWith(SIMULATED_PREFIX)
    }

    private fun replace(updated: ParkingSpot) {
        _spots.value = _spots.value.map {
            if (it.id == updated.id) updated else it
        }
    }
}
