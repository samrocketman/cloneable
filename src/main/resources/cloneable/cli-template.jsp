#!/bin/sh
JAR_PATH='${jarPath}'
if ! type java > /dev/null 2>&1; then
  echo 'java is required to run cloneable but missing.' >&2
  exit 1
fi
java -jar "\${JAR_PATH}" "\$@"
