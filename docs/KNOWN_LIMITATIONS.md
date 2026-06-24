# Bekannte Einschränkungen

**Projekt:** GymLog · **Stand:** 2026-06-24

Ehrlich dokumentiert statt versteckt. Keine dieser Punkte verhindert das
zuverlässige Installieren, Starten und Bedienen der Haupt-Workflows.

## UI / Design

| ID | Schwere | Beschreibung | Workaround / geplanter Fix |
|----|---------|--------------|----------------------------|
| L-01 | Mittel | **Fraunces-Headline lädt nicht.** Die Headline-Schrift (Fraunces) erscheint auf dem Testgerät als System-Schrift. Ursache: der mitgelieferte **Variable-Font** wird vom Gerät nicht instanziiert und fällt still zurück. Inter (Fließtext) wirkt korrekt. | Umstieg auf **statische Schnitt-TTFs** (z. B. Fraunces SemiBold) statt Variable-Font — behebt es zuverlässig. Funktional ohne Auswirkung. |
| L-02 | Niedrig | **Blur-Fallback ungetestet auf Alt-Hardware.** Die floatende Nav nutzt RenderEffect (API 31+) bzw. RenderScript-Fallback (API 24–30). Der Fallback wurde nicht auf echter Hardware < API 31 verifiziert. | Auf Gerät/Emulator mit API 24–30 testen. |

## Funktion / Scope

| ID | Schwere | Beschreibung |
|----|---------|--------------|
| L-03 | Info | **Start ↔ Plan nicht verknüpft.** Die Startseite zeigt (noch) nicht die heute laut Trainingsplan vorgesehenen Übungen — der Plan-Tab ist eigenständig. |
| L-04 | Niedrig | **Mehrfachzuordnung im Plan möglich (KI-06).** Dieselbe Übung kann einem Wochentag mehrfach zugeordnet werden — keine UNIQUE-Constraint `(planId, exerciseId, dayOfWeek)`. |
| L-05 | Info | **Keine Muskelgruppen (KI-05).** `Exercise` hat kein `muscleGroup`-Feld; kein Filtern nach Muskelgruppe. Bewusst aus dem MVP-Scope. |
| L-06 | Info | **Nur Deutsch / nur Dark Mode.** Texte liegen nur auf Deutsch vor; kein Light-Theme (bewusste Designentscheidung). |
| L-07 | Info | **Kein Sync/Backup.** Daten liegen rein lokal (Room). Bei Deinstallation gehen sie verloren. |

## Test / Qualität

| ID | Schwere | Beschreibung |
|----|---------|--------------|
| L-08 | Niedrig | Keine automatisierten Tests für `MIGRATION_1_2`, `TrainingPlanDao` und die neuen DAO-Queries; keine UI-/Espresso-Tests. Reine Logik (Validator, DateUtils, Streak, PR) ist unit-getestet. |
| L-09 | Info | Plan-Dialoge nutzen noch `androidx.AlertDialog` statt des Glass-`MaterialAlertDialog` — kleiner Stilbruch, rein kosmetisch. |
