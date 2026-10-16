# Instalacija i okruženje: šta treba, i da li sve može u IntelliJ-u

## Kratak odgovor

**Da, sve može u IntelliJ IDEA-i**: backend (Ktor), Postman/HTTP zahtevi i Android (emulator, Logcat, Compose preview). Potreban je **Android plugin** (JetBrains) i **Android SDK**. Android Studio je rezerva (isti IDE).

Ceo projekat je **jedan Gradle projekat** (Kotlin Multiplatform) sa tri modula. Otvaraš **koren `parkmini/`** (`File → Open`), jednom:

| Modul | Šta je | Časovi |
|---|---|---|
| `shared` | modeli koje koriste i server i aplikacija (`ParkingSpot`, `SpotStatus`...) | 2 (Task 1) |
| `backend` | Ktor server | 2 i 3 |
| `android` | Compose aplikacija | 4 do 6 |

Zato je **Android SDK potreban i za časove servera** (Gradle ga traži čim učita projekat): instaliraš ga u domaćem 0.

---

## 1. Stanje na tvom računaru (provereno 5. oktobra)

| Stavka | Stanje |
|---|---|
| IntelliJ IDEA | 2026.2.3, **Android plugin instaliran** |
| JDK | 24 (radi; za studente preporuka 21) |
| Android SDK | `platforms/android-36`, `build-tools`, `platform-tools`, **`cmdline-tools` (dodato)**, **system image Android 36 ARM64 (dodato)** |
| Emulator | virtuelni uređaj **`ParkMini_Pixel`** (Pixel 6, Android 16) je napravljen. U IntelliJ-u ga vidiš u **Device Manager**. |
| Gradle | `gradlew` sam skida Gradle 9.3.0 |

Kako pokrećeš emulator iz IntelliJ-a: **View → Tool Windows → Device Manager** → `ParkMini_Pixel` → ▶. Ili iz terminala:

```bash
~/Library/Android/sdk/emulator/emulator -avd ParkMini_Pixel
```

## 2. Koraci za STUDENTE ("domaći 0", nedelju dana pre prvog časa)

1. **IntelliJ IDEA**. Za Android: `Settings → Plugins → Marketplace → "Android"` → Install.
2. **JDK 21**: `File → Project Structure → SDKs → + → Download JDK → Eclipse Temurin 21`.
3. **Android SDK**: `Settings → Languages & Frameworks → Android SDK` → SDK Platform *Android 16 (API 36)* i *Build-Tools*.
4. **Emulator ili telefon.** Emulator: `Device Manager → Create Virtual Device → Pixel`, slika *Google APIs, API 36* (ARM 64 na Apple Silicon, x86_64 na Windows/Intel). Na **Windows-u** emulator traži uključen *Windows Hypervisor Platform* i virtuelizaciju u BIOS-u, pa je USB telefon često lakši.
5. **Preuzmi projekat:**

```bash
git clone <adresa repozitorijuma> parkmini
cd parkmini
```

6. **Proveri backend:** otvori koren `parkmini` (jedan prozor), u `Settings → Build Tools → Gradle → Gradle JVM` izaberi JDK 21, sačekaj sync, pokreni `Application.kt` (zeleni trougao pored `main`). U browseru `http://localhost:8080/` treba da piše `ParkMini API works.`
7. **Proveri Android:** u istom prozoru izaberi konfiguraciju **`android`** (gore, pored zelenog trougla) i pokreni je na emulatoru/telefonu (backend mora da radi): treba da se vidi lista parking mesta.
8. **Obavezno kod kuće**: prvi sync skida stotine MB; na fakultetskoj mreži bi pojeo početak časa.

### Komandna linija (ako IntelliJ pravi problem)

```bash
# sve se radi iz korena parkmini/ (treba ANDROID_HOME ili local.properties sa sdk.dir)
./gradlew :backend:run            # server na portu 8080   (Windows: gradlew.bat :backend:run)
./gradlew :android:assembleDebug  # aplikacija
```

## 3. Kako se emulator/telefon povezuje sa serverom

| Uređaj | Adresa servera (`BASE_URL` u `ParkingApi.kt`) |
|---|---|
| Android emulator | `http://10.0.2.2:8080` (već podrazumevano; `10.0.2.2` je računar na kome radi emulator) |
| Pravi telefon | `http://<IP računara>:8080` u istoj Wi-Fi mreži |

Server sluša na portu 8080. Prvi put firewall pita da li dozvoljavaš dolazne konekcije: **dozvoli**. Ako 8080 zauzima nešto drugo, promeni port u `Application.kt` **i** u `ParkingApi.kt`.

## 4. Provera pre prvog časa (30 minuta)

- [ ] Backend: `Application.kt` se pokreće, `requests.http` zahtevi 1 do 7 prolaze, WebSocket (zahtev 8) šalje listu
- [ ] Android: `android` na emulatoru prikazuje mesta koja **sama menjaju boju** (senzori, preko WebSocket-a)
- [ ] Parkiranje: klik na slobodno mesto → unesi tablicu (npr. `KG-123-AB`) → *Park here* → mesto je crveno sa oznakom *YOU*; zatim *Leave spot* → zeleno
- [ ] Neispravna tablica (npr. `KG-12-AB`): polje je crveno, dugme *Park here* je isključeno
- [ ] Ugasiš server: baner *No connection*; pokreneš: oporavlja se sam
- [ ] `localhost:8080/` u browseru piše `ParkMini API works.`

## 5. Poznata ograničenja i rizici

| Rizik | Napomena |
|---|---|
| Verzije su zaključane (Kotlin 2.3.21, Ktor 3.6.0, Gradle 9.3.0, AGP 8.13.1, `compileSdk` 36) | Ne diži ih usred kursa; novije biblioteke traže AGP 9 i `compileSdk` 37. |
| IntelliJ prikazuje upozorenja o ranjivostima (Ktor/Netty, logback) | Za lokalni demo server nisu relevantna. Pominje se studentima kao primer. |
| Android plugin kod studenata | Proveri u domaćem 0. Rezerva: Android Studio. |
| Emulator je spor ili ne radi | USB telefon; ili neka studenti gledaju tvoj ekran. |
| Gradle JVM u IntelliJ-u pokazuje na pogrešan JDK | `Settings → Build Tools → Gradle → Gradle JVM`: JDK 17 ili noviji. |
| Senzori menjaju mesto dok je dijalog otvoren | Rešeno: dijalog se zatvara ako mesto više nije slobodno. |
| U emulatoru ne može da se kuca: umesto tastature se vidi uzana traka (mikrofon, brisanje, kvačica) ili ekran „Try out your stylus“ | Emulator prijavljuje miš kao olovku, pa Gboard ulazi u režim pisanja olovkom. To nije greška aplikacije. Isključi: u emulatoru otvori `Settings → System → Keyboard → On-screen keyboard → Gboard → Handwriting` (ili iz terminala `adb shell am start -n com.google.android.inputmethod.latin/com.google.android.apps.inputmethod.latin.stylus.StylusSettingsActivity`) i ugasi **Use stylus to write in text fields**. |
