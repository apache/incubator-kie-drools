#!/usr/bin/env sh

# Replaces tabs ('\t') in source files with spaces. Number of spaces depends on type of the file:
#    - 4 spaces for *.java and *.drl files
#    - 2 spaces for *.xml files

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

find "$DIR" -name '*.java' ! -type d -exec bash -c 'expand -t 4 "$0" > /tmp/tabs2spaces-tmp && mv /tmp/tabs2spaces-tmp "$0"' {} \;
find "$DIR" -name '*.drl' ! -type d -exec bash -c 'expand -t 4 "$0" > /tmp/tabs2spaces-tmp && mv /tmp/tabs2spaces-tmp "$0"' {} \;
find "$DIR" -name '*.xml' ! -type d -exec bash -c 'expand -t 2 "$0" > /tmp/tabs2spaces-tmp && mv /tmp/tabs2spaces-tmp "$0"' {} \;
