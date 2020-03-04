#!/usr/bin/env bash

find . -type d -name 'node_modules' -prune -exec rm -rf {} \;
find . -type d -name 'node' -prune -exec rm -rf {} \;
find . -type d -name 'dist' -prune -exec rm -rf {} \;
find . -type f -name 'package-lock.json' -prune -exec rm -rf {} \;
find . -type f -name 'yarn.lock' -prune -exec rm -rf {} \;
find . -type f -name '*.log' -prune -exec rm -rf {} \;

