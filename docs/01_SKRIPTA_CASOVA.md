# Skripta za 6 časova: Uvod u Kotlin (backend + Android): ParkMini (REST)

> **Radna skripta za predavača.** Kod je izvučen direktno iz git tagova, pa je isti kao u radnom projektu.
> Instalacija: [00_INSTALACIJA.md](00_INSTALACIJA.md). Tekstovi taskova za Pages (engleski): [02_TASKS_EN.md](02_TASKS_EN.md).

## Princip: kucamo samo ono što nosi koncept

Realno, kad studenti kucaju uživo, jedan task od 15 do 25 linija traje **10 do 12 minuta**. Zato čas primi 3 do 4 stvari, ne 6.
Svaki task je ili **"kucamo uživo"**, ili **"gotov kod (čitamo)"**, ili **"domaći"**.

| Čas | Kucaju uživo | Gotov kod (čitamo, ne kucamo) | Domaći |
|---|---|---|---|
| **1** | Kotlin osnove: `val`/`var`, funkcije, null safety, `when`, kolekcije i lambde, `data class` | | evolucije sa `groupBy`/`groupingBy` |
| **2** | **Task 1** model, **Task 2** početni podaci i statistika, **Task 3** prvi server | | Homework 1: filter, statistika, jedno mesto |
| **3** | **Task 4** rezervacija (`sealed`), **Task 5** simulator senzora (korutine) | `ParkingRepository` (čitanje), potvrda i otkazivanje, istek rezervacije | Homework 2 (potvrda i otkazivanje), Homework 3 (greške i testovi) |
| **4** | Compose osnove: `@Composable`, stanje, `SpotTile`, lista sa lažnim podacima | `Theme.kt`, zaglavlje sa statistikom, modeli | |
| **5** | Ktor klijent (`ParkingApi`), `ViewModel`, `UiState` (`sealed`), živi podaci | rad bez veze (baner) | |
| **6** | akcije: klik, dijalog, rezervacija | potvrda, otkazivanje, snackbar; demo sa dva uređaja (radi predavač) | |

## Struktura projekta

```
parkmini-rest/
├── backend/   Ktor REST server (paket com.v1.parkmini)
├── android/   Jetpack Compose aplikacija (paket rs.pmf.parkmini)
└── docs/
```

Dva nezavisna Gradle projekta (backend ne zahteva Android SDK). **Modeli su namerno kopirani** (backend `models/` i Android `data/Models.kt`): to je DTO obrazac koji važi i sa backendom u bilo kom drugom jeziku.

## Git tagovi: stanje koda po časovima

Student koji zaostane radi `git checkout <tag>`.

| Tag | Stanje |
|---|---|
| `cas0-start` | prazan skelet: Gradle, zavisnosti, "Zdravo" (**domaći 0**) |
| `cas1` | kraj Časa 1 (+ `playground/Class1.kt`) |
| `cas2` | kraj Časa 2: Task 1 do 3 (model, podaci, statistika, prvi server) |
| `cas3-start` | **početak Časa 3**: `ParkingRepository` + read endpointi su već gotovi (Homework 1) |
| `cas3` | kraj Časa 3: rezervacija, potvrda, otkazivanje, simulator senzora |
| `cas4` | kraj Časa 4: Compose, stanje, lažni podaci |
| `cas5` | kraj Časa 5: Ktor klijent, `ViewModel`, živi podaci |
| `cas6` | kraj Časa 6: **aplikacija gotova** |

Šta je novo u času N: `git diff cas(N-1) casN --stat`.

## Šta je proveravano

- Svaki tag je kompajliran iz čistog klona (backend `gradlew build`, Android `compileDebugKotlin`).
- Backend je testiran preko HTTP-a (Postman kolekcija, 17 zahteva, svi statusi se poklapaju) i sa simulatorom.
- **Android aplikacija je pokrenuta na emulatoru** (Pixel 6, Android 16): prikaz mesta, rezervacija, potvrda dolaska, napuštanje, greška servera u snackbar-u, rad bez veze (baner + poslednje stanje) i automatski oporavak.
- **Nije proveravano:** rad u IntelliJ GUI-ju, rad sa pravim telefonom, dva uređaja istovremeno.

## Mentalna mapa (stalno se vraćaj na nju)

| Java / C# | Kotlin |
|---|---|
| `final` / `readonly` | `val` (`var` je promenljivo) |
| svaka referenca može biti `null` | `String?`: tip kaže da li sme `null` |
| POJO + `equals/hashCode/toString` / `record` | `data class` (+ `copy()`) |
| `switch` | `when`: izraz; sa `sealed` tipovima kompajler proverava da su svi slučajevi pokriveni |
| statičke util klase | extension funkcije |
| Stream / LINQ | `map`, `filter`, `groupBy` direktno na kolekciji |
| `async/await`, niti | korutine: `suspend`, `launch`, `delay` |

## Prečice (Windows prvo, pa macOS)

| Akcija | Windows / Linux | macOS |
|---|---|---|
| Pokreni `main` / test | zeleni trougao, ili `Ctrl+Shift+F10` | zeleni trougao, ili `Ctrl+Shift+R` |
| Predlozi i brze ispravke | `Alt+Enter` | `Option+Enter` |
| Pretraga svega | `Shift` `Shift` | `Shift` `Shift` |
| Pretraga akcije (npr. "Show Kotlin Bytecode") | `Ctrl+Shift+A` | `Cmd+Shift+A` |
| Idi na deklaraciju | `Ctrl+B` | `Cmd+B` |
| Tip izraza | `Alt+=` | `Ctrl+Shift+P` |
| Formatiraj kod | `Ctrl+Alt+L` | `Cmd+Option+L` |
| Optimizuj importe | `Ctrl+Alt+O` | `Ctrl+Option+O` |
| Preimenuj | `Shift+F6` | `Shift+F6` |

---

# ČAS 1: Kotlin osnove (u konzoli)

**Tag na početku:** `cas0-start` · **Na kraju:** `cas1` · **Radi se u:** `backend/`, paket `playground`.

**Cilj:** da za 45 minuta osete da je Kotlin Java/C# koja se piše brže i bezbednije. Nema servera i klasa (osim teasera na kraju).

