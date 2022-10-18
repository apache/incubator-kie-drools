#!/bin/bash -e

# Used to retrieve current git author, to set correctly the main config file repo
GIT_SERVER='github.com'

git_url=$(git remote -v | grep origin | awk -F' ' '{print $2}' | head -n 1)
if [ -z "${git_url}" ]; then
  echo "Script must be executed in a Git repository for this script to run correctly"
  exit 1
fi
echo "git_url = ${git_url}"

git_server_url=
if [[ "${git_url}" = https://* ]]; then
  git_server_url="https://${GIT_SERVER}/"
elif [[ "${git_url}" = git@* ]]; then 
  git_server_url="git@${GIT_SERVER}:"
else
  echo "Unknown protocol for url ${git_url}"
  exit 1
fi

git_author="$(echo ${git_url} | awk -F"${git_server_url}" '{print $2}' | awk -F. '{print $1}'  | awk -F/ '{print $1}')"

export DSL_DEFAULT_MAIN_CONFIG_FILE_REPO="${git_author}"/optaplanner
export DSL_DEFAULT_FALLBACK_MAIN_CONFIG_FILE_REPO=kiegroup/optaplanner
export DSL_DEFAULT_MAIN_CONFIG_FILE_PATH=.ci/jenkins/config/main.yaml
export DSL_DEFAULT_BRANCH_CONFIG_FILE_REPO="${git_author}"/optaplanner

file=$(mktemp)
# For more usage of the script, use ./test.sh -h
curl -o ${file} https://raw.githubusercontent.com/kiegroup/kogito-pipelines/main/dsl/seed/scripts/seed_test.sh
chmod u+x ${file}
${file} $@