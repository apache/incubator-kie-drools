#!/bin/bash

# This script copies the antora-template.yml with asciidoc attributes substituted by a maven build to src/antora.yml
# in optaplanner-docs.

this_script_directory="${BASH_SOURCE%/*}"
if [[ ! -d "$this_script_directory" ]]; then
  this_script_directory="$PWD"
fi

readonly optaplanner_project_root=$this_script_directory/../..
readonly optaplanner_docs=$optaplanner_project_root/optaplanner-docs
readonly antora_yml=$optaplanner_docs/src/antora.yml
readonly antora_yml_template=$optaplanner_docs/target/antora-template.yml
if [ ! -f "$antora_yml_template" ]; then
    echo "The $antora_yml_template with substituted attributes was not found. Maybe build the optaplanner-docs first."
    exit 1
fi

cp "$antora_yml_template" "$antora_yml"


