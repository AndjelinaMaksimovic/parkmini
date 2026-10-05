# ParkMini: task texts for the Pages document (English)

Copy-paste texts for the tasks of Class 3 and the homework, in the same style as Tasks 1 to 3. Numbering after the cut:

| Old number | New number | Class |
|---|---|---|
| Task 1, 2, 3 | Task 1, 2, 3 | Class 2 (typed live) |
| Task 4 (filter, statistics, one spot) | **Homework 1** | ready code |
| Task 5 (ParkingRepository) | removed as a task; **given as ready code** at the start of Class 3 | |
| Task 6 (reserving a spot) | **Task 4** | Class 3 (typed live) |
| Task 7 (confirming and leaving) | **Homework 2** | ready code |
| (new) | **Task 5: Sensor simulation** | Class 3 (typed live) |
| (new) | **Homework 3: Errors and tests** | homework |

## Missing "Done when" lines for Task 2 and Task 3

**Task 2: Done when.** Printing the sample data shows 18 spots; exactly one (C6) is unavailable; about 40% of the rest are occupied (the exact number changes on every run). The statistics show total = 18 and free + occupied = 17.

**Task 3: Done when.** The server starts without errors. In the browser, `http://localhost:8080/` shows a short text and `http://localhost:8080/api/spots` shows a JSON list with 18 spots, where spot C6 has the status UNAVAILABLE.

---

## Task 4: Reserving a spot

*Given (ready code).* The list of spots lives in one class, `ParkingRepository`, which the server creates once when it starts. The class offers two read operations: get all spots, and find one spot by its id.

*Requirements.* A driver can reserve a free spot. The reservation is valid for 10 minutes.

| Method | Path | Body | Success | Errors |
|---|---|---|---|---|
| POST | `/api/spots/{id}/reserve` | `{"driver": "ana"}` | `200 OK`, the updated spot: status `RESERVED`, `reservedBy` and `reservedUntil` are filled | `409 Conflict` if the spot is not FREE; `404 Not Found` if the spot does not exist; `400 Bad Request` if the id or the body is invalid |

*Kotlin concepts covered (note for me).* `sealed interface` (three outcomes of an action), `when` over a sealed type without `else`, the elvis operator with `return` (`find(id) ?: return ...`), `copy()` for changing a spot, `companion object` with a constant.

*Done when.* Reserving a free spot returns it as RESERVED; reserving the same spot again returns 409; reserving spot 999 returns 404.

## Task 5: Sensor simulation

*Requirements.* The server changes the state of the car park by itself, as if real sensors were installed. Every 2 to 6 seconds, one spot changes: a free spot becomes OCCUPIED (a car arrives) or an occupied spot becomes FREE (a car leaves). The occupancy stays between 20% and 70%. The sensors never touch an unavailable spot or a spot that is reserved or occupied by a driver of the app.

*Kotlin concepts covered (note for me).* A coroutine (`launch`, `delay`, `isActive`), `CoroutineScope` as an extension receiver, why `delay` does not block a thread (unlike `Thread.sleep`), `randomOrNull()`, `@Synchronized` (several threads now change the list).

*Done when.* While the server runs, refreshing `http://localhost:8080/api/spots/stats` every few seconds shows that the numbers of free and occupied spots keep changing, and the occupancy never leaves the range 20% to 70%.

---

## Homework 1: Filter, statistics and a single spot

*Requirements.* The server offers three more ways to read the parking data.

| Method | Path | Description | Response |
|---|---|---|---|
| GET | `/api/spots?status=free` | Returns only the spots with the given status (case does not matter) | `200 OK`, JSON array. Unknown status: `400 Bad Request` |
| GET | `/api/spots/stats` | Returns the statistics of all spots | `200 OK`, JSON object |
| GET | `/api/spots/{id}` | Returns one spot by its id | `200 OK`, JSON object. Unknown id: `404 Not Found`. An id that is not a number: `400 Bad Request` |

Without the `status` parameter, `GET /api/spots` still returns all 18 spots.

*Kotlin concepts covered (note for me).* Query parameters, path parameters, `?.` and `toIntOrNull()`, `SpotStatus.entries.find { }`, early return `return@get`, HTTP status codes, reusing `toStats()`.

*Done when.* `/api/spots?status=free` shows only FREE spots, `/api/spots/stats` shows the statistics (total = 18, unavailable = 1), `/api/spots/5` shows spot A5, `/api/spots/999` returns 404 and `/api/spots?status=xyz` returns 400.

## Homework 2: Confirming arrival and leaving

*Requirements.* The driver who reserved a spot can confirm arrival (the spot becomes OCCUPIED) or cancel the reservation / leave the spot (the spot becomes FREE). Nobody else can do this. A reservation that is older than 10 minutes is cancelled automatically.

| Method | Path | Body | Success | Errors |
|---|---|---|---|---|
| POST | `/api/spots/{id}/confirm` | `{"driver": "ana"}` | `200 OK`, spot is `OCCUPIED` | `409` if the spot is not reserved, or it is reserved by another driver; `404`, `400` as in Task 4 |
| POST | `/api/spots/{id}/release` | `{"driver": "ana"}` | `200 OK`, spot is `FREE` again | `409` if the driver does not hold the spot; `404`, `400` as in Task 4 |

*Kotlin concepts covered (note for me).* A lambda as a function parameter (`handleAction { id, driver -> ... }`), reusing one pattern for three routes, `when` with conditions.

*Done when.* reserve → confirm → release works for one driver, and another driver gets 409 on confirm and on release.

## Homework 3: Errors and tests

*Requirements.* Every error answer is JSON of the form `{"message": "..."}`. A request body that is not valid JSON returns `400 Bad Request`. Automated tests cover: reserve then confirm, a double reservation (409), another driver's spot (409), an unknown spot (404) and the expiry of a reservation.

*Kotlin concepts covered (note for me).* `StatusPages`, `testApplication`, `kotlin.test`, a data class as an error response.

*Done when.* `gradlew test` passes, and a request with the body `not json` returns 400 with a JSON message.