| Min | Segment | Šta kucaš / pokazuješ |
|---|---|---|
| 0–5 | Uvod i demo | Pokaži gotovu aplikaciju (`cas6`) na emulatoru. "Ovo gradimo za 6 časova." |
| 5–15 | `val`/`var`, šabloni, funkcije | `varVal()`, `functions()`. Greška `town = "Beograd"` ne kompajlira. Podrazumevani i imenovani argumenti. |
| 15–27 | **Null safety** | `nullSafety()`: prvo greška za `vozac.length`, pa `?.`, `?:`, smart cast. **Najjači argument, ne žuri.** |
| 27–35 | `if`/`when` kao izrazi | `whenExp()`, opsezi `1..3`, `downTo`, `step`. `Alt+Enter` → *Replace 'if' with 'when'*. |
| 35–42 | Kolekcije i lambde | `collections()`: `filter`, `groupBy`, `mapValues`, `firstOrNull ?:`. Poredi sa Stream/LINQ. |
| 42–45 | `data class` | `firstDataClass()`: `copy()`, `==`, destrukturiranje. **Decompile** u Javu (`Ctrl+Shift+A` → "Show Kotlin Bytecode" → Decompile). |

**Ako kasniš:** preskoči `for/downTo/step` i `mapValues`. **Evolucije** (`groupBy` → `groupingBy`, `shuffled`) su opcione.

**`Class1.kt`** (`backend/src/main/kotlin/playground/Class1.kt`)

```kotlin
package com.v1.playground

import kotlin.times

/*
* val/var
* functions
* null safety
* when
* collections and lambdas
* data class
* java decompile
* */

fun main() {
    varVal()
    functions() // calling a function
    nullSafety()
    whenExp()
    collections()
    firstDataClass()
}

fun varVal() {
    val town = "Kragujevac" // val means immutable (just like final or readonly)
    var spotsNumber = 18    // mutable, type is inferred
    spotsNumber += 2
    val pricePerHour: Double = 120.0

    println("Parking in town $town has $spotsNumber spots, and two hours costs ${pricePerHour * 2}.")
}

// Functions: expression body, default and named arguments
fun functions() {
    println(price(3))
    println(price(3, discount = 0.1))
    println(price(hours = 2, pricePerHour = 150.0))
}

fun price(hours: Int, pricePerHour: Double = 120.0, discount: Double = 0.0) = hours * pricePerHour * (1 - discount)

// Null safety show of
fun nullSafety(){
    var vozac: String? = null                       // ? = can be null
    println(vozac?.length)                          // safe call -> null
    println(vozac?.uppercase() ?: "Unknown driver") // Elvis operator -> default valueDo

    vozac = "Ana"
    println(vozac.length)                           // smart cast: compiler knows it is not null
}

// if/when expressions show of
fun occupancyDescription(procent: Int): String = when {
    procent < 30 -> "Empty"
    procent in 30..70 -> "Normal"
    procent > 70 -> "Almost full"
    else -> "?"
}

fun whenExp() {
    val procent = 45
    val message = if (procent > 50) "Crowded" else "There are spots available"
    println("$message / ${occupancyDescription(procent)}")

    for (i in 1..3) print("A$i ")
    for (i in 6 downTo 1 step 2) print("B$i ")
    println()
}

// Collections and lambdas (instead of Stream API/LINQ)
fun collections() {
    val labels = listOf("A1", "A2", "B1", "B2", "B3", "C1")   // immutable list
    val taken = mutableSetOf("A2", "B3")                    // mutable set

    val free = labels.filter { it !in taken }
    println("Free spots: $free")

    // TODO: Built to groupBy for better explanation
    val perZones = labels.groupBy { it.first() }              // Map<Char, List<String>>
    println(perZones)
    println(perZones.mapValues { (_, mesta) -> mesta.size })

    println(labels.joinToString(", ") { it.lowercase() })
    println(labels.count { it.startsWith("B") })
    println(labels.firstOrNull { it.startsWith("Z") } ?: "no zone Z")
}

/* 1. Mini Task for students
    Using labels, print only spots from B zone in caps lock, separated by comma.

    Build solution step by step.
    labele.filter { it.startsWith("B") }.map { it.uppercase() }.joinToString(", ")
* */

// data class show of with decompile to Java
data class Spot(val sign: String, val zone: String, val taken: Boolean = false)

fun firstDataClass(){
    val a1 = Spot("A1", "A1", true)
    val a1Taken = a1.copy(taken = true)                     // immutable objects + copy()
    println(a1)                                             // prewritten toString() for us
    println( a1 == Spot("A1","A"))             // prewritten equals() for us
    println(a1Taken)
    val (sign, zone) = a1                                   // decoupling
}
```

---

# ČAS 2: Model, podaci, prvi server (Task 1 do 3)

**Tag na početku:** `cas1` · **Na kraju:** `cas2`

**Cilj:** pravi OOP u Kotlinu i prvi endpoint koji vraća JSON. **Redosled je: model → podaci → statistika → server** (prvo podaci, da se statistika odmah vidi).

| Min | Task | Šta kucaš |
|---|---|---|
| 0–5 | | Ponavljanje: `data class`, `copy` |
| 5–15 | **Task 1: model** | `enum SpotStatus`, `enum Zone`, `data class ParkingSpot` (podrazumevane vrednosti, `String?`, `@Serializable`). Isprobaj `copy(status = ...)` u `demo.kt`. Usput: `companion object` koji se pojavi uz `@Serializable` (jedna rečenica). |
| 15–27 | **Task 2: podaci** | `sampleSpots()`: dve petlje, `"$zone$num"`, `when` sa tri grane (C6 nedostupno, `Random` za zauzeta, ostalo slobodno). Gradi se u koracima: 18 mesta → C6 → 40%. |
| 27–33 | **Task 2: statistika** | `toStats()` (extension funkcija, `groupingBy { }.eachCount()`, elvis). Pozovi iz `demo.kt`. |
| 33–45 | **Task 3: prvi server** | `Application.kt`: `embeddedServer`, `ContentNegotiation`, `get("/")`, `get("/api/spots")`. Otvori `localhost:8080/api/spots` u browseru. |

**"Wow" trenutak:** `call.respond(spots)`: lista `data class`-ova postaje JSON, bez mappera.
**Zamka:** ako zaborave `@Serializable`, dobiju grešku u runtime-u; pročitaj poruku zajedno.
**Ako kasniš:** `toStats()` pokaži kao gotov kod.
**Gotov kod / Homework 1** (stiže u `cas3-start`): filter `?status=`, `/stats`, `/{id}`.

**`ParkingSpot.kt`** (`backend/src/main/kotlin/parkmini/models/ParkingSpot.kt`)

```kotlin
package com.v1.parkmini.models

import com.v1.parkmini.models.enums.SpotStatus
import com.v1.parkmini.models.enums.Zone
import kotlinx.serialization.Serializable

/** Every parking spot has an:
    - id
    - zone (e.g. A)
    - label (e.g. A3)
    - one of 4 possible statuses (FREE, RESERVED, OCCUPIED, UNAVAILABLE)
A spot is FREE by default.
A spot may also store who reserved it and until when; otherwise, these two values are empty.
 **/
@Serializable
data class ParkingSpot(
    val id: Int,
    val label: String,
    val zone: Zone,
    val status: SpotStatus = SpotStatus.FREE,
    val reservedBy: String? = null,
    val reservedUntil: Long? = null, // epoch millis
)
```

