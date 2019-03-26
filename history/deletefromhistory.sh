#!/bin/bash

rm -rf $1
git add .
git commit -m "SUBMARINE deleted $1 from /history"

