package rs.pmf.parkmini.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import rs.pmf.parkmini.data.SpotStatus

@Composable
fun ParkMiniTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(primary = Color(0xFF1565C0)),
        content = content,
    )
}

// Extension properties: we add behaviour to a class we did not write, without inheritance.
val SpotStatus.color: Color
    get() = when (this) {
        SpotStatus.FREE -> Color(0xFF43A047)
        SpotStatus.RESERVED -> Color(0xFFF9A825)
        SpotStatus.OCCUPIED -> Color(0xFFE53935)
        SpotStatus.UNAVAILABLE -> Color(0xFF1E88E5)
    }

val SpotStatus.title: String
    get() = when (this) {
        SpotStatus.FREE -> "Free"
        SpotStatus.RESERVED -> "Reserved"
        SpotStatus.OCCUPIED -> "Occupied"
        SpotStatus.UNAVAILABLE -> "Unavailable"
    }