**`SpotStatus.kt`** (`backend/src/main/kotlin/parkmini/models/enums/SpotStatus.kt`)

```kotlin
package com.v1.parkmini.models.enums

import kotlinx.serialization.Serializable

/** 4 possible statuses**/
@Serializable
enum class SpotStatus {
    FREE,
    RESERVED,
    OCCUPIED,
    UNAVAILABLE
}
```

**`Zone.kt`** (`backend/src/main/kotlin/parkmini/models/enums/Zone.kt`)

```kotlin
package com.v1.parkmini.models.enums

enum class Zone {
    A,
    B,
    C
}
```

**`SampleData.kt`** (`backend/src/main/kotlin/parkmini/models/SampleData.kt`)

```kotlin
package com.v1.parkmini.models

import com.v1.parkmini.models.enums.SpotStatus
import com.v1.parkmini.models.enums.Zone
import kotlin.random.Random


/** The parking lot has:
    - 18 spots
    - 3 zones (A, B, C)
    - 6 spots per zone, labeled A1-A6, B1-B6, and C1-C6
    - spot C6 is unavailable
When the server starts, about 40% of the spots are already OCCUPIED, picked at random.
occupiedShare is the percentage of spots OCCUPIED in the beginning (a chance that any other spot starts as OCCUPIED - 0.4 about 40%)
 **/
fun sampleSpots(occupiedShare: Double = 0.0 ): List<ParkingSpot> {
    val spots = mutableListOf<ParkingSpot>()
    var id = 1
    for (zone in Zone.entries) {
        for (num in 1..6) {
            val status = when {
                zone == Zone.C && num == 6 -> SpotStatus.UNAVAILABLE
                Random.nextDouble() < occupiedShare -> SpotStatus.OCCUPIED
                else -> SpotStatus.FREE
            }
            spots.add(ParkingSpot(id, "$zone$num", zone, status))
            id++
        }
    }
    return spots
}

/*If I have enough time show shuffle with take: */
fun sampleSpots2(occupiedShare: Double = 0.0 ): List<ParkingSpot> {
    val spots = mutableListOf<ParkingSpot>()
    var id = 1
    for (zone in Zone.entries) {
        for (num in 1..6) {
            val status = if (zone == Zone.C && num == 6) SpotStatus.UNAVAILABLE
            else SpotStatus.FREE
            spots.add(ParkingSpot(id, "$zone$num", zone, status))
            id++
        }
    }
    // 1. candidates: spots that can become occupied (every one of them without C6)
    val candidates = spots.filter { it.status == SpotStatus.FREE }

    // 2. how many spots should be taken
    val howMany = (spots.size * occupiedShare).toInt()

    // 3. shuffle candidates and take first howMany
    val chosen = candidates.shuffled().take(howMany)

    // 4. we exchange chosen spots with occupied copies, the rest of them stay the same
    return spots.map { if (it in chosen) it.copy(status = SpotStatus.OCCUPIED) else it }
    return spots
}
```

**`Stats.kt`** (`backend/src/main/kotlin/parkmini/models/Stats.kt`)

```kotlin
package com.v1.parkmini.models

import com.v1.parkmini.models.enums.SpotStatus
import kotlinx.serialization.Serializable

/**For any list of spots, we can compute statistics:
	- the total number of spots
	- the number of spots in each status
	- occupancy percentage (reserved + occupied spots as a percentage of all spots)
**/
@Serializable
data class Stats(
    val total: Int, val free: Int, val reserved: Int, val occupied: Int, val unavailable: Int, val occupancyPercent: Int
)

// Extension function show of - adding toStats() to an existing class List, without inheritance
fun List<ParkingSpot>.toStats(): Stats {
    val count = groupingBy { it.status }.eachCount()
    val busy = (count[SpotStatus.RESERVED] ?: 0) + (count[SpotStatus.OCCUPIED] ?: 0)
    return Stats(
        total = size,
        free = count[SpotStatus.FREE] ?: 0,
        reserved = count[SpotStatus.RESERVED] ?: 0,
        occupied = count[SpotStatus.OCCUPIED] ?: 0,
        unavailable = count[SpotStatus.UNAVAILABLE] ?: 0,
        occupancyPercent = if (isEmpty()) 0 else busy * 100 / size,
    )
}

//TODO:  step by step groupingBy show off
/*
// V1 - "traditional" way
var free = 0
var reserved = 0
var occupied = 0
var unavailable = 0
for (spot in this) {
    if (spot.status == SpotStatus.FREE) {
        free++
    } else if (spot.status == SpotStatus.RESERVED) {
        reserved++
    } else if (spot.status == SpotStatus.OCCUPIED) {
        occupied++
    } else if (spot.status == SpotStatus.UNAVAILABLE) {
        unavailable++
    }
}
val busy = reserved + occupied
val percent = if (this.isEmpty()) 0 else busy * 100 / this.size
return Stats(this.size, free, reserved, occupied, unavailable, percent )

// V2 - V1 simplified with when
for (spot in this) {
    when (spot.status) {
        SpotStatus.FREE -> free++
        SpotStatus.RESERVED -> reserved++
        SpotStatus.OCCUPIED -> occupied++
        SpotStatus.UNAVAILABLE -> unavailable++
    }
}

//V3 without loop and counter -> count{}
free = this.count{ it.status == SpotStatus.FREE }
reserved = this.count{ it.status == SpotStatus.RESERVED }
occupied = this.count{ it.status == SpotStatus.OCCUPIED }
unavailable = this.count{ it.status == SpotStatus.UNAVAILABLE }
*/
```

**`Application.kt`** (`backend/src/main/kotlin/parkmini/Application.kt`)

```kotlin
package com.v1.parkmini

import com.v1.parkmini.models.sampleSpots
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

// Task 3
fun main() {
    val spots = sampleSpots(occupiedShare = 0.4)

    // starting a server inside our project
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() } // no handwriting of JSON

        routing {
            get("/") { call.respondText("ParkMini API works.") }

            get("/api/spots") { call.respond(spots) }
        }
    }.start(wait = true) // stay and listen
}
```

---

# ČAS 3: Rezervacija i simulator senzora (Task 4 i 5)

**Tag na početku:** `cas3-start` (gotovo: `ParkingRepository` sa `all()`/`find()` i read endpointi) · **Na kraju:** `cas3` (backend gotov)

**Cilj:** pravila rezervacije i server koji sam menja stanje. Ovde su `sealed` i korutine.

