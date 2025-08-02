#!/usr/bin/env bash

baseProfile="http"
additionalProfile="redis"

case "$1" in
  --queue)
    additionalProfile="queue"
    ;;
  --scheduler)
    additionalProfile="scheduler"
    ;;
  --redis)
    additionalProfile="redis"
    ;;
  *)
    additionalProfile=""
    ;;
esac

if [ -n "$additionalProfile" ]; then
  profiles="$baseProfile,$additionalProfile"
else
  profiles="$baseProfile"
fi

echo "Using Spring Boot profiles: $profiles"

mvn spring-boot:run -Dspring-boot.run.profiles=$profiles
