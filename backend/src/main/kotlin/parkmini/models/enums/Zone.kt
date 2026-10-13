package com.v1.parkmini.models.enums

import kotlinx.serialization.Serializable

/** The three zones of the parking lot. */
@Serializable
enum class Zone {
    A,
    B,
    C
}