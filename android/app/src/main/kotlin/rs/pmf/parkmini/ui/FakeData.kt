package rs.pmf.parkmini.ui

import rs.pmf.parkmini.data.ParkingSpot
import rs.pmf.parkmini.data.SpotStatus

/** Fake data for class 4. In class 5 the real server takes its place. */
fun fakeSpots(): List<ParkingSpot> = buildList {
    var id = 1
    for (zone in listOf("A", "B", "C")) {
        for (number in 1..6) {
            add(ParkingSpot(id = id, label = "$zone$number", zone = zone, status = SpotStatus.entries[id % 4]))
            id++
        }
    }
}
