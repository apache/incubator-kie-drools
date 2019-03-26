#!/bin/bash

mv $1 history
git add .
git commit -m "SUBMARINE moved $1 into /history"
