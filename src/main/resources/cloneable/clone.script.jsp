#!/bin/bash
case "$1" in
  Username*)
    echo x-access-token
    ;;
  Password*)
    java -jar ${jarPath} -k ${keyPath} -g ${appId} --print-auth-token
    ;;
esac
