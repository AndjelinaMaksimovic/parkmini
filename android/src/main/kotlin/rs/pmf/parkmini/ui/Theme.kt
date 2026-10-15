package rs.pmf.parkmini.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.v1.parkmini.shared.SpotStatus

// Task 7 | GIVEN (ready code)
@Composable
fun ParkMiniTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(primary = Color(0xFF1565C0)),
        content = content,
    )
}

// Task 7 | GIVEN (ready code)
val FreeColor = Color(0xFF43A047)
val OccupiedColor = Color(0xFFE53935)
val UnavailableColor = Color(0xFF1E88E5)

// Extension properties: we add behaviour to a class we did not write, without inheritance.
// when over a sealed type: the compiler checks that every case is handled.
val SpotStatus.color: Color
    get() = when (this) {
        SpotStatus.Free -> FreeColor
        is SpotStatus.Occupied -> OccupiedColor
        SpotStatus.Unavailable -> UnavailableColor
    }

val SpotStatus.title: String
    get() = when (this) {
        SpotStatus.Free -> "Free"
        is SpotStatus.Occupied -> "Occupied"
        SpotStatus.Unavailable -> "Unavailable"
    }
