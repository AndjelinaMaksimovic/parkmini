package rs.pmf.parkmini.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rs.pmf.parkmini.data.SpotStatus
import rs.pmf.parkmini.data.toStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen() {
    // State: when it changes, Compose redraws everything that uses it.
    var spots by remember { mutableStateOf(fakeSpots()) }

    Scaffold(topBar = { TopAppBar(title = { Text("ParkMini") }) }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                StatsHeader(spots.toStats(), isOffline = false, driver = "you")
            }
            for ((zone, zoneSpots) in spots.groupBy { it.zone }) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Zone $zone", style = MaterialTheme.typography.titleMedium)
                }
                items(zoneSpots, key = { it.id }) { spot ->
                    SpotTile(
                        spot = spot,
                        isMine = false,
                        enabled = true,
                        // Only for practice: a click moves the spot to the next status, locally.
                        onClick = { spots = spots.map { if (it.id == spot.id) it.copy(status = spot.status.next()) else it } },
                    )
                }
            }
        }
    }
}

private fun SpotStatus.next() = SpotStatus.entries[(ordinal + 1) % SpotStatus.entries.size]
