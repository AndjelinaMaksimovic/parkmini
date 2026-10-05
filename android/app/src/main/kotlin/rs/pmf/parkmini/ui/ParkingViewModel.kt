package rs.pmf.parkmini.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.pmf.parkmini.data.ParkingApi
import rs.pmf.parkmini.data.ParkingSpot
import rs.pmf.parkmini.data.Stats
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

sealed interface ParkingUiState {
    data object Loading : ParkingUiState
    data class Error(val message: String) : ParkingUiState
    data class Content(
        val spots: List<ParkingSpot>,
        val stats: Stats,
        val isOffline: Boolean = false,
    ) : ParkingUiState
}

class ParkingViewModel(private val api: ParkingApi = ParkingApi()) : ViewModel() {

    /** Every installation of the app is a different driver. */
    val driver = "vozac-${Random.nextInt(100, 1000)}"

    private val _uiState = MutableStateFlow<ParkingUiState>(ParkingUiState.Loading)
    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                refresh()
                delay(3.seconds)
            }
        }
    }

    private suspend fun refresh() {
        try {
            // Two requests at the same time: coroutines cost us almost nothing.
            val content = coroutineScope {
                val spots = async { api.getSpots() }
                val stats = async { api.getStats() }
                ParkingUiState.Content(spots.await(), stats.await())
            }
            _uiState.value = content
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.update { current ->
                when (current) {
                    is ParkingUiState.Content -> current.copy(isOffline = true)
                    else -> ParkingUiState.Error("Cannot connect to the server.\nIs the backend running?")
                }
            }
        }
    }
}
