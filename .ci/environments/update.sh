#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)

environment=$1
shift

if [ -z "${environment}" ]; then
    echo "No environment given as first argument"
    exit 1
fi
env_path="${script_dir_path}/${environment}"
echo $env_path
if [ ! -d "${env_path}" ]; then
    echo "No configuration given for this environment ... Nothing done !"
    exit 0
fi

echo "Update project for environment '${environment}'"

# If update script is present, apply it
if [ -f "${env_path}/before.sh" ]; then
    echo "Run before script"
    sh ${env_path}/before.sh $@
fi

# Apply patches if any
patches_path="${env_path}"/patches
if [ -d ${patches_path} ]; then
    for patch_file in "${patches_path}"/*
    do
        echo "Apply git patch ${patch_file}"
        git apply ${patch_file}
    done
else
    echo 'No patch to apply'
fi

# If update script is present, apply it
if [ -f "${env_path}/after.sh" ]; then
    echo "Run after script"
    sh ${env_path}/after.sh
fi

# Download `setup_integration_branch` script and execute
curl -s https://raw.githubusercontent.com/apache/incubator-kie-kogito-pipelines/main/dsl/seed/scripts/setup_integration_branch.sh | bash