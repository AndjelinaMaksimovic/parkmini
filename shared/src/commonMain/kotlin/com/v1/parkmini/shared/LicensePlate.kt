package com.v1.parkmini.shared

import kotlinx.serialization.Serializable

/** Body of the occupy and release requests: {"licensePlate": "KG-123-AB"} */
@Serializable
data class LicensePlateRequest(val licensePlate: String)

// Two letters, three or four digits, two letters (e.g. KG-123-AB). The server and the app use the same rule.
private val licensePlateFormat = Regex("[A-Z]{2}-\\d{3,4}-[A-Z]{2}")

fun String.isValidLicensePlate(): Boolean = licensePlateFormat.matches(this)
