package com.v1.parkmini.models.enums

import kotlinx.serialization.Serializable

// The 4 possible statuses of a parking spot.
@Serializable
enum class SpotStatus {
    FREE,
    RESERVED,
    OCCUPIED,
    UNAVAILABLE
}