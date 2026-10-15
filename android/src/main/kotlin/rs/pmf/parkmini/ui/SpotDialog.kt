package rs.pmf.parkmini.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.v1.parkmini.shared.ParkingSpot
import com.v1.parkmini.shared.SpotStatus
import com.v1.parkmini.shared.isValidLicensePlate

// Task 10 | GIVEN (ready code)
@Composable
fun SpotDialog(
    spot: ParkingSpot,
    isMine: Boolean,
    savedLicensePlate: String,
    onOccupy: (licensePlate: String) -> Unit,
    onRelease: () -> Unit,
    onDismiss: () -> Unit,
) {
    // the plate the driver is typing (starts with the plate used last time)
    var licensePlate by remember { mutableStateOf(savedLicensePlate) }
    // the same rule as on the server (shared module), so the button is enabled only for a plate the server accepts
    val isValid = licensePlate.isValidLicensePlate()

    if (spot.status is SpotStatus.Free) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Spot ${spot.label}") },
            text = {
                OutlinedTextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it.uppercase() },
                    label = { Text("License plate") },
                    supportingText = { Text("Format: KG-123-AB") },
                    isError = licensePlate.isNotEmpty() && !isValid,
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = { onOccupy(licensePlate); onDismiss() }, enabled = isValid) { Text("Park here") }
            },
            dismissButton = { TextButton(onDismiss) { Text("Close") } },
        )
    } else if (isMine) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Your spot: ${spot.label}") },
            text = { Text("Are you leaving this parking spot?") },
            confirmButton = { TextButton(onClick = { onRelease(); onDismiss() }) { Text("Leave spot") } },
            dismissButton = { TextButton(onDismiss) { Text("Close") } },
        )
    }
}
