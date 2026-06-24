# Anforderungen (final)

**Projekt:** GymLog — Krafttraining-Tracker · **Stand:** 2026-06-24

## 1. Projektidee

Eine native Android-App, mit der Nutzer ihr Krafttraining dokumentieren und ihren
Fortschritt nachvollziehen können. Der Kern ist das schnelle Erfassen von Sätzen
(Gewicht × Wiederholungen) pro Übung und die Auswertung über die Zeit.

## 2. Funktionale Anforderungen

| ID | Anforderung | Status |
|----|-------------|--------|
| F-01 | Nutzer kann Übungen anlegen | ✅ |
| F-02 | Übungsnamen sind eindeutig (keine Duplikate) | ✅ (UNIQUE-Index, Schema v2) |
| F-03 | Nutzer kann Übungen löschen; abhängige Sätze werden kaskadierend entfernt | ✅ (FK CASCADE) |
| F-04 | Vor dem Löschen einer Übung erscheint eine Bestätigung mit Anzahl betroffener Sätze | ✅ |
| F-05 | Nutzer kann einen Satz (Übung, Gewicht, Wdh., Datum) eintragen | ✅ |
| F-06 | Gewicht akzeptiert Komma **und** Punkt als Dezimaltrennzeichen | ✅ |
| F-07 | Eingaben werden validiert (Gewicht > 0, Wdh. > 0, ganzzahlig) | ✅ |
| F-08 | Satz per Swipe löschen mit Undo-Möglichkeit | ✅ |
| F-09 | Startseite zeigt Wochentag, Datum, heutigen Satz-Count, letzte 10 Sätze | ✅ |
| F-10 | Statistik je Übung: Bestleistung + Verlaufs-Diagramm (max. Gewicht/Tag) | ✅ |
| F-11 | Trainingspläne anlegen/löschen | ✅ |
| F-12 | Übungen einem Wochentag im Plan zuordnen | ✅ |
| F-13 | Trainings-Streak (aufeinanderfolgende Trainingstage) anzeigen | ✅ |
| F-14 | Rückmeldung bei neuem persönlichen Rekord (PR) | ✅ |

## 3. Nicht-funktionale Anforderungen

| ID | Anforderung | Status |
|----|-------------|--------|
| NF-01 | Offline-fähig, lokale Persistenz (kein Backend) | ✅ Room/SQLite |
| NF-02 | Keine DB-Zugriffe auf dem Main-Thread | ✅ ExecutorService im Repository |
| NF-03 | Reaktive UI (Änderungen sofort sichtbar) | ✅ LiveData |
| NF-04 | Lauffähig ab Android 7.0 (API 24) | ✅ minSdk 24 |
| NF-05 | Einheitliches, modernes UI (Dark Mode) | ✅ „Liquid Glass"-Designsystem |
| NF-06 | Texte zentral in Ressourcen (Lokalisierbarkeit) | ✅ `strings.xml` (de) |
| NF-07 | Schema-Migrationen statt Datenverlust bei DB-Änderungen | ✅ `MIGRATION_1_2` |

## 4. Abgrenzung (Out of Scope)

- Kein Cloud-Sync / kein Mehrgeräte-Betrieb, kein Login.
- Kein Light-Mode (bewusst Dark-only).
- Kein `muscleGroup`/Muskelgruppen-Filter (Backlog, KI-05).
- Startseite verknüpft (noch) nicht die heute laut Plan vorgesehenen Übungen.

## 5. Zielplattform

Android, Java, minSdk 24, targetSdk 36, Gradle-Build mit `annotationProcessor`.
