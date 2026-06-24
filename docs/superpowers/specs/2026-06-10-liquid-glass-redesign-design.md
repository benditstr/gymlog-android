# GymLog – "Liquid Glass" Redesign

**Datum:** 2026-06-10
**Status:** ✅ Implementiert 2026-06-23 (alle 5 Phasen). Build grün (`assembleDebug`), 27 JVM-Tests grün.
**Mockups:** `docs/mockups/liquid-glass-screens.html` (7 Screens) — vom Nutzer freigegeben
**Bestätigte Entscheidungen:** Kein Light Mode. Terracotta #D97757 + Fraunces/Inter. Fun-Layer (Streak + PR). BlurView-Library für die Bottom-Nav.

**Umsetzungsnotizen:**
- Fonts als Variable TTFs direkt in `res/font/` (Fraunces, Inter) — Betonung über `textStyle`/`textFontWeight` statt fontVariationSettings-XML.
- Alte Farb-Namen (violet/teal) als Aliase auf neue Tokens umgebogen → bestehende Referenzen brechen nicht.
- BlurView **3.2.0** (nicht 3.1) — neue API mit `BlurTarget`-Wrapper (`MainActivity.setupNavBlur`).
- UF-03 über `windowSoftInputMode="adjustResize"` gelöst (kein ScrollView um den RecyclerView — Anti-Pattern vermieden).
- KI-08 mitbehoben (`SetValidatorTest`).
- Plan-Dialoge noch auf `androidx.AlertDialog` (Backlog).

## Ziel

Komplette visuelle Überarbeitung von GymLog auf einen warmen, minimalistischen "Liquid Glass"-Look (Claude-Terracotta-Akzent, Serif-Display-Typografie), ergänzt um einen leichten Fun-/Gamification-Layer. Betrifft alle 4 Tabs, Statistik-Detail, alle Listen-Items, Dialoge und Empty-States. Nur Dark Mode (kein Light-Theme).

## Out of Scope

- Light Mode / helle Theme-Variante
- App-Icon / Launcher-Icon-Redesign
- Neue Datenbank-Migration (Schema bleibt v2; neue Queries sind additiv, lesend)
- Architekturänderungen (MVVM + Repository + Room bleibt wie bisher)

---

## 1. Design-System

### 1.1 Farben (`res/values/colors.xml`, ersetzt aktuelle Palette)

| Token | Wert | Verwendung |
|---|---|---|
| `bg_base` | `#0F0D0C` | App-Hintergrund (alle Screens) |
| `glow_accent` | `#D97757` @ 12% | Radiale Glow-Akzente in 1–2 Ecken pro Screen (Drawable mit weichem Gradient-Verlauf, kein Echtzeit-Blur nötig) |
| `glass_fill` | `#FFFFFF` @ 4% | Füllung von Glass-Cards/Items |
| `glass_border` | `#FFFFFF` @ 7% | 1dp Border auf Glass-Cards |
| `glass_sheen` | `#FFFFFF` @ 10% → transparent (Gradient) | Dezenter "Glanz"-Streifen am oberen Card-Rand |
| `glass_fill_dialog` | `#FFFFFF` @ 8% über `bg_base` @ 70% Scrim | Dialog-Hintergrund (mehr Kontrast als normale Cards) |
| `accent` | `#D97757` | Werte/Zahlen, aktiver Tab, Primär-Button, Streak/PR-Icons |
| `accent_soft` | `#D97757` @ 16% | Hintergrund von Pills/Badges |
| `text_primary` | `#F3EDE9` | Haupttext, Headlines |
| `text_secondary` | `#8A817C` | Labels, Zeitstempel, Sub-Infos, Chart-Achsen |
| `text_tertiary` | `#5C5550` | Platzhaltertexte, deaktivierte Elemente |
| `error_color` | `#CF6679` (bestehend) | Lösch-Aktionen, Validierungsfehler, Swipe-to-Delete-Hintergrund |

Statistik-Charts (MPAndroidChart): Linie/Punkte in `accent`, Füllverlauf `accent` → transparent, Gitterlinien `glass_border`, Achsentext `text_secondary`. Bei mehreren Datenreihen: Graustufen-Varianten von `text_secondary`/`text_primary` statt neuer Buntfarben.

### 1.2 Typografie

