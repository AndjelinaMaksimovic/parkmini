package com.v1.parkmini.models

import kotlinx.serialization.Serializable

/** Body of a reservation request: {"driver": "ana"} */
@Serializable
data class DriverRequest(val driver: String)