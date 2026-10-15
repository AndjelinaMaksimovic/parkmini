package rs.pmf.parkmini.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v1.parkmini.shared.ParkingSpot
import com.v1.parkmini.shared.SpotStatus

// Task 9 | GIVEN (ready code): what the screen shows for Loading, Error and Content
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen(viewModel: ParkingViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val licensePlate by viewModel.licensePlate.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("ParkMini") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                ParkingUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ParkingUiState.Error -> Text(
                    text = s.message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                )
                is ParkingUiState.Content -> ParkingContent(
                    content = s,
                    licensePlate = licensePlate,
                    onOccupy = viewModel::occupy,
                    onRelease = viewModel::release,
                )
            }
        }
    }
}

// Task 7: the grid grouped by zone (typed). Task 8 adds the header and the click, Task 10 adds the dialog.
@Composable
private fun ParkingContent(
    content: ParkingUiState.Content,
    licensePlate: String,
    onOccupy: (ParkingSpot, String) -> Unit,
    onRelease: (ParkingSpot) -> Unit,
) {
    // Task 10: which spot is open in the dialog
    // We remember only the id, so the dialog always shows the freshest state of the spot.
    var selectedId by remember { mutableStateOf<Int?>(null) }
    // The sensors can change a spot while the dialog is open. If the spot is no longer free (and is not ours),
    // there is nothing to offer, so the dialog disappears.
    val selected = content.spots.firstOrNull { it.id == selectedId }
        ?.takeIf { it.status is SpotStatus.Free || it.isOccupiedBy(licensePlate) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Task 8: the header
        item(span = { GridItemSpan(maxLineSpan) }) {
            StatsHeader(content.stats, content.isOffline, licensePlate)
        }
        for ((zone, zoneSpots) in content.spots.groupBy { it.zone }) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Zone $zone", style = MaterialTheme.typography.titleMedium)
            }
            items(zoneSpots, key = { it.id }) { spot ->
                SpotTile(
                    spot = spot,
                    isMine = spot.isOccupiedBy(licensePlate),
                    onClick = { selectedId = spot.id },
                )
            }
        }
    }

    // Task 10 | GIVEN (ready code)
    selected?.let { spot ->
        SpotDialog(
            spot = spot,
            isMine = spot.isOccupiedBy(licensePlate),
            savedLicensePlate = licensePlate,
            onOccupy = { plate -> onOccupy(spot, plate) },
            onRelease = { onRelease(spot) },
            onDismiss = { selectedId = null },
        )
    }
}