| Min | Task | Šta radiš |
|---|---|---|
| 0–5 | | Pokreni `cas3-start`: pročitaj `ParkingRepository` (klasa sa konstruktorom, `private val`, `ParkingSpot?`). Pokaži endpointe u Postman-u. |
| 5–25 | **Task 4: rezervacija** | `sealed interface ActionResult` (3 ishoda), `DriverRequest`, `reserve()` u repozitorijumu (`find(id) ?: return ...`, `copy`, `companion object`), `post("/api/spots/{id}/reserve")`, `when (result)` **bez `else`**. Obriši jednu granu i kompajler javlja grešku. Test u Postman-u: 200, pa 409. |
| 25–40 | **Task 5: simulator** | `Simulator.kt`: `launch { while (isActive) { delay(...); repository.simulateSensor() } }` i `simulateSensor()` u repozitorijumu. Pozovi iz `embeddedServer { }`. Osvežavaj `/api/spots/stats`: brojevi se menjaju. |
| 40–45 | | `@Synchronized`: zašto (zahtevi i simulator menjaju listu istovremeno). Pokaži **gotov kod**: `confirmArrival`, `release`, `releaseExpired`, `handleAction`. |

**"Wow" trenutak:** osvežavaš `/api/spots` i mesta se sama zauzimaju i oslobađaju, bez niti, `Timer`-a i `@Scheduled`.
**Pravila simulatora (zahtev 2024):** zauzetost 20 do 70%; rezervacija ističe za 10 min.
**Ako kasniš:** Task 5 pokaži kao gotov kod i samo ga pokreni.
**Domaći:** Homework 2 (potvrda i otkazivanje, isti obrazac kao `reserve`), Homework 3 (greške kao JSON i testovi).

**Što se menja u `ParkingRepository.kt`** (`backend/src/main/kotlin/parkmini/ParkingRepository.kt`):

```diff
@@ -2,5 +2,8 @@ package com.v1.parkmini
 
 import com.v1.parkmini.models.ParkingSpot
+import com.v1.parkmini.models.enums.SpotStatus
+import kotlin.random.Random
 
+// explain primary constructor with decompile to java
 /**
  * The list of parking spots lives in one class, ParkingRepository, which is created once when the server starts.
@@ -11,6 +14,91 @@ class ParkingRepository(initial: List<ParkingSpot>) {
     private val spots = initial.toMutableList()
 
+    @Synchronized
     fun all(): List<ParkingSpot> = spots.toList()
 
+    @Synchronized
     fun find(id: Int): ParkingSpot? = spots.find { it.id == id }
-}
+
+    // Task 6 and 7
+    @Synchronized
+    fun reserve(id: Int, driver: String): ActionResult {
+        val spot = find(id) ?: return ActionResult.NotFound(id)
+        if (spot.status != SpotStatus.FREE) {
+            return ActionResult.Conflict("Spot ${spot.label} is not free (${spot.status})")
+        }
+        val reserved = spot.copy(
+            status = SpotStatus.RESERVED,
+            reservedBy = driver,
+            reservedUntil = System.currentTimeMillis() + RESERVATION_MILLIS,
+        )
+        replace(reserved)
+        return ActionResult.Success(reserved)
+    }
+
+    @Synchronized
+    fun confirmArrival(id: Int, driver: String): ActionResult {
+        val spot = find(id) ?: return ActionResult.NotFound(id)
+        return when {
+            spot.status != SpotStatus.RESERVED -> ActionResult.Conflict("Spot ${spot.label} is not reserved")
+            spot.reservedBy != driver -> ActionResult.Conflict("Spot is reserved by another driver")
+            else -> {
+                val occupied = spot.copy(status = SpotStatus.OCCUPIED, reservedUntil = null)
+                replace(occupied)
+                ActionResult.Success(occupied)
+            }
+        }
+    }
+
+    /** Cancel a reservation or leave the spot: only the driver who holds the spot can do it. */
+    @Synchronized
+    fun release(id: Int, driver: String): ActionResult {
+        val spot = find(id) ?: return ActionResult.NotFound(id)
+        if (spot.reservedBy != driver) {
+            return ActionResult.Conflict("Spot ${spot.label} is not held by $driver")
+        }
+        val free = spot.copy(status = SpotStatus.FREE, reservedBy = null, reservedUntil = null)
+        replace(free)
+        return ActionResult.Success(free)
+    }
+
+    // Task 5: the sensors. Several threads (requests + simulator) now change the list, so every public function is @Synchronized.
+    @Synchronized
+    fun simulateSensor() {
+        val usable = spots.filter { it.status != SpotStatus.UNAVAILABLE }
+        val busy = usable.count { it.status != SpotStatus.FREE }
+        val ratio = busy.toDouble() / usable.size
+
+        val shouldOccupy = when {
+            ratio < 0.2 -> true      // too empty: a car arrives
+            ratio > 0.7 -> false     // too full: a car leaves
+            else -> Random.nextBoolean()
+        }
+        val candidate = if (shouldOccupy) {
+            usable.filter { it.status == SpotStatus.FREE }.randomOrNull()
+        } else {
+            usable.filter { it.status == SpotStatus.OCCUPIED && it.reservedBy == null }.randomOrNull()
+        }
+        if (candidate != null) {
+            replace(candidate.copy(status = if (shouldOccupy) SpotStatus.OCCUPIED else SpotStatus.FREE))
+        }
+    }
+
+    /** Ready code: a reservation that is older than 10 minutes is cancelled. */
+    @Synchronized
+    fun releaseExpired(now: Long = System.currentTimeMillis()) {
+        for (spot in spots.toList()) {
+            if (spot.status == SpotStatus.RESERVED && (spot.reservedUntil ?: Long.MAX_VALUE) < now) {
+                replace(spot.copy(status = SpotStatus.FREE, reservedBy = null, reservedUntil = null))
+            }
+        }
+    }
+
+    private fun replace(updated: ParkingSpot) {
+        val index = spots.indexOfFirst { it.id == updated.id }
+        spots[index] = updated
+    }
+
+    companion object {
+        const val RESERVATION_MILLIS = 10 * 60 * 1000L   // 10 minutes
+    }
+}
\ No newline at end of file
```

**`ActionResult.kt`** (`backend/src/main/kotlin/parkmini/ActionResult.kt`)

```kotlin
package com.v1.parkmini

import com.v1.parkmini.models.ParkingSpot

/** Every action on a spot ends in exactly one of these three outcomes. */
sealed interface ActionResult {
    data class Success(val spot: ParkingSpot) : ActionResult
    data class NotFound(val id: Int) : ActionResult
    data class Conflict(val reason: String) : ActionResult
}
```

**`DriverRequest.kt`** (`backend/src/main/kotlin/parkmini/models/DriverRequest.kt`)

```kotlin
package com.v1.parkmini.models

import kotlinx.serialization.Serializable

/** Body of a reservation request: {"driver": "ana"} */
@Serializable
data class DriverRequest(val driver: String)
```

