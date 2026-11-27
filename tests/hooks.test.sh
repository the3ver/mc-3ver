#!/usr/bin/env bash
# Tests fuer die Claude-Hooks in .claude/settings.json (scripts/format-hook.sh, scripts/check.sh --hook).
# Aufruf: bash tests/hooks.test.sh
set -uo pipefail
cd "$(dirname "$0")/.."
root=$PWD

fails=0
pass() { echo "ok   $1"; }
fail() { echo "FAIL $1"; fails=$((fails + 1)); }

# Liegt in src/test, damit spotless sie erfasst; wird am Ende entfernt
probe=src/test/java/net/frank/HookProbe.java
trap 'rm -f "$root/$probe"' EXIT
hook_json() { printf '{"session_id":"t","hook_event_name":"PostToolUse","tool_name":"Edit","tool_input":{"file_path":"%s"},"tool_response":{"filePath":"x.txt"}}' "$1"; }

# 1. Bearbeitete Java-Datei wird formatiert, auch aus einem anderen Arbeitsverzeichnis
mkdir -p "$(dirname "$probe")"
printf 'package net.frank;\nclass HookProbe{int   x=1;}\n' >"$probe"
(cd / && hook_json "$root/$probe" | bash "$root/scripts/format-hook.sh")
code=$?
if [ $code -eq 0 ] && grep -q '^    int x = 1;$' "$probe"; then pass "format-hook formatiert Java-Datei"; else
  fail "format-hook formatiert Java-Datei (exit $code)"; cat "$probe"; fi

# 2. Nicht formatierbare Java-Datei meldet Exit 2 an Claude
printf 'package net.frank;\nclass HookProbe{ int x = ; \n' >"$probe"
hook_json "$root/$probe" | bash scripts/format-hook.sh 2>/dev/null
code=$?
[ $code -eq 2 ] && pass "format-hook meldet Syntaxfehler mit Exit 2" || fail "format-hook meldet Syntaxfehler mit Exit 2 (exit $code)"
rm -f "$probe"

# 3. Andere Dateien und fehlende Dateien ignoriert der Hook
hook_json "$root/README.md" | bash scripts/format-hook.sh
[ $? -eq 0 ] && pass "format-hook ignoriert Nicht-Java-Dateien" || fail "format-hook ignoriert Nicht-Java-Dateien"
hook_json "$root/gibt/es/nicht.java" | bash scripts/format-hook.sh
[ $? -eq 0 ] && pass "format-hook ignoriert fehlende Dateien" || fail "format-hook ignoriert fehlende Dateien"

# 5. settings.json verdrahtet beide Hooks ueber $CLAUDE_PROJECT_DIR
settings=.claude/settings.json
grep -q '"command": "bash \\"\$CLAUDE_PROJECT_DIR\\"/scripts/format-hook.sh"' "$settings" &&
  grep -q '"matcher": "Edit|Write"' "$settings" &&
  pass "settings.json: PostToolUse -> format-hook.sh" || fail "settings.json: PostToolUse -> format-hook.sh"
grep -q '"command": "bash \\"\$CLAUDE_PROJECT_DIR\\"/scripts/check.sh --hook"' "$settings" &&
  pass "settings.json: Stop -> check.sh --hook" || fail "settings.json: Stop -> check.sh --hook"
grep -qx '@AGENTS.md' CLAUDE.md && pass "CLAUDE.md importiert AGENTS.md" || fail "CLAUDE.md importiert AGENTS.md"

# 6. check.sh ohne uvx: meldet, dass Semgrep uebersprungen wurde
# PATH ohne Verzeichnisse, die uvx enthalten
nouvx=$(tr ':' '\n' <<<"$PATH" | while read -r d; do [ -x "$d/uvx" ] || printf '%s:' "$d"; done)
out=$(PATH="${nouvx%:}" bash scripts/check.sh 2>&1)
code=$?
if [ $code -eq 0 ] && grep -q 'Semgrep lokal übersprungen' <<<"$out"; then pass "check.sh meldet fehlendes uvx"; else
  fail "check.sh meldet fehlendes uvx (exit $code)"; echo "$out" | tail -5; fi

[ $fails -eq 0 ] && echo "ALLE HOOK-TESTS GRUEN" || echo "$fails Hook-Tests rot"
[ $fails -eq 0 ]
