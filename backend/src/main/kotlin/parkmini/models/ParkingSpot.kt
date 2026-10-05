package com.v1.parkmini.models

import com.v1.parkmini.models.enums.SpotStatus
import com.v1.parkmini.models.enums.Zone
import kotlinx.serialization.Serializable

/** Every parking spot has an:
    - id
    - zone (e.g. A)
    - label (e.g. A3)
    - one of 4 possible statuses (FREE, RESERVED, OCCUPIED, UNAVAILABLE)
A spot is FREE by default.
A spot may also store who reserved it and until when; otherwise, these two values are empty.
 **/
@Serializable
data class ParkingSpot(
    val id: Int,
    val label: String,
    val zone: Zone,
    val status: SpotStatus = SpotStatus.FREE,
    val reservedBy: String? = null,
    val reservedUntil: Long? = null, // epoch millis
)