#!/usr/bin/env bash

debug=""
if [ "$2" == "--debug" ]; then
  debug="-Dmaven.surefire.debug"
fi

./devops/compile.sh test -Dtest="$1" $debug
