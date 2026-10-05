package com.v1.parkmini

import com.v1.parkmini.models.ParkingSpot

/** Every action on a spot ends in exactly one of these three outcomes. */
sealed interface ActionResult {
    data class Success(val spot: ParkingSpot) : ActionResult
    data class NotFound(val id: Int) : ActionResult
    data class Conflict(val reason: String) : ActionResult
}