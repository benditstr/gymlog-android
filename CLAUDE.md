# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Projektübersicht

Native Android-App zum Tracken von Krafttraining. Nutzer können Übungen verwalten, Sätze (Gewicht × Wiederholungen) eintragen, ihre Fortschritte in Diagrammen verfolgen und Trainingspläne pro Wochentag anlegen.

- **Package:** `de.th.nuernberg.bme.gymlog`
- **Sprache:** Java (kein Kotlin)
- **minSdk:** 24 | **targetSdk:** 36
- **Build:** Gradle mit `annotationProcessor` (kein KAPT)
- **Java-Quell-/Ziel-Kompatibilität:** 11 (`compileOptions` in `app/build.gradle.kts`)

---

## Build & Befehle

Alle Befehle vom Projekt-Root (`./gradlew`, unter Windows `gradlew.bat`):

| Zweck | Befehl |
|-------|--------|
| Debug-APK bauen | `./gradlew assembleDebug` |
| Auf Gerät/Emulator installieren | `./gradlew installDebug` |
| Alle JVM-Unit-Tests | `./gradlew test` |
| Eine Testklasse | `./gradlew test --tests "de.th.nuernberg.bme.gymlog.util.SetValidatorTest"` |
| Eine Testmethode | `./gradlew test --tests "de.th.nuernberg.bme.gymlog.util.SetValidatorTest.isWeightValid_validWeights_returnsTrue"` |
| Instrumented Tests (Emulator/Gerät nötig) | `./gradlew connectedAndroidTest` |
| Kompilieren ohne Tests (schnelle Validierung) | `./gradlew compileDebugJavaWithJavac` |
| Clean-Build | `./gradlew clean assembleDebug` |

**Gotcha — Version-Catalog wird umgangen:** `gradle/libs.versions.toml` existiert, aber nur das AGP-Plugin wird daraus referenziert. Alle App-Abhängigkeiten sind in `app/build.gradle.kts` **hartkodiert** (z. B. `material:1.12.0`, nicht das `1.13.0` aus dem Catalog). Beim Versionswechsel die `build.gradle.kts`-Strings ändern, nicht die TOML. MPAndroidChart kommt über das **JitPack**-Repo (in `settings.gradle.kts`).

---

## Architektur

MVVM + Repository Pattern, keine externe DI:

```
UI (Fragment + ViewBinding)
    └── ViewModel (LiveData)
        └── Repository
            └── Room DAO
                └── SQLite (Room)
```

- Fragments beobachten LiveData aus dem ViewModel
- ViewModels halten keine Android-Referenzen
- Repositories kapseln DB-Zugriff; async via `ExecutorService.newSingleThreadExecutor()`
- Kein Coroutines, kein RxJava — rein LiveData-basiert

---

## Projektstruktur