- **Fraunces** (Variable Font, Gewicht 600) – Screen-Titel/Headlines: "Montag", Statistik-Hauptwert, Dialog-Titel
- **Inter** (400/500/600) – alles andere: Labels, Listen-Items, Buttons, Navigation, Eingabefelder
- Beide als `res/font/` Ressourcen (SIL Open Font License), als `fontFamily` referenziert
- Neue `TextAppearance`-Styles in `themes.xml`:
  - `TextAppearance.GymLog.Headline` (Fraunces 600, 28sp)
  - `TextAppearance.GymLog.Label` (Inter 500, 11sp, uppercase, letterSpacing 0.06)
  - `TextAppearance.GymLog.Body` (Inter 400/600, 13sp)
  - `TextAppearance.GymLog.Value` (Inter 700, 13sp, tabular figures, `accent`)

### 1.3 Form-Sprache

- Card-/Item-Radius: 10–12dp (kantig statt "blobby")
- Item-Innenabstand: ~8dp (kompakt)
- Glass-Card-Drawable: `glass_fill` + 1dp `glass_border` + `glass_sheen`-Overlay oben, abgerundete Ecken 10–12dp
- Schatten werden durch Glow-Drawables (weiche radiale Gradients) ersetzt, keine harten Elevation-Schatten

---

## 2. Glass-/Blur-Implementierung

Zwei Techniken, je nach Bedarf:

1. **Simuliertes Glas** (Standard – Hero-Card, Listen-Items, Dialog-Hintergründe, Empty-States): nur `glass_fill` + `glass_border` + `glass_sheen` + statischer Hintergrund-Glow. Kein Echtzeit-Blur, läuft auf jedem Gerät ab API 24, kein Performance-Risiko. Visuell von echtem Blur kaum unterscheidbar, da darunter nur statischer Hintergrund liegt.

2. **Echter Backdrop-Blur** – nur für die **Bottom-Navigation**, die als floatende Glass-Bar (16dp Margin zu allen Seiten, ~24dp Corner-Radius) über dem scrollenden Listeninhalt schwebt. Implementierung über die Library **`com.github.Dimezis:BlurView`** (JitPack):
   - Deckt automatisch `RenderEffect`-Blur (API 31+) und RenderScript-Fallback (API 24–30) ab – genau der gewünschte Hybrid, ohne Eigenbau
   - Blur-Radius ~20dp, Overlay-Farbe `bg_base` @ 60% für Icon-Kontrast
   - RecyclerViews/ScrollViews bekommen `clipToPadding="false"` + Bottom-Padding in Höhe der Nav-Bar, damit Inhalte sichtbar darunter scrollen

**Build-Änderung:** JitPack-Repository in `settings.gradle` ergänzen, Dependency `com.github.Dimezis:BlurView:<latest>` in `app/build.gradle`.

---

## 3. Komponenten-Bibliothek

Wird im Pilot-Screen (Home) gebaut, danach in allen weiteren Screens wiederverwendet:

- **`bg_glass_card.xml`** – Layer-List-Drawable (Fill + Border + Sheen), parametrisierbar über Radius
- **Floating Glass Bottom Navigation** – `BlurView` + neues Linien-Icon-Set (Home, Stift/Log, Hantel/Übungen, Kalender/Plan), aktiver Tab-Indikator in `accent`
- **Buttons**: Primär = gefüllt `accent`, Text `bg_base`; Sekundär = Glass-Outline, Text `text_primary`
- **Dialoge**: `MaterialAlertDialog` mit `glass_fill_dialog`-Theme, Titel in Fraunces, Bestätigen-Button = Primär-Button-Style
- **Eingabefelder**: `TextInputLayout` im Glass-Stil (transparente Box, `glass_border` als Outline, Fokus-Farbe `accent`)
- **Icon-Set** (alle als Vector-Drawables, stroke-width 1.6dp, `currentColor`):
  - Ersetzt: `ic_home`, `ic_log`, `ic_exercises`, `ic_plan`, `ic_stats`, `ic_remove`, `ic_chevron_right`
  - Neu: `ic_streak` (Flame), `ic_pr` (Trophy/Star) für den Fun-Layer
- **Empty States**: Icon (`text_tertiary`) + Text (`text_secondary`) in Glass-Card, konsistent für Home/Übungen/Statistik

---

## 4. Rollout pro Screen

**Pilot (Phase 2): Home** – komplette Umsetzung als Referenz für alle weiteren Screens.

