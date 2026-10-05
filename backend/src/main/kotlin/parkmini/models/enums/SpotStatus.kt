package com.v1.parkmini.models.enums

import kotlinx.serialization.Serializable

/** 4 possible statuses**/
@Serializable
enum class SpotStatus {
    FREE,
    RESERVED,
    OCCUPIED,
    UNAVAILABLE
}