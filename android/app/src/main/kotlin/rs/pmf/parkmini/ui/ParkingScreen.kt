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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen(viewModel: ParkingViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("ParkMini") }) }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            // when over a sealed type: the compiler checks that we handled every state.
            when (val s = state) {
                ParkingUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ParkingUiState.Error -> Text(
                    text = s.message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                )
                is ParkingUiState.Content -> ParkingContent(s, viewModel.driver)
            }
        }
    }
}

@Composable
private fun ParkingContent(content: ParkingUiState.Content, driver: String) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            StatsHeader(content.stats, content.isOffline, driver)
        }
        for ((zone, zoneSpots) in content.spots.groupBy { it.zone }) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Zone $zone", style = MaterialTheme.typography.titleMedium)
            }
            items(zoneSpots, key = { it.id }) { spot ->
                // Actions (reservation) come in class 6.
                SpotTile(spot = spot, isMine = spot.reservedBy == driver, onClick = {})
            }
        }
    }
}
