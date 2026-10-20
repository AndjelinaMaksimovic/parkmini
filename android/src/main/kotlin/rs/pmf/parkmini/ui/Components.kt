package rs.pmf.parkmini.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v1.parkmini.shared.ParkingSpot
import com.v1.parkmini.shared.SpotStatus
import com.v1.parkmini.shared.Zone

// Task 7: the tile (typed). Task 8 adds onClick, Task 10 adds isMine.
@Composable
fun SpotTile(
    spot: ParkingSpot,
    isMine: Boolean,
    onClick: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = spot.status.color),
        border = if (isMine) BorderStroke(3.dp, Color.Black) else null,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = spot.status is SpotStatus.Free || isMine, onClick = onClick),
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

// Task 10: does this spot hold my car?
fun ParkingSpot.isOccupiedBy(licensePlate: String): Boolean {
    val status = status
    return licensePlate.isNotBlank() && status is SpotStatus.Occupied && status.licensePlate == licensePlate
}

// Optional | GIVEN (ready code)
// ---- Preview: see the component without running an emulator ----

@Preview(showBackground = true)
@Composable
private fun SpotTilePreview() {
    ParkMiniTheme {
        val statuses = listOf(SpotStatus.Free, SpotStatus.Occupied("KG-123-AB"), SpotStatus.Unavailable)
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            for ((index, status) in statuses.withIndex()) {
                Box(Modifier.size(96.dp)) {
                    SpotTile(ParkingSpot(index, "A${index + 1}", Zone.A, status), isMine = false, onClick = {})
                }
            }
        }
    }
}