**`Simulator.kt`** (`backend/src/main/kotlin/parkmini/Simulator.kt`)

```kotlin
package com.v1.parkmini

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/** The "sensors": a coroutine that keeps changing the state of the parking spots in the background. */
fun CoroutineScope.launchSensorSimulator(repository: ParkingRepository): Job = launch {
    while (isActive) {
        delay(Random.nextLong(2_000, 6_000))    // wait 2-6 seconds without blocking a thread
        repository.simulateSensor()
        repository.releaseExpired()
    }
}
```

**Što se menja u `Application.kt`** (`backend/src/main/kotlin/parkmini/Application.kt`):

```diff
@@ -14,4 +14,8 @@ import io.ktor.server.response.respondText
 import io.ktor.server.routing.get
 import io.ktor.server.routing.routing
+import com.v1.parkmini.models.DriverRequest
+import io.ktor.server.application.ApplicationCall
+import io.ktor.server.request.receive
+import io.ktor.server.routing.post
 
 
@@ -24,4 +28,6 @@ fun main() {
         install(ContentNegotiation) { json() } // no handwriting of JSON
 
+        launchSensorSimulator(repository)      // Task 5: the sensors work in the background
+
         routing {
             get("/") { call.respondText("ParkMini API works.") }
@@ -67,5 +73,32 @@ fun main() {
                 }
             }
+
+            post("/api/spots/{id}/reserve") {
+                call.handleAction { id, driver -> repository.reserve(id, driver) }
+            }
+
+            post("/api/spots/{id}/confirm") {
+                call.handleAction { id, driver -> repository.confirmArrival(id, driver) }
+            }
+
+            post("/api/spots/{id}/release") {
+                call.handleAction { id, driver -> repository.release(id, driver) }
+            }
         }
     }.start(wait = true) // stay and listen
 }
+
+/** Shared part of all three POST routes: read the id and the driver, run the action, send the answer. */
+private suspend fun ApplicationCall.handleAction(action: (id: Int, driver: String) -> ActionResult) {
+    val id = parameters["id"]?.toIntOrNull()
+        ?: return respondText("Invalid id", status = HttpStatusCode.BadRequest)
+    val request = receive<DriverRequest>()
+    respondResult(action(id, request.driver))
+}
+
+/** when over a sealed type: the compiler checks that we handled every outcome. */
+private suspend fun ApplicationCall.respondResult(result: ActionResult) = when (result) {
+    is ActionResult.Success -> respond(result.spot)
+    is ActionResult.NotFound -> respondText("Spot ${result.id} does not exist", status = HttpStatusCode.NotFound)
+    is ActionResult.Conflict -> respondText(result.reason, status = HttpStatusCode.Conflict)
+}
\ No newline at end of file
```

---

# ČAS 4: Android projekat i Compose osnove

**Tag na početku:** `cas3` (Android je samo "Zdravo, ParkMini!") · **Na kraju:** `cas4`. **Radi se u:** `android/` (poseban IntelliJ prozor).

**Cilj:** UI kao funkcija stanja, **još bez mreže** (lažni podaci).

| Min | Segment | Šta pokazuješ |
|---|---|---|
| 0–8 | **Anatomija projekta** | `AndroidManifest.xml` (`INTERNET`, `usesCleartextTraffic`), `app/build.gradle.kts`. Pokreni "Zdravo, ParkMini!" na emulatoru. |
| 8–18 | **`@Composable`** | `Text`, `Column`, `Row`, `Modifier.padding(16.dp)`. "UI je funkcija stanja." |
| 18–25 | **Stanje** | Scratch primer `Counter` (ispod): `remember`, `mutableStateOf`, `by`. |
| 25–40 | **`SpotTile` i lista** | Gotov `Theme.kt` (extension svojstva `color` i `title`). Kucaš `SpotTile` i `LazyVerticalGrid` sa `groupBy`. |
| 40–45 | **`@Preview` i klik** | Klik lokalno menja status (`copy`), a statistika na vrhu se sama preračuna. |

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Column(Modifier.padding(32.dp)) {
        Text("Clicked: $count times")
        Button(onClick = { count++ }) { Text("Click") }
    }
}
```

**"Wow" trenutak:** klik na pločicu menja boju, a "Free: X / 18" se preračuna samo.
**Ako kasniš:** preskoči `Counter`.

**`Models.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/data/Models.kt`)

```kotlin
// A copy of the backend models (in a real project: DTO classes that match the JSON of the server).
package rs.pmf.parkmini.data

import kotlinx.serialization.Serializable

@Serializable
enum class SpotStatus { FREE, RESERVED, OCCUPIED, UNAVAILABLE }

@Serializable
data class ParkingSpot(
    val id: Int,
    val label: String,
    val zone: String,
    val status: SpotStatus = SpotStatus.FREE,
    val reservedBy: String? = null,
    val reservedUntil: Long? = null, // epoch millis
)

@Serializable
data class DriverRequest(val driver: String)

@Serializable
data class Stats(
    val total: Int,
    val free: Int,
    val reserved: Int,
    val occupied: Int,
    val unavailable: Int,
    val occupancyPercent: Int,
)

fun List<ParkingSpot>.toStats(): Stats {
    val count = groupingBy { it.status }.eachCount()
    val busy = (count[SpotStatus.RESERVED] ?: 0) + (count[SpotStatus.OCCUPIED] ?: 0)
    return Stats(
        total = size,
        free = count[SpotStatus.FREE] ?: 0,
        reserved = count[SpotStatus.RESERVED] ?: 0,
        occupied = count[SpotStatus.OCCUPIED] ?: 0,
        unavailable = count[SpotStatus.UNAVAILABLE] ?: 0,
        occupancyPercent = if (isEmpty()) 0 else busy * 100 / size,
    )
}
```

**`Theme.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/Theme.kt`)

```kotlin
package rs.pmf.parkmini.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import rs.pmf.parkmini.data.SpotStatus

@Composable
fun ParkMiniTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(primary = Color(0xFF1565C0)),
        content = content,
    )
}

// Extension properties: we add behaviour to a class we did not write, without inheritance.
val SpotStatus.color: Color
    get() = when (this) {
        SpotStatus.FREE -> Color(0xFF43A047)
        SpotStatus.RESERVED -> Color(0xFFF9A825)
        SpotStatus.OCCUPIED -> Color(0xFFE53935)
        SpotStatus.UNAVAILABLE -> Color(0xFF1E88E5)
    }

val SpotStatus.title: String
    get() = when (this) {
        SpotStatus.FREE -> "Free"
        SpotStatus.RESERVED -> "Reserved"
        SpotStatus.OCCUPIED -> "Occupied"
        SpotStatus.UNAVAILABLE -> "Unavailable"
    }
