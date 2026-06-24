# GymLog — Krafttraining-Tracker (Android)

Native Android-App zum Tracken von Krafttraining: Übungen verwalten, Sätze
(Gewicht × Wiederholungen) eintragen, Fortschritt in Diagrammen verfolgen und
Trainingspläne pro Wochentag anlegen. Mit einem leichten Gamification-Layer
(Trainings-Streak und „Neuer-Rekord"-Feedback).

> Hochschulprojekt (TH Nürnberg) · Package `de.th.nuernberg.bme.gymlog` · Java · Min SDK 24 / Target 36

---

## Schnellstart

```bash
# Debug-APK bauen
./gradlew assembleDebug

# Auf verbundenem Gerät/Emulator installieren
./gradlew installDebug

# JVM-Unit-Tests
./gradlew test

# Instrumentierte Tests (Emulator/Gerät nötig)
./gradlew connectedAndroidTest
```

In Android Studio: Projekt öffnen → **Run ▶**. Eine `local.properties` mit dem
SDK-Pfad wird von Android Studio automatisch erzeugt (nicht eingecheckt).

---

## Funktionsumfang

| Tab | Funktion |
|-----|----------|
| **Start** | Wochentag, Datum, heutiger Satz-Count, letzte 10 Einträge, **Streak-Badge** |
| **Eintragen** | Satz erfassen (Übung, Gewicht, Wdh., Datum); Swipe-to-Delete + Undo; **„Neuer PR!"-Feedback** |
| **Übungen** | Übungen anlegen/löschen (mit Bestätigungsdialog inkl. Anzahl betroffener Sätze); Navigation zur Statistik |
| **Statistik** | Bestleistung + Verlaufs-Diagramm je Übung (MPAndroidChart) |
| **Plan** | Trainingspläne anlegen, Übungen je Wochentag zuordnen |

Optik: „Liquid Glass"-Designsystem — Dark Mode, Terracotta-Akzent, Glass-Cards,
floatende Blur-Bottom-Navigation.

---

## Abgabe-Dokumente

| Deliverable | Datei |
|-------------|-------|
| Anforderungen (final) | [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md) |
| Design- & Architektur-Zusammenfassung | [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) |
| Testbericht | [docs/TEST_REPORT.md](docs/TEST_REPORT.md) |
| Release Notes / Changelog | [docs/CHANGELOG.md](docs/CHANGELOG.md) |
| Bekannte Einschränkungen | [docs/KNOWN_LIMITATIONS.md](docs/KNOWN_LIMITATIONS.md) |
| KI-Nutzungs-Reflexion | [docs/AI_REFLECTION.md](docs/AI_REFLECTION.md) |
| Entwickler-Leitfaden (Konventionen) | [CLAUDE.md](CLAUDE.md) |
| Design-Spec & Mockups | [docs/superpowers/specs/](docs/superpowers/specs/) · [docs/mockups/](docs/mockups/) |

---

## Tech-Stack

Java · MVVM + Repository · Room (SQLite, Schema v2) · LiveData · Navigation
Component · ViewBinding · MPAndroidChart · BlurView · Material 3.
Keine externe DI, keine Coroutines/RxJava.
