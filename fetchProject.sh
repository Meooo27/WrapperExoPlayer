#!/usr/bin/env bash

set -euo pipefail

########################################
# CONFIG
########################################
MEDIA3_REPO="https://github.com/androidx/media.git"
BRANCH="release"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MEDIA3_DIR="$ROOT/media"
TMP_DIR="$ROOT/.tmp_media"

VERSION_FILE="$MEDIA3_DIR/.version"

########################################
# LOG
########################################
log()  { echo "[INFO] $*"; }
err()  { echo "[ERR ] $*" >&2; }

trap 'rm -rf "$TMP_DIR"' EXIT

########################################
# GET REMOTE VERSION
########################################
log "Checking Media3 version..."

REMOTE=$(git ls-remote "$MEDIA3_REPO" "$BRANCH" | awk '{print $1}')
LOCAL=$(cat "$VERSION_FILE" 2>/dev/null || echo "")

log "Remote: $REMOTE"
log "Local : ${LOCAL:-<none>}"

if [ "$REMOTE" == "$LOCAL" ]; then
    log "✔ Up-to-date. Exit."
    exit 0
fi

log "🚀 New version detected!"

########################################
# CLONE
########################################
rm -rf "$TMP_DIR"
git clone --depth=1 -b "$BRANCH" "$MEDIA3_REPO" "$TMP_DIR"

########################################
# REPLACE SOURCE
########################################
rm -rf "$MEDIA3_DIR"
mkdir -p "$MEDIA3_DIR"

rsync -a --exclude='.git' "$TMP_DIR/" "$MEDIA3_DIR/"

########################################
# SAVE VERSION
########################################
echo "$REMOTE" > "$VERSION_FILE"

log "Version saved to media/.version"

########################################
# BUILD
########################################
log "Building..."

chmod +x "$ROOT/gradlew"

cd "$ROOT"
./gradlew :app:fatJar

log "🎉 Done!"