```
app/src/main/java/de/th/nuernberg/bme/gymlog/
├── MainActivity.java              # Bottom Navigation, NavController
├── database/
│   ├── GymLogDatabase.java        # Room Singleton, Schema v2, MIGRATION_1_2
│   ├── dao/
│   │   ├── ExerciseDao.java
│   │   ├── WorkoutSetDao.java
│   │   └── TrainingPlanDao.java   # CRUD Pläne/Plan-Übungen, getExercisesForPlan (JOIN)
│   └── entity/
│       ├── Exercise.java          # id, name (UNIQUE-Index)
│       ├── WorkoutSet.java        # id, exerciseId (FK CASCADE), weight, reps, date (Unix ms)
│       ├── TrainingPlan.java      # id, name
│       └── PlanExercise.java      # id, planId (FK CASCADE), exerciseId (FK CASCADE), dayOfWeek
├── model/
│   ├── MaxWeightPerDay.java       # Projektion für Statistik-Chart
│   ├── WorkoutSetWithExercise.java
│   └── PlanExerciseWithName.java  # Projektion: Plan-Übung + Übungsname
├── repository/
│   ├── ExerciseRepository.java    # fängt SQLiteConstraintException (Duplikat-Name) ab
│   ├── WorkoutSetRepository.java
│   └── TrainingPlanRepository.java
├── ui/
│   ├── home/                      # Tab 1: Wochentag + Datum + heutiger Satz-Count + letzte 10 Sätze
│   ├── log/                       # Tab 2: Satz erfassen, Swipe-to-Delete + Undo-Snackbar
│   ├── exercises/                 # Tab 3: Übungsverwaltung, Navigation zu Statistik-Detail
│   ├── statistics/                # Statistik-Detailansicht (von Tab 3 aus), DateAxisFormatter
│   └── plan/                      # Tab 4: Trainingsplan (Plan anlegen, Übungen je Wochentag)
└── util/
    ├── DateUtils.java             # todayMidnight(), tomorrowMidnight(), normalisierung
    ├── SetValidator.java          # Eingabevalidierung (Gewicht, Wiederholungen)
    ├── StreakCalculator.java      # reine Funktion: aufeinanderfolgende Trainingstage (Fun-Layer)
    └── PrDetector.java            # reine Funktion: isNewPr() für PR-Celebration (Fun-Layer)
```

---

## Datenmodell (Schema v2)

| Entity | Felder |
|--------|--------|
| `Exercise` | `id` (autoGen), `name` (NonNull, **UNIQUE-Index**, KI-01 behoben) |
| `WorkoutSet` | `id` (autoGen), `exerciseId` (FK → Exercise, CASCADE), `weight` (float), `reps` (int), `date` (long, Unix-ms) |
| `TrainingPlan` | `id` (autoGen), `name` (NonNull) |
| `PlanExercise` | `id` (autoGen), `planId` (FK → TrainingPlan, CASCADE), `exerciseId` (FK → Exercise, CASCADE), `dayOfWeek` (int) |

- `MIGRATION_1_2` in `GymLogDatabase.java`: legt UNIQUE-Index auf `exercises.name` an, erstellt `training_plans` und `plan_exercises`
- Dezimaltrennzeichen: `SetValidator.isWeightValid()` normalisiert **Komma → Punkt** im Code (`"12,5"` → `"12.5"`). Der bestehende Test `SetValidatorTest` ist damit aber inkonsistent — siehe KI-08
- Kein `muscleGroup`-Feld — bewusst aus MVP-Scope herausgelassen (KI-05)
- Keine `UNIQUE`-Constraint auf `(planId, exerciseId, dayOfWeek)` — dieselbe Übung kann mehrfach pro Wochentag zugeordnet werden (KI-06)

---

## Navigation (4 Tabs)

| Tab | Inhalt |
|-----|--------|
| 1 | Home (Start) — Wochentag, Datum, heutiger Satz-Count, letzte 10 Sätze |
| 2 | Eintragen (Log) — Satz erfassen |
| 3 | Übungen — Verwaltung + Navigation (`action_exercises_to_statistics`) zur Statistik-Detailansicht |
| 4 | Trainingsplan — Plan anlegen/löschen, Übungen je Wochentag zuordnen |

Hinweis: Die Startseite (Tab 1) zeigt **noch keine** für heute geplanten Übungen aus dem Trainingsplan — Tab 4 ist eigenständig, noch nicht mit Tab 1 verknüpft (offene Erweiterung, siehe Backlog).

---

## Abhängigkeiten (key)

| Bibliothek | Zweck |
|-----------|-------|
| `androidx.room:room-runtime:2.7.0` | Lokale SQLite-Datenbank |
| `androidx.lifecycle:lifecycle-viewmodel:2.8.7` | ViewModel |
| `androidx.lifecycle:lifecycle-livedata:2.8.7` | reaktive UI-Updates |
| `androidx.navigation:navigation-fragment:2.8.9` | Fragment-Navigation |
| `com.github.PhilJay:MPAndroidChart:v3.1.0` | Fortschritts-Diagramme |
| `com.github.Dimezis:BlurView:version-3.2.0` | Echter Backdrop-Blur für die floatende Bottom-Nav (Liquid-Glass-Redesign) |
| `com.google.android.material:material:1.12.0` | Material Design |

