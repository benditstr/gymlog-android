# Design- & Architektur-Zusammenfassung

**Projekt:** GymLog · **Stand:** 2026-06-24

## 1. Architekturüberblick

Die App folgt **MVVM + Repository-Pattern** ohne externe DI-Bibliothek. Der
Datenfluss ist strikt einseitig und rein LiveData-basiert (kein Coroutines/RxJava).

```
UI (Fragment + ViewBinding)
   └── beobachtet LiveData ───┐
ViewModel (hält keine Android-/View-Referenzen)
   └── ruft ───────────────┐
Repository (kapselt DB-Zugriff, async via ExecutorService)
   └── nutzt ──────────────┐
Room DAO  ──►  SQLite (Room, Schema v2)
```

- **Fragments** beobachten `LiveData` aus dem ViewModel und rendern nur.
- **ViewModels** (`AndroidViewModel`) halten Zustand als `LiveData`/`MutableLiveData`,
  ableitende Werte über `Transformations.map/switchMap`.
- **Repositories** kapseln die DAOs. Lesen liefert `LiveData` (Room threaded
  automatisch); Schreiben läuft über `Executors.newSingleThreadExecutor()` —
  **nie auf dem Main-Thread**.
- **DAO/Room** mit parametrisierten Queries (kein SQL-Injection-Risiko).

## 2. Schichten & Pakete

```
de.th.nuernberg.bme.gymlog
├── MainActivity              Bottom-Nav + NavController + Blur-Setup
├── database/                 GymLogDatabase (Singleton, Migration), DAOs, Entities
├── model/                    Projektionen (MaxWeightPerDay, *WithExercise, *WithName)
├── repository/               Exercise / WorkoutSet / TrainingPlan Repositories
├── ui/{home,log,exercises,statistics,plan}   je Fragment + ViewModel + Adapter
└── util/                     DateUtils, SetValidator, StreakCalculator, PrDetector
```

## 3. Datenmodell (Room, Schema v2)

| Entity | Felder (Kurz) | Beziehungen |
|--------|---------------|-------------|
| `Exercise` | id, **name** (UNIQUE) | 1—n WorkoutSet |
| `WorkoutSet` | id, exerciseId (FK CASCADE), weight, reps, date (Unix-ms) | gehört zu Exercise |
| `TrainingPlan` | id, name | 1—n PlanExercise |
| `PlanExercise` | id, planId (FK CASCADE), exerciseId (FK CASCADE), dayOfWeek | verknüpft Plan ↔ Übung ↔ Wochentag |

`MIGRATION_1_2` legt den UNIQUE-Index auf `exercises.name` an und erstellt
`training_plans` + `plan_exercises`. Daten gehen bei DB-Änderungen nicht verloren.

## 4. Navigation

Single-Activity mit **Navigation Component**, 4 Bottom-Nav-Ziele (Start, Eintragen,
Übungen, Plan). Die Statistik-Detailansicht wird per Action vom Übungen-Tab aus
geöffnet (`action_exercises_to_statistics`, Argument `exerciseId`).

## 5. UI-Designsystem („Liquid Glass")

- **Dark-only**, Terracotta-Akzent `#D97757`, Hintergrund `#0F0D0C`.
- **Glass-Cards** = halbtransparente Füllung + 1 dp Rand + Sheen-Gradient; weiche
  radiale Glows statt harter Schatten.
- **Typografie:** Fraunces (Headlines) + Inter (Fließtext) — siehe Limitierung L-01.
- **Floatende Bottom-Navigation** mit echtem Backdrop-Blur über `BlurView`
  (RenderEffect ab API 31, RenderScript-Fallback darunter).
- Tokens zentral in `res/values/colors.xml` + `themes.xml` (TextAppearances,
  Button-/Dialog-Styles). Alte Farb-Namen sind als Aliase auf die neuen Tokens
  umgebogen, sodass keine Alt-Referenz bricht.

## 6. Fun-/Gamification-Layer

- **Streak:** `StreakCalculator.calculateStreak()` (reine Funktion) über die
  distinkten Trainingstage (`WorkoutSetDao.getDistinctWorkoutDates()`); im
  `HomeViewModel` via `Transformations.map` an die UI gebunden.
- **PR-Erkennung:** `PrDetector.isNewPr()` (reine Funktion); `WorkoutSetRepository.
  insertWithPrCheck()` liest den bisherigen Max-Wert vor dem Insert und meldet
  einen neuen Rekord per Einmal-Event an den `LogViewModel`. Feedback ohne
  zusätzliche Animations-Bibliothek (ViewPropertyAnimator).

## 7. Bewusste Entscheidungen

- **Reine Funktionen** für Streak/PR → einfach unit-testbar, keine Android-Deps.
- **Keine Migration fürs Redesign** — neue DAO-Queries sind additiv/lesend bzw.
  synchron; Schema bleibt v2.
- **Kein Coroutines/DI** — bewusst schlank gehalten (Lehrprojekt-Scope), Async
  ausschließlich über `ExecutorService`.
