package com.v1.parkmini.models

import com.v1.parkmini.models.enums.SpotStatus
import kotlinx.serialization.Serializable

/* Task 2:
* For any list of spots, we can compute statistics:
*	- the total number of spots
*	- the number of spots in each status
*	- occupancy percentage (reserved + occupied spots as a percentage of all spots)
*/
@Serializable
data class Stats(
    val total: Int,
    val free: Int,
    val reserved: Int,
    val occupied: Int,
    val unavailable: Int,
    val occupancyPercent: Int
)

// Extension function show of - adding toStats() to an existing class List, without inheritance
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

/*
// V1 - "traditional" way
var free = 0
var reserved = 0
var occupied = 0
var unavailable = 0
for (spot in this) {
    if (spot.status == SpotStatus.FREE) {
        free++
    } else if (spot.status == SpotStatus.RESERVED) {
        reserved++
    } else if (spot.status == SpotStatus.OCCUPIED) {
        occupied++
    } else if (spot.status == SpotStatus.UNAVAILABLE) {
        unavailable++
    }
}
val busy = reserved + occupied
val percent = if (this.isEmpty()) 0 else busy * 100 / this.size
return Stats(this.size, free, reserved, occupied, unavailable, percent )

// V2 - V1 simplified with when
for (spot in this) {
    when (spot.status) {
        SpotStatus.FREE -> free++
        SpotStatus.RESERVED -> reserved++
        SpotStatus.OCCUPIED -> occupied++
        SpotStatus.UNAVAILABLE -> unavailable++
    }
}

//V3 without loop and counter -> count{}
free = this.count{ it.status == SpotStatus.FREE }
reserved = this.count{ it.status == SpotStatus.RESERVED }
occupied = this.count{ it.status == SpotStatus.OCCUPIED }
unavailable = this.count{ it.status == SpotStatus.UNAVAILABLE }
*/