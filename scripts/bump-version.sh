#!/usr/bin/env bash
# Setzt die Mod-Version. Einzige Quelle ist gradle.properties; Mc3verMod und fabric.mod.json lesen sie beim Build.
# Aufruf: scripts/bump-version.sh <neue-version> [--docs]
#   --docs zieht README.md und docs/ mit (erst beim Release, Pages deployt bei jedem Push auf main).
# Die CHANGELOG-Eintraege schreibt weiterhin ein Mensch oder Agent; das Skript legt die Ueberschriften an und zeigt einen git-cliff-Entwurf.
set -euo pipefail
cd "$(dirname "$0")/.."

new=${1:-}
[[ $new =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]] || { echo "Aufruf: $0 X.Y.Z [--docs]" >&2; exit 1; }
old=$(sed -n 's/^version=//p' gradle.properties)
[ "$old" != "$new" ] || { echo "Version ist bereits $new" >&2; exit 1; }
old_re=${old//./\\.}

sed -i "s/^version=.*/version=$new/" gradle.properties

today=$(date +%F)
for log in CHANGELOG.md CHANGELOG_EN.md; do
  grep -q "^## \[$new\]" "$log" && continue
  # Neue Ueberschrift vor den ersten Versionseintrag setzen
  sed -i "0,/^## \[/s//## [$new] - $today\n\n## [/" "$log"
done

if [ "${2:-}" = "--docs" ]; then
  sed -i "s/mc-3ver-$old_re\.jar/mc-3ver-$new.jar/g" README.md docs/index.html docs/script.js
  sed -i "s/v$old_re\b/v$new/g" docs/index.html docs/script.js
fi

echo "Version $old -> $new"
if command -v uvx >/dev/null; then
  echo
  echo "Rohentwurf aus den Commits seit v$old (cliff.toml), als Vorlage für die Changelog-Texte:"
  uvx git-cliff --unreleased 2>/dev/null
fi
rest=$(git grep -nF "$old" -- . ':!CHANGELOG*.md' ':!settings.gradle' || true)
[ -z "$rest" ] || { echo "Alte Version steht noch in:"; echo "$rest"; }
