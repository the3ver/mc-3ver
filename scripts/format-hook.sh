#!/usr/bin/env bash
# Claude-PostToolUse-Hook: formatiert eine gerade bearbeitete Java-Datei mit Spotless.
# Liest das Hook-JSON von stdin. Nicht formatierbarer Code (Syntaxfehler) meldet Exit 2 an Claude.
set -uo pipefail
cd "$(dirname "$0")/.."

file=$(python -c "import json,sys; print(json.load(sys.stdin).get('tool_input',{}).get('file_path',''))" 2>/dev/null)
case "$file" in
  *.java) ;;
  *) exit 0 ;;
esac
[ -f "$file" ] || exit 0

# spotlessIdeHook formatiert nur diese eine Datei
out=$(./gradlew spotlessApply -PspotlessIdeHook="$(cygpath -w "$file" 2>/dev/null || echo "$file")" -q --console=plain 2>&1)
if [ $? -ne 0 ]; then
  echo "Spotless konnte $file nicht formatieren:" >&2
  grep -vE '^(WARNING|Hinweis):|^\s+at ' <<<"$out" | head -4 >&2
  exit 2
fi
exit 0
