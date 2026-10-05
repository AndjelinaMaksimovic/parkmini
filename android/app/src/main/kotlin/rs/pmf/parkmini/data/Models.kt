// A copy of the backend models (in a real project: DTO classes that match the JSON of the server).
package rs.pmf.parkmini.data

import kotlinx.serialization.Serializable

@Serializable
enum class SpotStatus { FREE, RESERVED, OCCUPIED, UNAVAILABLE }

@Serializable
data class ParkingSpot(
    val id: Int,
    val label: String,
    val zone: String,
    val status: SpotStatus = SpotStatus.FREE,
    val reservedBy: String? = null,
    val reservedUntil: Long? = null, // epoch millis
)

@Serializable
data class DriverRequest(val driver: String)

@Serializable
data class Stats(
    val total: Int,
    val free: Int,
    val reserved: Int,
    val occupied: Int,
    val unavailable: Int,
    val occupancyPercent: Int,
)

fun List<ParkingSpot>.toStats(): Stats {
    val count = groupingBy { it.status }.eachCount()
    val busy = (count[SpotStatus.RESERVED] ?: 0) + (count[SpotStatus.OCCUPIED] ?: 0)
    return Stats(
        total = size,
        free = count[SpotStatus.FREE] ?: 0,
        reserved = count[SpotStatus.RESERVED] ?: 0,
        occupied = count[SpotStatus.OCCUPIED] ?: 0,
        unavailable = count[SpotStatus.UNAVAILABLE] ?: 0,
        occupancyPercent = if (isEmpty()) 0 else busy * 100 / size,
    )
}
