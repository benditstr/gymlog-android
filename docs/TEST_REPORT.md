# Testbericht

**Projekt:** GymLog · **Stand:** 2026-06-24 · **Build:** `assembleDebug` erfolgreich

## 1. Zusammenfassung

| Kategorie | Anzahl | Ergebnis |
|-----------|--------|----------|
| JVM-Unit-Tests | 27 | ✅ alle grün |
| Instrumentierte Tests | 8 | ⚠️ vorhanden, in dieser Abgabe nicht erneut ausgeführt (kein Gerät verbunden) |
| Build (`assembleDebug`) | — | ✅ erfolgreich |
| Manuelle Smoke-Tests (Gerät) | 4 Tabs | ✅ installiert, gestartet, Hauptflows bedienbar |

Ausführung: `./gradlew test` (JVM), `./gradlew connectedAndroidTest` (instrumentiert,
Emulator/Gerät nötig).

## 2. JVM-Unit-Tests (27, alle bestanden)

| Testklasse | Tests | Prüft |
|-----------|-------|-------|
| `SetValidatorTest` | 8 | Gewicht/Wdh.-Validierung inkl. Komma-Dezimaltrennzeichen („12,5") |
| `DateUtilsTest` | 5 | Mitternachts-Normalisierung, heute/morgen-Grenzen |
| `StreakCalculatorTest` | 9 | Streak: lückenlos, mit Lücke, heute fehlt/gestern da, leer/null, Duplikate, unsortiert |
| `PrDetectorTest` | 4 | PR-Erkennung: erster Eintrag, schwerer, leichter, gleich |
| `ExampleUnitTest` | 1 | Android-Studio-Vorlage |

## 3. Instrumentierte Tests (8, vorhanden)

| Testklasse | Tests | Prüft |
|-----------|-------|-------|
| `GymLogDatabaseTest` | 7 | Room In-Memory: Insert/Query/Delete für Exercise & WorkoutSet, FK-Cascade |
| `ExampleInstrumentedTest` | 1 | Android-Studio-Vorlage |

Hinweis: Erfordern Emulator/Gerät und wurden in der finalen Build-Umgebung dieser
Abgabe nicht erneut ausgeführt. Decken die v1-Datenbankfunktionalität ab.

## 4. Manuelle Smoke-Tests (auf physischem Gerät)

| Flow | Ergebnis |
|------|----------|
| App installieren & starten | ✅ |
| Übung anlegen / löschen (mit Bestätigungsdialog) | ✅ |
| Satz eintragen (inkl. Komma-Gewicht) | ✅ |
| Satz per Swipe löschen + Undo | ✅ |
| Startseite: Datum, Satz-Count, letzte Einträge | ✅ |
| Statistik-Diagramm öffnen | ✅ |
| Trainingsplan anlegen, Übung je Wochentag zuordnen | ✅ |
| Navigation zwischen allen 4 Tabs | ✅ |

(Screenshots der vier Haupt-Tabs liegen vor; Optik entspricht dem Designsystem.)

## 5. Testlücken (ehrlich dokumentiert)

- Keine automatisierten Tests für `MIGRATION_1_2`, `TrainingPlanDao` und die neuen
  DAO-Queries (`getDistinctWorkoutDates`, `countSetsForExercise`, `getMaxWeightSync`).
- Keine UI-/Espresso-Tests; UI-Flows nur manuell verifiziert.
- Der BlurView-Fallback auf API < 31 wurde nicht auf realer Alt-Hardware geprüft.

Siehe [KNOWN_LIMITATIONS.md](KNOWN_LIMITATIONS.md).
