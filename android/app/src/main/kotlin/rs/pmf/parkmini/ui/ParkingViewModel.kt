package rs.pmf.parkmini.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.pmf.parkmini.data.ApiException
import rs.pmf.parkmini.data.ParkingApi
import rs.pmf.parkmini.data.ParkingSpot
import rs.pmf.parkmini.data.Stats
import rs.pmf.parkmini.data.toStats
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

// Task 9: the UI state (typed)
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

    // Task 10: every installation of the app is a different driver
    /** Every installation of the app is a different driver. */
    val driver = "vozac-${Random.nextInt(100, 1000)}"

    // Task 9
    private val _uiState = MutableStateFlow<ParkingUiState>(ParkingUiState.Loading)
    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    // Task 10
    // One-time messages (snackbar) are not "state", so they do not go into uiState.
    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    // Task 9
    init {
        viewModelScope.launch { followServer() }
    }

    // Task 10: reserve and act (typed)
    fun reserve(spot: ParkingSpot) = act("Reserved spot ${spot.label}") { api.reserve(spot.id, driver) }

    // Homework 2 | GIVEN (ready code)
    fun confirmArrival(spot: ParkingSpot) = act("Arrival confirmed: ${spot.label}") { api.confirm(spot.id, driver) }

    fun release(spot: ParkingSpot) = act("Spot ${spot.label} is free again") { api.release(spot.id, driver) }

    private fun act(successMessage: String, call: suspend () -> ParkingSpot) {
        viewModelScope.launch {
            try {
                call()
                _messages.send(successMessage)
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                _messages.send(e.message ?: "Error")
            } catch (e: Exception) {
                _messages.send("No connection to the server")
            }
        }
    }

    // Task 9: followServer (typed)
    // The server pushes the list, we only collect it. If the connection drops, show "offline" and try again.
    private suspend fun followServer() {
        while (true) {
            try {
                api.spots().collect { spots ->
                    _uiState.value = ParkingUiState.Content(spots, spots.toStats())
                }
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
            delay(2.seconds)
        }
    }
}
