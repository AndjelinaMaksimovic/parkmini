# ParkMini

A small car park with live status, written end to end in Kotlin. It is the example project for a six-class introduction to Kotlin
(three classes of backend, three classes of Android) for students who know Java or C#.

One Gradle project (Kotlin Multiplatform), three modules:

* **shared/**: the models used by both sides (`ParkingSpot`, `SpotStatus`, `Zone`, `Stats`, the license plate rule). Written once, compiled for the JVM and for Android.
* **backend/**: a Ktor server that simulates the parking sensors. A small REST API plus a WebSocket that pushes every change (package `com.v1.parkmini`).
* **android/**: a Jetpack Compose app that shows the spots and lets a driver park and leave (package `rs.pmf.parkmini`).
* **docs/**: a Serbian setup guide.

Open the **root folder** in IntelliJ IDEA (one window). An Android SDK is needed even for the backend classes, because Gradle loads all three modules.

## Quick start

```bash
./gradlew :backend:run          # server on :8080   (Windows: gradlew.bat :backend:run)
```

* Browser: <http://localhost:8080/> (health check)
* IntelliJ HTTP client: `backend/requests.http` (requests with automatic checks, and the WebSocket)
* Android: run the `android` configuration on an emulator (it reaches the computer at `10.0.2.2:8080`) or on a phone (change `BASE_URL` and `WS_URL` in `ParkingApi.kt`).

## API

| | Path | |
|---|---|---|
| GET | `/` | health check |
| POST | `/api/spots/{id}/occupy` | body `{"licensePlate": "KG-123-AB"}`; park in a free spot |
| POST | `/api/spots/{id}/release` | same body; only the car that is parked there can leave |
| WebSocket | `/ws/spots` | the full list on connect and after every change |

Errors: `409` the spot is not free (or not occupied by that plate), `404` unknown spot, `400` invalid id or license plate.
A spot in the list looks like `{"id": 1, "label": "A1", "zone": "A", "status": {"type": "free"}}`; an occupied one has
`"status": {"type": "occupied", "licensePlate": "KG-123-AB"}`. There is no endpoint for statistics: the app calculates them from the list.

## Classes and tasks

| Class | Tasks | Where |
|---|---|---|
| 2 | 1 Parking spot model, 2 Sample data, 3 First server | `shared` (model), `backend` (`models/`, `demo.kt`, `Application.kt`) |
| 3 | 4 Parking in a spot, 5 Sensor simulation, 6 Live updates | `backend` (`ParkingRepository.kt`, `Simulator.kt`, `Application.kt`) |
| 4 | 7 Show the spots, 8 State and statistics | `android` (`ui/Components.kt`, `ui/ParkingScreen.kt`), `shared` (`Stats.kt`) |
| 5 | 9 Live data from the server | `android` (`data/ParkingApi.kt`, `ui/ParkingViewModel.kt`) |
| 6 | 10 Park from the app | `android` (`ui/SpotDialog.kt`, `ui/ParkingViewModel.kt`) |

The code is the finished solution. Comments of the form `// Task N` mark what is typed live in class, and `// Task N | GIVEN (ready code)` marks what students receive ready.

## Design notes

* **Sealed status.** A spot is `Free`, `Occupied(licensePlate)` or `Unavailable`. A free spot with a license plate, or an occupied spot without one, cannot exist.
* **Immutable list + `StateFlow`.** A change creates a new list. The repository keeps it in a `MutableStateFlow`, so the WebSocket route needs no timers or polling. The list is the single source of truth: statistics are derived from it in the app.
* **`@Synchronized`** on the repository functions: "check, then change" must be atomic (20 parallel requests for one spot give exactly one `200`).
* **Simulated cars** have plates like `SIM-123`. A driver cannot type such a plate (the shared format rule rejects it), so the sensors only free their own cars.
* The sensors run in a coroutine (`suspend fun sensorSimulator`) launched in the server's scope.

## Known limitations (on purpose, it is a simplified example)

No authentication (the license plate in the request is trusted), every client sees every license plate, data lives only in memory, plain HTTP/WS.
See the `// SECURITY` comment in `ParkingRepository.kt`.

## Versions (pinned)

Kotlin 2.3.21, Ktor 3.6.0, Gradle 9.3.0, AGP 8.13.1, `compileSdk` 36, Compose BOM 2025.09.00.

Older states of the project (per-class tags from the earlier polling version) are kept as `archive/cas*` tags.
