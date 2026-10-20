package rs.pmf.parkmini.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v1.parkmini.shared.Stats

// Task 8 | GIVEN (ready code) (isOffline is used in Task 9, licensePlate in Task 10)
@Composable
fun StatsHeader(stats: Stats, isOffline: Boolean, licensePlate: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Free: ${stats.free} / ${stats.total}", style = MaterialTheme.typography.headlineSmall)
            LinearProgressIndicator(
                progress = { stats.occupancyPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            val you = if (licensePlate.isBlank()) "" else "  •  You: $licensePlate"
            Text("Occupancy: ${stats.occupancyPercent}%$you", style = MaterialTheme.typography.bodySmall)
            if (isOffline) {
                Text("No connection to the server, showing the last known state", color = MaterialTheme.colorScheme.error)
            }
            Legend()
        }
    }
}

@Composable
private fun Legend() {
    val items = listOf("Free" to FreeColor, "Occupied" to OccupiedColor, "Unavailable" to UnavailableColor)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        for ((title, color) in items) {
            Box(Modifier.size(10.dp).background(color, CircleShape))
            Text(title, fontSize = 11.sp)
        }
    }
}
