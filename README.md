# ParkMini

A small car park with live status, written end to end in Kotlin. It is the example project for a six-class introduction to Kotlin
(three classes of backend, three classes of Android) for students who know Java or C#.

* **backend/**: a Ktor server that simulates the parking sensors. REST API plus a WebSocket that pushes every change (package `com.v1.parkmini`).
* **android/**: a Jetpack Compose app that shows the spots in colour and lets a driver reserve, confirm arrival and leave (package `rs.pmf.parkmini`).
* **docs/**: a Serbian setup guide and a Postman collection. The task texts are a separate document.

Open `backend` and `android` as **two separate projects** in IntelliJ IDEA (they are independent Gradle builds).

## Quick start

```bash
cd backend && ./gradlew run          # server on :8080   (Windows: gradlew.bat run)
```

* Browser: <http://localhost:8080/api/spots>
* IntelliJ HTTP client: `backend/requests.http` (requests with automatic checks)
* Android: open the `android` folder, run the `app` configuration on an emulator (the emulator reaches the computer at `10.0.2.2:8080`) or on a phone (change `BASE_URL` and `WS_URL` in `ParkingApi.kt`).

## API

| | Path | |
|---|---|---|
| GET | `/` | health check |
| GET | `/api/spots?status=free` | all spots, optionally filtered |
| GET | `/api/spots/stats` | statistics |
| GET | `/api/spots/{id}` | one spot |
| POST | `/api/spots/{id}/reserve`, `/confirm`, `/release` | body `{"driver": "ana"}`; `409` conflict, `404` unknown spot, `400` bad request |
| WebSocket | `/ws/spots` | the full list on connect and after every change |

## Classes and tasks

| Class | Tasks | Where |
|---|---|---|
| 2 | 1 Parking spot model, 2 Sample data and statistics, 3 First server | `backend` (`models/`, `demo.kt`, `Application.kt`) |
| 3 | 4 Reserving a spot, 5 Sensor simulation, 6 Live updates | `backend` (`ParkingRepository.kt`, `Simulator.kt`, `Application.kt`) |
| 4 | 7 Show the spots, 8 State and statistics | `android` (`ui/Components.kt`, `ui/ParkingScreen.kt`) |
| 5 | 9 Live data from the server | `android` (`data/ParkingApi.kt`, `ui/ParkingViewModel.kt`) |
| 6 | 10 Reserve from the app | `android` (`ui/SpotDialog.kt`, `ui/ParkingViewModel.kt`) |
| homework | 1 filter, statistics and a single spot; 2 confirming arrival and leaving | `backend` |

The code is the finished solution. Comments of the form `// Task N` mark what is typed live in class, and `// Task N | GIVEN (ready code)` marks what students receive ready.

## Design notes

* The list of spots is **immutable**: a change creates a new list. The repository keeps it in a `MutableStateFlow`, so the WebSocket route and the simulator need no timers or polling.
* Every public repository function is `@Synchronized`: "check, then change" must be atomic (20 parallel reservations of one spot give exactly one `200`).
* The sensors run in a coroutine (`suspend fun sensorSimulator`) launched in the server's scope.

## Versions (pinned)

Kotlin 2.3.21, Ktor 3.6.0, Gradle 9.3.0, AGP 8.13.1, `compileSdk` 36, Compose BOM 2025.09.00.

Older states of the project (per-class tags from the earlier polling version) are kept as `archive/cas*` tags.