```

**`Components.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/Components.kt`)

```kotlin
package rs.pmf.parkmini.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.pmf.parkmini.data.ParkingSpot
import rs.pmf.parkmini.data.SpotStatus
import rs.pmf.parkmini.data.Stats

@Composable
fun StatsHeader(stats: Stats, isOffline: Boolean, driver: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Free: ${stats.free} / ${stats.total}", style = MaterialTheme.typography.headlineSmall)
            LinearProgressIndicator(
                progress = { stats.occupancyPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Occupancy: ${stats.occupancyPercent}%  •  You are: $driver", style = MaterialTheme.typography.bodySmall)
            if (isOffline) {
                Text("No connection to the server, showing the last known state", color = MaterialTheme.colorScheme.error)
            }
            Legend()
        }
    }
}

@Composable
private fun Legend() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        for (status in SpotStatus.entries) {
            Box(Modifier.size(10.dp).background(status.color, CircleShape))
            Text(status.title, fontSize = 11.sp)
        }
    }
}

@Composable
fun SpotTile(
    spot: ParkingSpot,
    isMine: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = spot.status == SpotStatus.FREE || isMine,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = spot.status.color),
        border = if (isMine) BorderStroke(3.dp, Color.Black) else null,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = enabled && spot.status != SpotStatus.UNAVAILABLE, onClick = onClick),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(spot.label, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(if (isMine) "YOU" else spot.status.title, color = Color.White, fontSize = 12.sp)
        }
    }
}

// ---- Preview: see the component without running an emulator ----

@Preview(showBackground = true)
@Composable
private fun SpotTilePreview() {
    ParkMiniTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            for ((index, status) in SpotStatus.entries.withIndex()) {
                Box(Modifier.size(96.dp)) {
                    SpotTile(ParkingSpot(index, "A${index + 1}", "A", status), isMine = false, onClick = {})
                }
            }
        }
    }
}
```

**`FakeData.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/FakeData.kt`)

```kotlin
package rs.pmf.parkmini.ui

import rs.pmf.parkmini.data.ParkingSpot
import rs.pmf.parkmini.data.SpotStatus

/** Fake data for class 4. In class 5 the real server takes its place. */
fun fakeSpots(): List<ParkingSpot> = buildList {
    var id = 1
    for (zone in listOf("A", "B", "C")) {
        for (number in 1..6) {
            add(ParkingSpot(id = id, label = "$zone$number", zone = zone, status = SpotStatus.entries[id % 4]))
            id++
        }
    }
}
```

**`ParkingScreen.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/ParkingScreen.kt`)

```kotlin
package rs.pmf.parkmini.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rs.pmf.parkmini.data.SpotStatus
import rs.pmf.parkmini.data.toStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen() {
    // State: when it changes, Compose redraws everything that uses it.
    var spots by remember { mutableStateOf(fakeSpots()) }

    Scaffold(topBar = { TopAppBar(title = { Text("ParkMini") }) }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                StatsHeader(spots.toStats(), isOffline = false, driver = "you")
            }
            for ((zone, zoneSpots) in spots.groupBy { it.zone }) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Zone $zone", style = MaterialTheme.typography.titleMedium)
                }
                items(zoneSpots, key = { it.id }) { spot ->
                    SpotTile(
                        spot = spot,
                        isMine = false,
                        enabled = true,
                        // Only for practice: a click moves the spot to the next status, locally.
                        onClick = { spots = spots.map { if (it.id == spot.id) it.copy(status = spot.status.next()) else it } },
                    )
                }
            }
        }
    }
}

private fun SpotStatus.next() = SpotStatus.entries[(ordinal + 1) % SpotStatus.entries.size]
```

---

# ČAS 5: Ktor klijent, ViewModel i živi podaci

**Tag na početku:** `cas4` · **Na kraju:** `cas5`

**Cilj:** aplikacija prikazuje **živo stanje sa servera**.

| Min | Segment | Šta kucaš |
|---|---|---|
| 0–5 | **Arhitektura** | `Screen → ViewModel → ParkingApi → Backend`. |
| 5–15 | **`ParkingApi`** | `HttpClient(Android) { install(ContentNegotiation) { json() } }`, `suspend fun getSpots()`. `10.0.2.2` je računar iz emulatora. |
| 15–30 | **`UiState` i `ViewModel`** | `sealed interface ParkingUiState { Loading, Error, Content }`, `MutableStateFlow`, `viewModelScope.launch { while (true) { refresh(); delay(3.seconds) } }`. |
| 30–38 | **Veza sa UI-jem** | `collectAsStateWithLifecycle()` + `when (val s = state)`. |
| 38–45 | **Pokretanje** | Server + aplikacija: mesta menjaju boju dok senzori rade. Ugasi server: baner "No connection"; pokreni: oporavak. |

**"Wow" trenutak:** ceo razred gleda boje koje se menjaju zbog korutine na serveru.
**Zamka:** `CancellationException` (u `catch` prvo `catch (e: CancellationException) { throw e }`). Pomeni jednom.
**Ako kasniš:** baner "No connection" pokaži samo ugašenim serverom.

**`ParkingApi.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/data/ParkingApi.kt`)

```kotlin
package rs.pmf.parkmini.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** An error returned by our backend (e.g. 409: the spot is already taken). */
class ApiException(message: String) : Exception(message)

class ParkingApi(private val baseUrl: String = BASE_URL) {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getSpots(): List<ParkingSpot> = client.get("$baseUrl/api/spots").parse()

    suspend fun getStats(): Stats = client.get("$baseUrl/api/spots/stats").parse()

    suspend fun reserve(id: Int, driver: String): ParkingSpot = postAction(id, "reserve", driver)

    suspend fun confirm(id: Int, driver: String): ParkingSpot = postAction(id, "confirm", driver)

    suspend fun release(id: Int, driver: String): ParkingSpot = postAction(id, "release", driver)

    private suspend fun postAction(id: Int, action: String, driver: String): ParkingSpot =
        client.post("$baseUrl/api/spots/$id/$action") {
            contentType(ContentType.Application.Json)
            setBody(DriverRequest(driver))
        }.parse()

    private suspend inline fun <reified T> HttpResponse.parse(): T {
        if (status.isSuccess()) return body()
        // The server answers errors with a short plain text, e.g. "Spot A2 is not free (RESERVED)".
        val message = bodyAsText().ifBlank { "Error ${status.value}" }
        throw ApiException(message)
    }

    companion object {
        // 10.0.2.2 is the address of the computer on which the emulator runs.
        // On a real phone use the IP address of the computer, e.g. "http://192.168.0.15:8080".
        const val BASE_URL = "http://10.0.2.2:8080"
    }
}
```

**`ParkingViewModel.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/ParkingViewModel.kt`)

```kotlin
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
```

**Što se menja u `ParkingScreen.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/ParkingScreen.kt`):

```diff
@@ -2,5 +2,7 @@ package rs.pmf.parkmini.ui
 
 import androidx.compose.foundation.layout.Arrangement
