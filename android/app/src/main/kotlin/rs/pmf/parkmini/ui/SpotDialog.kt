package rs.pmf.parkmini.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import rs.pmf.parkmini.data.ParkingSpot
import rs.pmf.parkmini.data.SpotStatus
import kotlin.time.Clock

// Task 10 | GIVEN (ready code)
@Composable
fun SpotDialog(
    spot: ParkingSpot,
    isMine: Boolean,
    onReserve: () -> Unit,
    onConfirm: () -> Unit,
    onRelease: () -> Unit,
    onDismiss: () -> Unit,
) {
    // Every action runs the call and closes the dialog.
    fun run(action: () -> Unit): () -> Unit = { action(); onDismiss() }

    val (title, body) = when (spot.status) {
        SpotStatus.FREE -> "Spot ${spot.label}" to "A reservation is valid for 10 minutes."
        SpotStatus.RESERVED -> {
            val minutes = ((spot.reservedUntil ?: Clock.System.now()) - Clock.System.now()).inWholeMinutes + 1
            "Your reservation: ${spot.label}" to "Expires in about $minutes min. Confirm when you park."
        }
        else -> "Spot ${spot.label}" to "Are you leaving this parking spot?"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            when (spot.status) {
                SpotStatus.FREE -> TextButton(run(onReserve)) { Text("Reserve") }
                SpotStatus.RESERVED -> TextButton(run(onConfirm)) { Text("Confirm arrival") }
                else -> TextButton(run(onRelease)) { Text("Leave spot") }
            }
        },
        dismissButton = {
            if (isMine && spot.status == SpotStatus.RESERVED) {
                TextButton(run(onRelease)) { Text("Cancel reservation") }
            } else {
                TextButton(onDismiss) { Text("Close") }
            }
        },
    )
}

