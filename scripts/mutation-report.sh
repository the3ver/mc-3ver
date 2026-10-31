#!/usr/bin/env bash
# Knappe Zusammenfassung des PIT-Laufs fuer Agenten: Score gesamt, je Klasse, ueberlebende Mutanten mit Zeile.
# Aufruf: scripts/mutation-report.sh [--run] [max-zeilen]
#   --run startet vorher ./gradlew pitest (ca. 1 min). Ohne --run wird der letzte Bericht ausgewertet.
set -uo pipefail
cd "$(dirname "$0")/.."

if [ "${1:-}" = "--run" ]; then
  shift
  ./gradlew pitest --console=plain -q >.gradle/agent-check/pitest.log 2>&1 || {
    echo "pitest fehlgeschlagen, Log: .gradle/agent-check/pitest.log" >&2
    grep -E 'SEVERE|did not pass' .gradle/agent-check/pitest.log | head -10 >&2
    exit 1
  }
fi

report=build/reports/pitest/mutations.xml
[ -f "$report" ] || { echo "Kein Bericht unter $report, erst mit --run starten" >&2; exit 1; }

py=$(command -v python || command -v python3)
PYTHONIOENCODING=utf-8 "$py" - "$report" "${1:-25}" <<'EOF'
import sys, collections, xml.etree.ElementTree as ET
root = ET.parse(sys.argv[1]).getroot()
limit = int(sys.argv[2])
per_class = collections.defaultdict(lambda: [0, 0])
survivors = []
total = killed = 0
for m in root.iter("mutation"):
    status = m.get("status")
    if status == "NON_VIABLE":
        continue
    cls = m.findtext("mutatedClass").split("$")[0].rsplit(".", 1)[-1]
    total += 1
    per_class[cls][1] += 1
    if status in ("KILLED", "TIMED_OUT", "MEMORY_ERROR"):
        killed += 1
        per_class[cls][0] += 1
    else:
        survivors.append((cls, int(m.findtext("lineNumber")), status, m.findtext("description")))
print(f"MUTATION SCORE: {killed}/{total} ({100 * killed // max(total, 1)} %)")
for cls, (k, t) in sorted(per_class.items(), key=lambda e: e[1][0] / e[1][1]):
    print(f"  {100 * k // t:3d} %  {cls} ({t - k} überlebt)")
if survivors:
    print(f"Überlebende Mutanten (erste {limit}, sortiert nach Klasse und Zeile):")
    for cls, line, status, desc in sorted(survivors)[:limit]:
        print(f"  {cls}.java:{line} {status}: {desc}")
EOF
