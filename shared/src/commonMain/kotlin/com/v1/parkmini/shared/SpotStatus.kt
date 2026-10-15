package com.v1.parkmini.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * The status of a parking spot is one of three cases, and only an occupied spot has a license plate.
 * A sealed type cannot describe a free spot with a license plate, or an occupied spot without one.
 */
@Serializable
sealed interface SpotStatus {

    @Serializable
    @SerialName("free")
    data object Free : SpotStatus

    @Serializable
    @SerialName("occupied")
    data class Occupied(val licensePlate: String) : SpotStatus

    @Serializable
    @SerialName("unavailable")
    data object Unavailable : SpotStatus
}
