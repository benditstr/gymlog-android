# Reflexion zur KI-Nutzung

**Projekt:** GymLog · **Stand:** 2026-06-24

## 1. Wofür KI eingesetzt wurde

Die Entwicklung wurde mit einem KI-Coding-Assistenten (Claude Code) begleitet.
Konkrete Einsatzfelder:

- **Code-Generierung:** Layouts, ViewModels, Repository-/DAO-Methoden, Utility-Klassen
  und Unit-Tests des „Liquid Glass"-Redesigns.
- **Design-Findung:** Iteratives Brainstorming der Optik und Erzeugung von
  HTML-Mockups (`docs/mockups/`) zur Bewertung vor der Implementierung.
- **Recherche:** Auswahl/Version der BlurView-Bibliothek, Beschaffung der Fonts,
  Klärung von Android-spezifischen Details.
- **Dokumentation:** Diese Abgabe-Dokumente, `CLAUDE.md`, Changelog.
- **Review:** Sicherheits- und Qualitäts-Review der Änderungen (parametrisierte
  Queries, Thread-Sicherheit, Crash-Pfade).

## 2. Was gut funktioniert hat

- **Spec-first-Vorgehen:** Erst Designentscheidung + Mockups bestätigen, dann in
  Phasen implementieren. Das hat Nacharbeit reduziert.
- **Reine Funktionen für Logik** (Streak, PR-Erkennung) — von der KI bewusst so
  geschnitten, dass sie ohne Android-Abhängigkeiten unit-testbar sind.
- **Inkrementelle Builds** nach jeder Phase fingen Fehler früh ab.

## 3. Wo menschliche Kontrolle nötig war

- **Visuelle Verifikation am Gerät:** Die KI konnte nur kompilieren, nicht sehen.
  Erst die manuellen Screenshots deckten auf, dass (a) die alten Nav-Icons noch
  aktiv waren und (b) die Fraunces-Schrift nicht lädt. Beides war im Build „grün",
  aber visuell falsch — ein gutes Beispiel dafür, dass „kompiliert" ≠ „korrekt".
- **Font-Problem (L-01):** Mehrere KI-Hypothesen (textAppearance, direkte
  fontFamily) lösten es nicht vollständig; die zuverlässige Lösung (statische
  Schnitte) wurde bewusst zurückgestellt. Hier war menschliche Priorisierung
  („akzeptabel, dokumentieren statt verstecken") ausschlaggebend.
- **Entscheidungen mit Tragweite:** Scope (kein Light Mode), Akzeptanz von
  Einschränkungen und der Umgang mit dem fremden Inhalt im Ziel-Repository wurden
  bewusst durch den Menschen freigegeben, nicht automatisch durch die KI.

## 4. Auswirkung auf Qualität

- **Positiv:** schnelleres, konsistenteres UI; testbare Logik; ehrliche,
  vollständige Dokumentation der Schwächen.
- **Risiko:** KI-Code kann „plausibel, aber visuell/funktional unvollständig" sein.
  Gegenmaßnahme: jeder sichtbare Flow wurde manuell am Gerät geprüft, Schwächen
  offen dokumentiert (siehe [KNOWN_LIMITATIONS.md](KNOWN_LIMITATIONS.md)).

## 5. Fazit

KI war als Beschleuniger für Implementierung, Recherche und Dokumentation sehr
wertvoll, ersetzt aber nicht die menschliche Verifikation am laufenden Gerät und
die Verantwortung für Scope- und Qualitätsentscheidungen. Der ehrliche Umgang mit
verbleibenden Schwächen (insb. L-01) war eine bewusste menschliche Entscheidung.