**Fonts:** `res/font/fraunces.ttf` (Headlines) + `res/font/inter.ttf` (Body), beide Variable Fonts (SIL OFL), direkt referenziert. Betonung über `textStyle`/`textFontWeight` (Variable-Axis ab API 26, sonst Fallback).

---

## Tests (34 gesamt, davon 32 fachlich)

| Testklasse | Typ | Anzahl | Hinweis |
|-----------|-----|--------|---------|
| `SetValidatorTest` | JVM Unit | 8 | KI-08 behoben (`"12,5"` jetzt gültig) |
| `DateUtilsTest` | JVM Unit | 5 | |
| `StreakCalculatorTest` | JVM Unit | 9 | Streak-Logik (lückenlos, Lücke, heute fehlt/gestern da, leer, Duplikate, unsortiert) |
| `PrDetectorTest` | JVM Unit | 4 | PR-Erkennung (erster Eintrag, schwerer, leichter, gleich) |
| `GymLogDatabaseTest` | Instrumented (Room In-Memory) | 7 | deckt v1-Funktionalität ab (Exercise/WorkoutSet) |
| `ExampleUnitTest` / `ExampleInstrumentedTest` | Platzhalter | 2 | Android-Studio-Vorlage, ohne fachlichen Bezug |

JVM-Tests: `./gradlew test` (zuletzt: 27 JVM-Tests grün)
Instrumented Tests: `./gradlew connectedAndroidTest` (Emulator/Gerät nötig)

**Lücke:** Kein Test für `MIGRATION_1_2`, `TrainingPlanDao`, die UNIQUE-Constraint nach Migration oder die neuen DAO-Queries (`getDistinctWorkoutDates`, `countSetsForExercise`, `getMaxWeightSync`).

---

## Known Issues

| ID | Schwere | Beschreibung | Status |
|----|---------|-------------|--------|
| KI-01 | Medium | Kein `UNIQUE`-Constraint auf `exercises.name` → Duplikate möglich | ✅ Behoben (Migration v2 + Repository fängt `SQLiteConstraintException`) |
| KI-02 | Low | `SetValidator` lehnt Komma ab (`"12,5"`) | ✅ Code behoben (Komma→Punkt-Normalisierung), aber siehe KI-08 |
| KI-03 | Low | `StatisticsFragment` zeigt leeres Chart ohne Hinweis wenn keine Übungen | ✅ Behoben (Empty-State-View `tv_empty_stats`) |
| KI-04 | Low | `DateAxisFormatter` fällt auf Index-Nummern zurück wenn kein Datum passt | ✅ Behoben (Bounds-Check in `getFormattedValue()`) |
| KI-05 | Info | `Exercise` hat kein `muscleGroup`-Feld | Offen — Backlog |
| KI-06 | Low | Im Plan-Detail kann dieselbe Übung einem Wochentag mehrfach zugeordnet werden (`showAddExerciseDialog()` filtert nicht) | Offen — UNIQUE-Constraint `(planId, exerciseId, dayOfWeek)` für v3 geplant |
| KI-08 | Low | `SetValidatorTest` erwartete `assertFalse(isWeightValid("12,5"))`, der Code liefert wegen KI-02-Fix aber `true` | ✅ Behoben (`"12,5"` nach `isWeightValid_validWeights_returnsTrue` verschoben) |

---

## Usability-Findings (offen)

| ID | Schwere | Beschreibung |
|----|---------|-------------|
| UF-01 | ✅ Behoben | Übung löschen zeigt jetzt einen Glass-Bestätigungsdialog (`MaterialAlertDialogBuilder` + `ThemeOverlay.GymLog.Dialog`) mit Anzahl betroffener Sätze (`WorkoutSetDao.countSetsForExercise` async); kein Direkt-Delete mehr |
| UF-03 | ✅ Behoben | `android:windowSoftInputMode="adjustResize"` im Manifest gesetzt — Form bleibt über der Tastatur sichtbar (RecyclerView schrumpft) |

