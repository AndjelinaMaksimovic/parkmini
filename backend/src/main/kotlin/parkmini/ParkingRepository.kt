package com.v1.parkmini

import com.v1.parkmini.models.ParkingSpot
import com.v1.parkmini.models.enums.SpotStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/* Task 4:
* Start by creating a class ParkingRepository that holds the list of spots.
* It offers:
*   - getting all spots
*   - finding one spot by its id (the result is empty if the spot does not exist)
*   - a way for other parts of the server to follow the list: whenever a spot changes,
* they are given the new list

* The routes then ask the repository instead of touching the list directly,
* and the existing endpoints behave exactly as before.
*
* Then a driver can reserve a free spot. The reservation is valid for 10 minutes.
*/
class ParkingRepository(initial: List<ParkingSpot>) {

    private val _spots = MutableStateFlow(initial)
    val spots: StateFlow<List<ParkingSpot>> = _spots.asStateFlow()

    @Synchronized
    fun all(): List<ParkingSpot> = _spots.value

    @Synchronized
    fun find(id: Int): ParkingSpot? = _spots.value.find { it.id == id }

    @Synchronized
    fun reserve(id: Int, driver: String): ActionResult {
        val spot = find(id) ?: return ActionResult.NotFound(id)
        if (spot.status != SpotStatus.FREE) {
            return ActionResult.Conflict("Spot ${spot.label} is not free (${spot.status})")
        }
        val reserved = spot.copy(
            status = SpotStatus.RESERVED,
            reservedBy = driver,
            reservedUntil = Clock.System.now() + reservationDuration,
        )
        replace(reserved)
        return ActionResult.Success(reserved)
    }

    private fun replace(updated: ParkingSpot) {
        _spots.value = _spots.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    // like static final in Java
    companion object {
        val reservationDuration = 10.minutes
    }


    @Synchronized
    fun confirmArrival(id: Int, driver: String): ActionResult {
        val spot = find(id) ?: return ActionResult.NotFound(id)
        return when {
            spot.status != SpotStatus.RESERVED -> ActionResult.Conflict("Spot ${spot.label} is not reserved")
            spot.reservedBy != driver -> ActionResult.Conflict("Spot is reserved by another driver")
            else -> {
                val occupied = spot.copy(status = SpotStatus.OCCUPIED, reservedUntil = null)
                replace(occupied)
                ActionResult.Success(occupied)
            }
        }
    }


    // Homework
    /*
    * The driver who reserved a spot can:
    *   - confirm arrival (the spot becomes OCCUPIED) or
    *   - cancel the reservation / leave the spot (the spot becomes FREE).
    * Nobody else can do this.
    * A reservation that is older than 10 minutes is canceled automatically.
    */
    @Synchronized
    fun release(id: Int, driver: String): ActionResult {
        val spot = find(id) ?: return ActionResult.NotFound(id)
        if (spot.reservedBy != driver) {
            return ActionResult.Conflict("Spot ${spot.label} is not held by $driver")
        }
        val free = spot.copy(status = SpotStatus.FREE, reservedBy = null, reservedUntil = null)
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
    * The sensors never touch an unavailable spot, or a spot that is reserved or occupied by a driver of the app.
    *
    * A reservation that is older than 10 minutes is canceled automatically.
    * */
    @Synchronized
    fun simulateSensor() {
        val usable = _spots.value.filter { it.status != SpotStatus.UNAVAILABLE }
        val busy = usable.count { it.status != SpotStatus.FREE }
        val ratio = busy.toDouble() / usable.size

        val shouldOccupy = when {
            ratio < 0.2 -> true      // too empty: a car arrives
            ratio > 0.7 -> false     // too full: a car leaves
            else -> Random.nextBoolean()
        }
        val candidate =
            if (shouldOccupy) {
                usable.filter { it.status == SpotStatus.FREE }.randomOrNull()
            } else {
                usable.filter { it.status == SpotStatus.OCCUPIED && it.reservedBy == null }.randomOrNull()
            }
        if (candidate != null) {
            replace(
                candidate.copy(
                    status =
                        if (shouldOccupy) SpotStatus.OCCUPIED else SpotStatus.FREE
                )
            )
        }
    }

    @Synchronized
    fun releaseExpired(clock: Clock) {
        for (spot in _spots.value.toList()) {
            if (spot.status == SpotStatus.RESERVED && (spot.reservedUntil ?: Instant.DISTANT_FUTURE) < clock.now()) {
                replace(spot.copy(status = SpotStatus.FREE, reservedBy = null, reservedUntil = null))
            }
        }
    }
}