| Screen | Wichtigste Änderungen |
|---|---|
| **Home** (Pilot) | Hero-Glass-Card: Fraunces-"Wochentag" + Datum, animierter Satz-Counter, Streak-Badge (Flame-Icon + Zahl); Liste letzter Sätze als Glass-Items; floatende Glass-Bottom-Nav |
| **Log** | Glass-Inputfelder, Exercise-Dropdown im neuen Stil, Primär-Button "Eintragen"; gleichzeitig **UF-03-Fix** (ScrollView + `windowSoftInputMode="adjustResize"`) im Rahmen des Layout-Rewrites |
| **Übungen** | Glass-Listen-Items je Übung, Empty-State im neuen Stil; Lösch-Bestätigungsdialog im Glass-Stil zeigt Anzahl betroffener Sätze (deckt **UF-01** ab) |
| **Statistik-Detail** | MPAndroidChart neu gestylt (siehe 1.1), Glass-Card-Container, Achsen-Labels in Inter, bestehender Empty-State (KI-03) im neuen Stil |
| **Trainingsplan** | Plan-Liste + Wochentag-Zuordnung als Glass-Cards, Wochentag-Chips in `accent_soft`/`accent` |
| **Item-Layouts** | `item_workout_set`, `item_exercise`, `item_plan_exercise`, `item_dropdown` einheitlich auf Glass-Item-Stil; Swipe-to-Delete-Hintergrund in `error_color`-Glass |

---

## 5. Fun-/Motion-Layer

### 5.1 Streak (Trainingstage in Folge)

- Neue `WorkoutSetDao`-Query: `getDistinctWorkoutDates()` – distinkte, auf Mitternacht normalisierte Datumswerte (absteigend), rein lesend, **keine Migration nötig**
- Neue Utility `util/StreakCalculator.java`: reine Funktion `calculateStreak(List<Long> datesDescending, long today)` → Anzahl aufeinanderfolgender Tage (zählt auch, wenn heute noch kein Eintrag existiert, aber gestern lückenlos war)
- `HomeViewModel` exponiert `LiveData<Integer> streak`, angezeigt als Flame-Icon + Zahl im Hero

### 5.2 "Neuer PR!"-Celebration

- Neue `WorkoutSetDao`-Query: bisheriger Max-`weight` für eine `exerciseId` (vor dem neuen Eintrag)
- Neue Utility `util/PrDetector.java`: reine Funktion `isNewPr(float newWeight, Float previousMax)` → `true` wenn `previousMax == null || newWeight > previousMax`
- `LogViewModel`/`WorkoutSetRepository`: nach erfolgreichem Insert PR-Check ausführen, bei Treffer einmaliges Event (`SingleLiveEvent<String>` mit Übungsname) auslösen
- UI: Glass-Snackbar mit Scale/Fade-Animation ("Neuer PR! 🏆 Bankdrücken") via `ObjectAnimator`/`ValueAnimator`, **keine neue Animations-Library** (kein Lottie)

### 5.3 Micro-Animationen

- Tap-Scale-Feedback auf Cards/Buttons (kurzes Scale-Down/Up via `ViewPropertyAnimator`)
- Animierter Counter für "Sätze heute" (Zahlen-Hochzählen via `ValueAnimator`)
- Sanfte Fragment-Übergänge (Fade/Slide über Navigation-Component `enterAnim`/`exitAnim`)
- RecyclerView-Item-Animationen beim Hinzufügen/Entfernen (erweiterter `DefaultItemAnimator` oder leichte Custom-Animation)

---

## 6. Tests

- Bestehende Tests (`SetValidatorTest`, `DateUtilsTest`, `GymLogDatabaseTest`) bleiben unverändert grün – Redesign betrifft UI-Layer + rein additive, lesende DAO-Queries
- Neue JVM-Unit-Tests:
  - `StreakCalculatorTest` – verschiedene Datumsfolgen (lückenlos, mit Lücke, leer, "heute fehlt aber gestern vorhanden")
  - `PrDetectorTest` – neuer Rekord, kein Rekord, erster Eintrag (kein bisheriger Max-Wert)
- Ziel: 80%+ Coverage für beide neuen Utility-Klassen
- Visuelle Verifikation: pro umgesetztem Screen manuell im Emulator prüfen (Glass-Effekt, Blur-Fallback auf API <31, Bottom-Nav-Scrollverhalten)

---

## 7. Implementierungsreihenfolge (Phasen)

1. **Foundation**: Farben, Fonts, TextAppearances, `bg_glass_card.xml`, BlurView-Dependency
2. **Pilot – Home**: komplette Umsetzung inkl. floatender Glass-Bottom-Nav, Streak-Anzeige
3. **Rollout**: Log (+ UF-03), Übungen (+ UF-01-Dialog), Trainingsplan, Statistik-Detail, alle Item-Layouts, übrige Dialoge
4. **Fun-Layer**: PR-Detection + Celebration, Micro-Animationen, RecyclerView-Item-Animationen
5. **Tests**: `StreakCalculatorTest`, `PrDetectorTest`

Jede Phase einzeln abschließbar/testbar; Phase 2 (Pilot) dient als visuelle Referenz für Phase 3.