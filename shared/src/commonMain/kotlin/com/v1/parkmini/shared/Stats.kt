package com.v1.parkmini.shared

/*
 * The statistics are calculated in the app from the list of spots, so the server needs no extra endpoint
 * and the numbers can never disagree with the list.
 */
data class Stats(
    val total: Int,
    val free: Int,
    val occupied: Int,
    val unavailable: Int,
    val occupancyPercent: Int,
)

fun List<ParkingSpot>.toStats(): Stats {
    val occupied = count { it.status is SpotStatus.Occupied }
    return Stats(
        total = size,
        free = count { it.status is SpotStatus.Free },
        occupied = occupied,
        unavailable = count { it.status is SpotStatus.Unavailable },
        occupancyPercent = if (isEmpty()) 0 else occupied * 100 / size,
    )
}
