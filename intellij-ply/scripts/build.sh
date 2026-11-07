#!/bin/sh
# Build the Ply IntelliJ plugin without Gradle: javac against the IDE's own jars, then zip.
#   scripts/build.sh            compile + package → dist/ply-intellij.zip
#   scripts/build.sh test       compile + run unit tests
#   RELEASE=1 scripts/build.sh  Marketplace build → dist/mycel-intellij-<version>.zip (no baked repo path)
#   scripts/build.sh install    build + copy into the IDE's plugins dir (restart the IDE to load it)
set -eu
HERE="$(cd "$(dirname "$0")/.." && pwd)"
IDE="${IDE_APP:-/Applications/IntelliJ IDEA CE.app}"
PLUGINS="${IDE_PLUGINS:-$HOME/Library/Application Support/JetBrains/IdeaIC2024.3/plugins}"
JAVAC="$IDE/Contents/jbr/Contents/Home/bin/javac"; JAVA="$IDE/Contents/jbr/Contents/Home/bin/java"
CP="$IDE/Contents/lib/*:$IDE/Contents/lib/modules/*"
OUT="$HERE/build"; rm -rf "$OUT"; mkdir -p "$OUT/classes" "$OUT/test" "$HERE/dist"
"$JAVAC" --release 21 -Xlint:-options -cp "$CP" -d "$OUT/classes" "$HERE"/src/ply/*.java
cp -R "$HERE/resources/." "$OUT/classes/"
# default location of lang/bin/ply for runs started outside the repo tree
# (RELEASE=1 omits it: a Marketplace build must not carry this machine's path)
REPO="$(cd "$HERE/.." && pwd)"; [ "${RELEASE:-}" = 1 ] || printf '%s\n' "$REPO/lang/bin/mycel" > "$OUT/classes/ply-default-path.txt"
case "${1:-}" in
 test) "$JAVAC" --release 21 -Xlint:-options -cp "$CP:$OUT/classes" -d "$OUT/test" "$HERE"/test/ply/*.java
       "$JAVA" -Djava.awt.headless=true -cp "$CP:$OUT/classes:$OUT/test" ply.PlyTests || exit 1
       [ "${2:-}" = live ] && "$JAVA" -Djava.awt.headless=true -cp "$CP:$OUT/classes:$OUT/test" ply.PlyLiveCheck
       exit $? ;;
esac
STAGE="$OUT/stage/Ply"; mkdir -p "$STAGE/lib"
( cd "$OUT/classes" && zip -qr "$STAGE/lib/ply.jar" . )
ZIP="$HERE/dist/ply-intellij.zip"
if [ "${RELEASE:-}" = 1 ]; then
  VER="$(sed -n 's:.*<version>\(.*\)</version>.*:\1:p' "$HERE/resources/META-INF/plugin.xml" | head -1)"
  mv "$OUT/stage/Ply" "$OUT/stage/Mycel"; ZIP="$HERE/dist/mycel-intellij-$VER.zip"; DIRN=Mycel
else DIRN=Ply; fi
( cd "$OUT/stage" && rm -f "$ZIP" && zip -qr "$ZIP" "$DIRN" )
echo "built $ZIP"
[ "${RELEASE:-}" = 1 ] && exit 0
if [ "${1:-}" = install ]; then
  rm -rf "$PLUGINS/Ply"; cp -R "$STAGE" "$PLUGINS/Ply"; echo "installed to $PLUGINS/Ply — restart IntelliJ to load it"
fi
