package rs.pmf.parkmini.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.pmf.parkmini.data.ParkingSpot
import rs.pmf.parkmini.data.SpotStatus
import rs.pmf.parkmini.data.Stats

@Composable
fun StatsHeader(stats: Stats, isOffline: Boolean, driver: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Free: ${stats.free} / ${stats.total}", style = MaterialTheme.typography.headlineSmall)
            LinearProgressIndicator(
                progress = { stats.occupancyPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Occupancy: ${stats.occupancyPercent}%  •  You are: $driver", style = MaterialTheme.typography.bodySmall)
            if (isOffline) {
                Text("No connection to the server, showing the last known state", color = MaterialTheme.colorScheme.error)
            }
            Legend()
        }
    }
}

@Composable
private fun Legend() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        for (status in SpotStatus.entries) {
            Box(Modifier.size(10.dp).background(status.color, CircleShape))
            Text(status.title, fontSize = 11.sp)
        }
    }
}

@Composable
fun SpotTile(
    spot: ParkingSpot,
    isMine: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = spot.status == SpotStatus.FREE || isMine,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = spot.status.color),
        border = if (isMine) BorderStroke(3.dp, Color.Black) else null,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = enabled && spot.status != SpotStatus.UNAVAILABLE, onClick = onClick),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(spot.label, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(if (isMine) "YOU" else spot.status.title, color = Color.White, fontSize = 12.sp)
        }
    }
}

// ---- Preview: see the component without running an emulator ----

@Preview(showBackground = true)
@Composable
private fun SpotTilePreview() {
    ParkMiniTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            for ((index, status) in SpotStatus.entries.withIndex()) {
                Box(Modifier.size(96.dp)) {
                    SpotTile(ParkingSpot(index, "A${index + 1}", "A", status), isMine = false, onClick = {})
                }
            }
        }
    }
}