---

## Nächste Schritte (Backlog)

1. ~~KI-08, UF-01, UF-03~~ — ✅ im Liquid-Glass-Redesign erledigt
2. **Home ↔ Trainingsplan** — Startseite um „heute laut Plan vorgesehene Übungen" erweitern (LiveData-Query auf `TrainingPlanDao`)
3. **KI-06** — UNIQUE-Constraint `(planId, exerciseId, dayOfWeek)` per Migration v3 + Duplikat-Filter in `showAddExerciseDialog()`
4. **Tests** — `MigrationTestHelper`-Test für `MIGRATION_1_2`, Tests für `TrainingPlanDao` + neue DAO-Queries; instrumentierte Prüfung des BlurView-Fallbacks auf API < 31
5. **KI-05** — `muscleGroup`-Feld in `Exercise` + Filter-UI
6. **Plan-Dialoge** — `showCreatePlanDialog`/`showAddExerciseDialog` noch auf `androidx.AlertDialog`; optional auf `MaterialAlertDialogBuilder` + `ThemeOverlay.GymLog.Dialog` vereinheitlichen

---

## Design-System („Liquid Glass", umgesetzt 2026-06-23)

Komplettes visuelles Redesign — **nur Dark Mode**. Spec: `docs/superpowers/specs/2026-06-10-liquid-glass-redesign-design.md`, Mockups: `docs/mockups/liquid-glass-screens.html`.

- **Tokens** (`res/values/colors.xml`): `bg_base #0F0D0C`, `accent #D97757` (Terracotta), `glass_fill/border/sheen` (weiß @ 4/7/10 %), `text_primary/secondary/tertiary`. Alte Violet/Teal-Namen sind als **Aliase** auf die neuen Tokens umgebogen (Alt-Layouts brechen nicht).
- **Typografie**: `TextAppearance.GymLog.Headline` (Fraunces), `.Label/.Body/.Value` (Inter) in `themes.xml`. Inter ist App-Default via Theme-`fontFamily`.
- **Glass-Drawables** (`res/drawable/`): `bg_glass_card(_accent)`, `bg_glow` (radial), `bg_glass_nav`, `bg_chip(_active)`, `bg_streak_badge`, `bg_swipe_delete`.
- **Floatende Bottom-Nav**: `activity_main.xml` nutzt `eightbitlab.com.blurview.BlurTarget` (umschließt NavHost) + `BlurView`; Setup in `MainActivity.setupNavBlur()` (Radius 20, Outline-Clip 22dp). Fragmente haben `paddingBottom≈96dp` damit Inhalt unter der Nav durchscrollt.
- **Fun-Layer**: Streak-Badge im Home-Hero (`HomeViewModel.streak` ← `StreakCalculator`); „Neuer PR!"-Glass-Snackbar mit Scale/Fade (`LogViewModel.newPrEvent` ← `WorkoutSetRepository.insertWithPrCheck` + `PrDetector`), keine Animations-Library.
- **Wichtig**: Schema bleibt **v2** — neue DAO-Queries sind rein additiv/lesend bzw. synchron (`getDistinctWorkoutDates`, `countSetsForExercise`, `getMaxWeightSync`), **keine Migration**.

## Konventionen

- Alle neuen Klassen in **Java** (kein Kotlin einmischen solange nicht explizit gewünscht)
- DB-Änderungen erfordern eine **Room-Migration** (`addMigrations(...)` in `GymLogDatabase`)
- Async-Operationen über `ExecutorService` im Repository — kein DB-Zugriff auf Main-Thread
- ViewBinding aktiviert — kein `findViewById`
- Keine hardcodierten Strings — `res/values/strings.xml` verwenden