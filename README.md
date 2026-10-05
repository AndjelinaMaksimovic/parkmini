# ParkMini (REST): primer za uvod u Kotlin (backend + Android)

Mala aplikacija za parking mesta. **Backend** (Ktor, REST API) simulira senzore, a **Android** aplikacija (Jetpack Compose) prikazuje mesta u boji
(zelena / žuta / crvena / plava) i omogućava rezervaciju i potvrdu dolaska. Sve u Kotlinu, sve u IntelliJ-u.

```
backend/   Ktor REST server, simulator senzora (paket com.v1.parkmini)
android/   Compose aplikacija, ViewModel, Ktor klijent (paket rs.pmf.parkmini)
docs/      instalacija, skripta za 6 časova, tekstovi taskova
```

## Brzi start

```bash
cd backend && ./gradlew run      # server na :8080  (Windows: gradlew.bat run)
```

U browseru: <http://localhost:8080/api/spots>

Android: otvori folder `android` u IntelliJ-u i pokreni konfiguraciju `app` (emulator ili telefon). Detalji: [docs/00_INSTALACIJA.md](docs/00_INSTALACIJA.md).

## Tagovi po časovima

`cas0-start` → `cas1` → `cas2` → `cas3-start` → `cas3` → `cas4` → `cas5` → `cas6`.
`git checkout cas3` postavlja kod na kraj trećeg časa. Skripta za predavača: [docs/01_SKRIPTA_CASOVA.md](docs/01_SKRIPTA_CASOVA.md). Tekstovi taskova (engleski): [docs/02_TASKS_EN.md](docs/02_TASKS_EN.md).

## Verzije (zaključane, ne dizati usred kursa)

Kotlin 2.3.21, Ktor 3.6.0, Gradle 9.3.0, AGP 8.13.1, `compileSdk` 36, Compose BOM 2025.09.00.

## Napomena

Postoji i varijanta sa RPC-om (`kotlinx-rpc`) u `~/IdeaProjects/kotlin-uvod-pmu/parkmini/`, kao opcioni bonus.
