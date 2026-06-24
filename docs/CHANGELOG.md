# Changelog / Release Notes

Format orientiert an *Keep a Changelog*. Datumsangaben absolut.

## [1.0] — 2026-06-24 — Finale Abgabe

### Hinzugefügt
- **„Liquid Glass"-Designsystem** (Dark-only): Terracotta-Akzent, Glass-Cards mit
  Sheen + Glow, Fraunces/Inter-Typografie, zentrale Design-Tokens.
- **Floatende Bottom-Navigation** mit echtem Backdrop-Blur (BlurView 3.2.0).
- **Trainings-Streak** auf der Startseite (aufeinanderfolgende Trainingstage).
- **„Neuer PR!"-Feedback** beim Eintragen eines neuen persönlichen Rekords.
- **Bestätigungsdialog** vor dem Löschen einer Übung inkl. Anzahl betroffener Sätze (UF-01).
- Neue Utility-Klassen `StreakCalculator`, `PrDetector` (reine Funktionen, unit-getestet).
- Neue, mockup-getreue Strich-Icons für die Navigation (Home, Stift, Hantel, Kalender).
- Neue DAO-Queries: `getDistinctWorkoutDates`, `countSetsForExercise`, `getMaxWeightSync`.
- Abgabe-Dokumentation unter `docs/` (Requirements, Architektur, Testbericht,
  Known Limitations, KI-Reflexion) + Design-Spec & Mockups.

### Geändert
- Komplettes UI-Refresh aller Screens (Home, Log, Übungen, Statistik, Plan) und
  Item-/Dialog-Layouts auf den Glass-Stil.
- Statistik-Diagramm in Terracotta umgestylt (Linie, Verlaufs-Füllung, Achsen).
- Tastatur verdeckt den „Eintragen"-Bereich nicht mehr — `windowSoftInputMode=adjustResize` (UF-03).

### Behoben
- **KI-08:** `SetValidatorTest` korrigiert — Komma-Gewicht „12,5" wird als gültig getestet.
- **UF-01:** Übung-Löschen ist nicht mehr nur Swipe, sondern bestätigt mit Satz-Anzahl.
- **UF-03:** Eingabeformular bleibt bei geöffneter Tastatur sichtbar.
- Potenzieller Crash beim Lösch-Callback (`requireActivity()` im Hintergrund-Thread)
  durch null-sicheres `getActivity()` + UI-Thread-Re-Check entschärft.

### Bekannt / offen
- Fraunces-Headline rendert auf dem Testgerät als System-Schrift (Variable-Font-Fallback) —
  siehe [KNOWN_LIMITATIONS.md](KNOWN_LIMITATIONS.md) L-01.

---

## [0.2] — 2026-06-09 — Trainingspläne & DB-Härtung

### Hinzugefügt
- Trainingsplan-Tab: Pläne anlegen/löschen, Übungen je Wochentag zuordnen.
- Schema v2 mit `training_plans` + `plan_exercises` und `MIGRATION_1_2`.
- UNIQUE-Index auf `exercises.name` (verhindert Duplikate, KI-01).

### Behoben
- KI-02: `SetValidator` akzeptiert Komma als Dezimaltrennzeichen.
- KI-03: Statistik zeigt Empty-State statt leerem Chart.
- KI-04: `DateAxisFormatter` mit Bounds-Check.

---

## [0.1] — Grundgerüst

- MVVM + Repository + Room, Navigation, 4 Tabs (Home, Log, Übungen, Statistik),
  Satz-Erfassung, Bestleistung + Verlaufs-Diagramm.
