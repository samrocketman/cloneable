#!/bin/bash

set -eo pipefail
./gradlew clean jar
pushd build/libs
for x in sha256sum sha512sum; do
  "$x" cloneable.jar > cloneable.jar."$x"
  "$x" -c cloneable.jar."$x"
done
popd

echo 'Files ready for upload:'
ls -1 build/libs/*
