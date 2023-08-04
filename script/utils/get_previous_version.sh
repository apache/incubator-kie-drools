#!/bin/bash

current_version="$1"
major=$(echo "$current_version" | cut -d'.' -f1)
minor=$(echo "$current_version" | cut -d'.' -f2)
micro=$(echo "$current_version" | cut -d'.' -f3 | cut -d'-' -f1)

# Decrement the minor version. If minor is 0, then use the same version
if [ "$minor" -gt 0 ]; then
  previous_minor=$((minor - 1))
  # Construct the previous version
  previous_version="$major.$previous_minor.0.Final"
else
  previous_version="$current_version"
fi
echo "$previous_version"
