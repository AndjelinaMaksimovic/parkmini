package com.v1.parkmini

import com.v1.parkmini.models.ParkingSpot
import com.v1.parkmini.models.enums.SpotStatus
import kotlin.random.Random

// explain primary constructor with decompile to java
/**
 * The list of parking spots lives in one class, ParkingRepository, which is created once when the server starts.
 * The class offers two read operations: get all spots, and find one spot by its id (the result is empty if the spot does not exist).
 */
class ParkingRepository(initial: List<ParkingSpot>) {

    private val spots = initial.toMutableList()

    @Synchronized
    fun all(): List<ParkingSpot> = spots.toList()

    @Synchronized
    fun find(id: Int): ParkingSpot? = spots.find { it.id == id }

    // Task 6 and 7
    @Synchronized
    fun reserve(id: Int, driver: String): ActionResult {
        val spot = find(id) ?: return ActionResult.NotFound(id)
        if (spot.status != SpotStatus.FREE) {
            return ActionResult.Conflict("Spot ${spot.label} is not free (${spot.status})")
        }
        val reserved = spot.copy(
            status = SpotStatus.RESERVED,
            reservedBy = driver,
            reservedUntil = System.currentTimeMillis() + RESERVATION_MILLIS,
        )
        replace(reserved)
        return ActionResult.Success(reserved)
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

    /** Cancel a reservation or leave the spot: only the driver who holds the spot can do it. */
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

    // Task 5: the sensors. Several threads (requests + simulator) now change the list, so every public function is @Synchronized.
    @Synchronized
    fun simulateSensor() {
        val usable = spots.filter { it.status != SpotStatus.UNAVAILABLE }
        val busy = usable.count { it.status != SpotStatus.FREE }
        val ratio = busy.toDouble() / usable.size

        val shouldOccupy = when {
            ratio < 0.2 -> true      // too empty: a car arrives
            ratio > 0.7 -> false     // too full: a car leaves
            else -> Random.nextBoolean()
        }
        val candidate = if (shouldOccupy) {
            usable.filter { it.status == SpotStatus.FREE }.randomOrNull()
        } else {
            usable.filter { it.status == SpotStatus.OCCUPIED && it.reservedBy == null }.randomOrNull()
        }
        if (candidate != null) {
            replace(candidate.copy(status = if (shouldOccupy) SpotStatus.OCCUPIED else SpotStatus.FREE))
        }
    }

    /** Ready code: a reservation that is older than 10 minutes is cancelled. */
    @Synchronized
    fun releaseExpired(now: Long = System.currentTimeMillis()) {
        for (spot in spots.toList()) {
            if (spot.status == SpotStatus.RESERVED && (spot.reservedUntil ?: Long.MAX_VALUE) < now) {
                replace(spot.copy(status = SpotStatus.FREE, reservedBy = null, reservedUntil = null))
            }
        }
    }

    private fun replace(updated: ParkingSpot) {
        val index = spots.indexOfFirst { it.id == updated.id }
        spots[index] = updated
    }

    companion object {
        const val RESERVATION_MILLIS = 10 * 60 * 1000L   // 10 minutes
    }
}