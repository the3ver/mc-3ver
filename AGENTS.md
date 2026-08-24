# Agent Guidelines

## Agent Verhalten
- Keine Entschuldigungen für Fehler. Halte Antworten kurz, fokussiert und lösungsorientiert.
- **Test-First / TDD (Inkrementell, 1 Test nach dem anderen):** Neue Features und Verhaltensänderungen müssen immer streng inkrementell testgetrieben implementiert werden: Schreibe und fixe immer genau EINEN Test (Rot -> Grün -> Refactor), bevor der nächste Test angelegt wird. Ein Test muss komplett fertig und grün sein, bevor der nächste angegangen wird. Falls unterwegs auffällt, dass weitere Tests sinnvoll sind, werden diese nach und nach auf dieselbe Weise ergänzt.
- Vor einem Git Push müssen immer alle Tests erfolgreich durchlaufen (grün sein).
