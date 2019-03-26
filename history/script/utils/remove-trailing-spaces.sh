#!/usr/bin/env sh

# Removes trailing whitespace characters from source files.

# Script takes one parameter:
#     - directory that should be recursively scanned to fix all files

if [ $# != 1 ]; then
    echo
    echo "Usage:"
    echo "  $0 <directory>"
    echo "For example:"
    echo "  $0 ."
    echo
    exit 1
fi

DIR=$1

find "$DIR" -name "*.java" ! -type d -exec sed -i 's/[[:space:]]\+$//' $0 {} \;
find "$DIR" -name "*.drl" ! -type d -exec sed -i 's/[[:space:]]\+$//' $0 {} \;
find "$DIR" -name "*.xml" ! -type d -exec sed -i 's/[[:space:]]\+$//' $0 {} \;