+import androidx.compose.foundation.layout.Box
 import androidx.compose.foundation.layout.PaddingValues
+import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.lazy.grid.GridCells
@@ -8,4 +10,5 @@ import androidx.compose.foundation.lazy.grid.GridItemSpan
 import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
 import androidx.compose.foundation.lazy.grid.items
+import androidx.compose.material3.CircularProgressIndicator
 import androidx.compose.material3.ExperimentalMaterial3Api
 import androidx.compose.material3.MaterialTheme
@@ -15,46 +18,52 @@ import androidx.compose.material3.TopAppBar
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.getValue
-import androidx.compose.runtime.mutableStateOf
-import androidx.compose.runtime.remember
-import androidx.compose.runtime.setValue
+import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
+import androidx.compose.ui.text.style.TextAlign
 import androidx.compose.ui.unit.dp
-import rs.pmf.parkmini.data.SpotStatus
-import rs.pmf.parkmini.data.toStats
+import androidx.lifecycle.compose.collectAsStateWithLifecycle
+import androidx.lifecycle.viewmodel.compose.viewModel
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
-fun ParkingScreen() {
-    // State: when it changes, Compose redraws everything that uses it.
-    var spots by remember { mutableStateOf(fakeSpots()) }
+fun ParkingScreen(viewModel: ParkingViewModel = viewModel()) {
+    val state by viewModel.uiState.collectAsStateWithLifecycle()
 
     Scaffold(topBar = { TopAppBar(title = { Text("ParkMini") }) }) { padding ->
-        LazyVerticalGrid(
-            columns = GridCells.Fixed(3),
-            modifier = Modifier.padding(padding),
-            contentPadding = PaddingValues(16.dp),
-            horizontalArrangement = Arrangement.spacedBy(12.dp),
-            verticalArrangement = Arrangement.spacedBy(12.dp),
-        ) {
+        Box(Modifier.padding(padding).fillMaxSize()) {
+            // when over a sealed type: the compiler checks that we handled every state.
+            when (val s = state) {
+                ParkingUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
+                is ParkingUiState.Error -> Text(
+                    text = s.message,
+                    textAlign = TextAlign.Center,
+                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
+                )
+                is ParkingUiState.Content -> ParkingContent(s, viewModel.driver)
+            }
+        }
+    }
+}
+
+@Composable
+private fun ParkingContent(content: ParkingUiState.Content, driver: String) {
+    LazyVerticalGrid(
+        columns = GridCells.Fixed(3),
+        contentPadding = PaddingValues(16.dp),
+        horizontalArrangement = Arrangement.spacedBy(12.dp),
+        verticalArrangement = Arrangement.spacedBy(12.dp),
+    ) {
+        item(span = { GridItemSpan(maxLineSpan) }) {
+            StatsHeader(content.stats, content.isOffline, driver)
+        }
+        for ((zone, zoneSpots) in content.spots.groupBy { it.zone }) {
             item(span = { GridItemSpan(maxLineSpan) }) {
-                StatsHeader(spots.toStats(), isOffline = false, driver = "you")
+                Text("Zone $zone", style = MaterialTheme.typography.titleMedium)
             }
-            for ((zone, zoneSpots) in spots.groupBy { it.zone }) {
-                item(span = { GridItemSpan(maxLineSpan) }) {
-                    Text("Zone $zone", style = MaterialTheme.typography.titleMedium)
-                }
-                items(zoneSpots, key = { it.id }) { spot ->
-                    SpotTile(
-                        spot = spot,
-                        isMine = false,
-                        enabled = true,
-                        // Only for practice: a click moves the spot to the next status, locally.
-                        onClick = { spots = spots.map { if (it.id == spot.id) it.copy(status = spot.status.next()) else it } },
-                    )
-                }
+            items(zoneSpots, key = { it.id }) { spot ->
+                // Actions (reservation) come in class 6.
+                SpotTile(spot = spot, isMine = spot.reservedBy == driver, onClick = {})
             }
         }
     }
 }
-
-private fun SpotStatus.next() = SpotStatus.entries[(ordinal + 1) % SpotStatus.entries.size]
```

---

# ČAS 6: Akcije, dijalog i završnica

**Tag na početku:** `cas5` · **Na kraju:** `cas6` (aplikacija gotova)

| Min | Segment | Šta kucaš |
|---|---|---|
| 0–12 | **`reserve` u ViewModel-u** | `reserve(spot)` + zajednički `act(successMessage) { ... }` (lambda kao parametar), `ApiException` za 409. |
| 12–25 | **Dijalog** | `SpotDialog` sa `when (spot.status)`: FREE → *Reserve*. `selectedId` umesto celog objekta. **Napomena:** dijalog se zatvara ako senzor promeni mesto (`takeIf`). |
| 25–32 | **Gotov kod** | Potvrda, otkazivanje, snackbar (`Channel<String>`): isti obrazac kao `reserve`. |
| 32–38 | **Dva uređaja** (demo predavača) | Dva uređaja: oba pokušaju isto mesto, jedan dobija poruku `Spot A2 is not free`; drugi vidi promenu u roku od 3 s. |
| 38–45 | **Most ka projektu** | Spark pored ParkMini-ja: ista struktura. **Backend opcije:** Ktor (ovo), Spring Boot (odlična Kotlin podrška), .NET. Isti REST API radi sa bilo kojim. |

**Ako kasniš:** "Dva uređaja" pokaži sama, a rezime skrati na 3 minuta.
**Izazovi:** filter po zoni (chip-ovi), istorija rezervacija, baza (Exposed + SQLite), WebSocket umesto pollinga.

**Što se menja u `ParkingViewModel.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/ParkingViewModel.kt`):

```diff
@@ -5,4 +5,5 @@ import androidx.lifecycle.viewModelScope
 import kotlinx.coroutines.CancellationException
 import kotlinx.coroutines.async
+import kotlinx.coroutines.channels.Channel
 import kotlinx.coroutines.coroutineScope
 import kotlinx.coroutines.delay
@@ -10,6 +11,8 @@ import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.StateFlow
 import kotlinx.coroutines.flow.asStateFlow
+import kotlinx.coroutines.flow.receiveAsFlow
 import kotlinx.coroutines.flow.update
 import kotlinx.coroutines.launch
+import rs.pmf.parkmini.data.ApiException
 import rs.pmf.parkmini.data.ParkingApi
 import rs.pmf.parkmini.data.ParkingSpot
@@ -36,4 +39,8 @@ class ParkingViewModel(private val api: ParkingApi = ParkingApi()) : ViewModel()
     val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()
 
+    // One-time messages (snackbar) are not "state", so they do not go into uiState.
+    private val _messages = Channel<String>(Channel.BUFFERED)
+    val messages = _messages.receiveAsFlow()
+
     init {
         viewModelScope.launch {
@@ -45,4 +52,26 @@ class ParkingViewModel(private val api: ParkingApi = ParkingApi()) : ViewModel()
     }
 
+    fun reserve(spot: ParkingSpot) = act("Reserved spot ${spot.label}") { api.reserve(spot.id, driver) }
+
+    fun confirmArrival(spot: ParkingSpot) = act("Arrival confirmed: ${spot.label}") { api.confirm(spot.id, driver) }
+
+    fun release(spot: ParkingSpot) = act("Spot ${spot.label} is free again") { api.release(spot.id, driver) }
+
+    private fun act(successMessage: String, call: suspend () -> ParkingSpot) {
+        viewModelScope.launch {
+            try {
+                call()
+                _messages.send(successMessage)
+                refresh()
+            } catch (e: CancellationException) {
+                throw e
+            } catch (e: ApiException) {
+                _messages.send(e.message ?: "Error")
+            } catch (e: Exception) {
+                _messages.send("No connection to the server")
+            }
+        }
+    }
+
     private suspend fun refresh() {
         try {
```

**`SpotDialog.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/SpotDialog.kt`)

```kotlin
package rs.pmf.parkmini.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import rs.pmf.parkmini.data.ParkingSpot
import rs.pmf.parkmini.data.SpotStatus

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
            val minutes = ((spot.reservedUntil ?: 0L) - System.currentTimeMillis()) / 60_000 + 1
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
```

**Što se menja u `ParkingScreen.kt`** (`android/app/src/main/kotlin/rs/pmf/parkmini/ui/ParkingScreen.kt`):

```diff
@@ -14,8 +14,14 @@ import androidx.compose.material3.ExperimentalMaterial3Api
 import androidx.compose.material3.MaterialTheme
 import androidx.compose.material3.Scaffold
+import androidx.compose.material3.SnackbarHost
+import androidx.compose.material3.SnackbarHostState
 import androidx.compose.material3.Text
 import androidx.compose.material3.TopAppBar
 import androidx.compose.runtime.Composable
+import androidx.compose.runtime.LaunchedEffect
 import androidx.compose.runtime.getValue
+import androidx.compose.runtime.mutableStateOf
+import androidx.compose.runtime.remember
+import androidx.compose.runtime.setValue
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
@@ -24,4 +30,6 @@ import androidx.compose.ui.unit.dp
 import androidx.lifecycle.compose.collectAsStateWithLifecycle
 import androidx.lifecycle.viewmodel.compose.viewModel
+import rs.pmf.parkmini.data.ParkingSpot
+import rs.pmf.parkmini.data.SpotStatus
 
 @OptIn(ExperimentalMaterial3Api::class)
@@ -29,8 +37,15 @@ import androidx.lifecycle.viewmodel.compose.viewModel
 fun ParkingScreen(viewModel: ParkingViewModel = viewModel()) {
     val state by viewModel.uiState.collectAsStateWithLifecycle()
+    val snackbarHostState = remember { SnackbarHostState() }
 
-    Scaffold(topBar = { TopAppBar(title = { Text("ParkMini") }) }) { padding ->
+    LaunchedEffect(Unit) {
+        viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
+    }
+
+    Scaffold(
+        topBar = { TopAppBar(title = { Text("ParkMini") }) },
+        snackbarHost = { SnackbarHost(snackbarHostState) },
+    ) { padding ->
         Box(Modifier.padding(padding).fillMaxSize()) {
-            // when over a sealed type: the compiler checks that we handled every state.
             when (val s = state) {
                 ParkingUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
@@ -40,5 +55,11 @@ fun ParkingScreen(viewModel: ParkingViewModel = viewModel()) {
                     modifier = Modifier.align(Alignment.Center).padding(32.dp),
                 )
-                is ParkingUiState.Content -> ParkingContent(s, viewModel.driver)
+                is ParkingUiState.Content -> ParkingContent(
+                    content = s,
+                    driver = viewModel.driver,
+                    onReserve = viewModel::reserve,
+                    onConfirm = viewModel::confirmArrival,
+                    onRelease = viewModel::release,
+                )
             }
         }
@@ -47,5 +68,18 @@ fun ParkingScreen(viewModel: ParkingViewModel = viewModel()) {
 
 @Composable
-private fun ParkingContent(content: ParkingUiState.Content, driver: String) {
+private fun ParkingContent(
+    content: ParkingUiState.Content,
+    driver: String,
+    onReserve: (ParkingSpot) -> Unit,
+    onConfirm: (ParkingSpot) -> Unit,
+    onRelease: (ParkingSpot) -> Unit,
+) {
+    // We remember only the id, so the dialog always shows the freshest state of the spot.
+    var selectedId by remember { mutableStateOf<Int?>(null) }
+    // The sensors can change a spot while the dialog is open. If the spot is no longer free (and is not ours),
+    // there is nothing to offer, so the dialog disappears.
+    val selected = content.spots.firstOrNull { it.id == selectedId }
+        ?.takeIf { it.status == SpotStatus.FREE || it.reservedBy == driver }
+
     LazyVerticalGrid(
         columns = GridCells.Fixed(3),
@@ -62,8 +96,22 @@ private fun ParkingContent(content: ParkingUiState.Content, driver: String) {
             }
             items(zoneSpots, key = { it.id }) { spot ->
-                // Actions (reservation) come in class 6.
-                SpotTile(spot = spot, isMine = spot.reservedBy == driver, onClick = {})
+                SpotTile(
+                    spot = spot,
+                    isMine = spot.reservedBy == driver,
+                    onClick = { selectedId = spot.id },
+                )
             }
         }
     }
+
+    selected?.let { spot ->
+        SpotDialog(
+            spot = spot,
+            isMine = spot.reservedBy == driver,
+            onReserve = { onReserve(spot) },
+            onConfirm = { onConfirm(spot) },
+            onRelease = { onRelease(spot) },
+            onDismiss = { selectedId = null },
+        )
+    }
 }
```


---

# Bonus (opciono): RPC varijanta

U `~/IdeaProjects/kotlin-uvod-pmu/parkmini/` postoji verzija sa **kotlinx-rpc** (server implementira Kotlin interfejs, telefon poziva njegove funkcije; server sam šalje promene preko `Flow`). Može da posluži kao **pet-minutni bonus na kraju 6. časa**. Ograničenja: biblioteka je pre 1.0, i klijent i server moraju biti u Kotlinu, pa REST znanje ostaje univerzalno.
