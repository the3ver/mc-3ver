#!/usr/bin/env bash
# Build + Tests mit knapper Ausgabe fuer Agenten. Gruen: eine Zeile. Rot: nur Compilerfehler und fehlgeschlagene Tests.
# Formatierung wird vorher automatisch angewendet statt als Fehler gemeldet (kein LLM-Durchlauf fuer Leerzeichen).
# Aufruf: scripts/check.sh          (Exit 0 gruen, 1 rot)
#         scripts/check.sh --hook   (Claude-Stop-Hook: liest JSON von stdin, ueberspringt unveraenderte Staende, Exit 2 bei rot)
set -uo pipefail
cd "$(dirname "$0")/.."

hook=false
[ "${1:-}" = "--hook" ] && hook=true
stamp_dir=.gradle/agent-check
mkdir -p "$stamp_dir"

# Fingerabdruck aller Eingaben des Builds: HEAD + uncommittete Aenderungen + neue Dateien
fingerprint() {
  local paths=(src build.gradle gradle.properties settings.gradle)
  {
    git rev-parse HEAD
    git diff HEAD -- "${paths[@]}"
    git ls-files -o --exclude-standard -- "${paths[@]}" | while read -r f; do git hash-object "$f"; done
  } 2>/dev/null | git hash-object --stdin
}

if $hook; then
  input=$(cat)
  fp=$(fingerprint)
  [ "$fp" = "$(cat "$stamp_dir/green" 2>/dev/null)" ] && exit 0
  # Schon einmal wegen genau dieses roten Stands blockiert: nicht endlos festhalten
  if [ "$fp" = "$(cat "$stamp_dir/red" 2>/dev/null)" ] && grep -q '"stop_hook_active": *true' <<<"$input"; then
    exit 0
  fi
fi

log="$stamp_dir/last.log"
start=$SECONDS
./gradlew spotlessApply check clientClasses --console=plain -q >"$log" 2>&1
status=$?
secs=$((SECONDS - start))

results=build/test-results/test
py=$(command -v python || command -v python3)
summary=$("$py" - "$results" <<'EOF'
import sys, glob, xml.etree.ElementTree as ET
tests = failed = 0
lines = []
for f in glob.glob(sys.argv[1] + "/*.xml"):
    root = ET.parse(f).getroot()
    tests += int(root.get("tests", 0))
    for case in root.iter("testcase"):
        for bad in list(case.findall("failure")) + list(case.findall("error")):
            failed += 1
            msg = (bad.get("message") or "").strip().splitlines()
            lines.append(f"FAIL {case.get('classname')}.{case.get('name')}: {msg[0][:200] if msg else bad.get('type')}")
            trace = [l.strip() for l in (bad.text or "").splitlines() if "net.frank" in l][:3]
            lines += ["    " + t for t in trace]
print(f"{tests} {failed}")
print("\n".join(lines))
EOF
)
tests=$(head -1 <<<"$summary" | cut -d' ' -f1)
failed=$(head -1 <<<"$summary" | cut -d' ' -f2)

if [ $status -eq 0 ]; then
  echo "CHECK OK: ${tests:-?} Tests, ${secs}s"
  $hook && fingerprint >"$stamp_dir/green"
  exit 0
fi

{
  echo "CHECK ROT nach ${secs}s (volles Log: $log)"
  # Gradle wiederholt Compilerfehler eingerueckt in der Zusammenfassung, daher nur Zeilen ab Spalte 0.
  # Warnungen zaehlen mit, weil -Werror sie (Error Prone) zu Fehlern macht.
  compile_rx='^[^[:space:]].*\.java:[0-9]+: (error|Fehler|warning|Warnung)'
  if grep -qE "$compile_rx" "$log"; then
    # Testergebnisse stammen dann noch vom letzten Lauf und waeren irrefuehrend
    grep -E -A5 "$compile_rx" "$log" | grep -vE '^--$' | head -40
  elif [ "${failed:-0}" != "0" ]; then
    echo "$failed von $tests Tests fehlgeschlagen:"
    tail -n +2 <<<"$summary" | head -40
  else
    grep -vE '^(WARNING|Hinweis):' "$log" | tail -25
  fi
} >&2

if $hook; then
  fingerprint >"$stamp_dir/red"
  exit 2
fi
exit 1
