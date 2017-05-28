
#! /bin/bash
./gradlew androidcommon:lint
echo "Lint check finished"
cat /home/travis/build/fresheed/ActionLogger/androidcommon/build/outputs/lint-results-debug.html
