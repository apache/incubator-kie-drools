#!/bin/bash

optaplanner_root="$1"
optaplanner_persistence_jpa="${optaplanner_root}/optaplanner-persistence/optaplanner-persistence-jpa"

rm -rf "${optaplanner_persistence_jpa}"/src/main/java/org/optaplanner/persistence/jpa/impl/*
rm -rf "${optaplanner_persistence_jpa}"/src/test/java/org/optaplanner/persistence/jpa/impl/score/*