package com.v1.parkmini.shared

import kotlinx.serialization.Serializable

/** The three zones of the parking lot. */
@Serializable
enum class Zone {
    A,
    B,
    C
}
