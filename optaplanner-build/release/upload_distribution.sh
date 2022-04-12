#!/bin/bash

function display_help() {
  readonly script_name="./$(basename "$0")"

  echo "This script uploads the documentation and javadocs to the filemgmt-prod.jboss.org."
  echo "Make sure the OptaPlanner project has been build with the full profile enabled before calling this script."
  echo
  echo "Usage:"
  echo "  $script_name PROJECT_VERSION SSH_KEY"
  echo "  $script_name --help"
}

function create_latest_symlinks() {
  local _working_directory=$1
  local _version=$2

  pushd "$_working_directory"
  cd "$_working_directory"
  ln -s "$_version" latest
  if [[ "$_version" == *Final* ]]; then
    ln -s "$_version" latestFinal
  fi
  popd
}

function assert_directory_exists() {
  if [[ ! -d "$1" ]]; then
    echo "The $1 directory does not exist. Please run the build with the full profile enabled."
    exit 1
  fi
}

if [[ $1 == "--help" ]]; then
  display_help
  exit 0
fi

if [[ $# -ne 2 ]]; then
  echo "Illegal number of arguments."
  display_help
  exit 1
fi

readonly remote_optaplanner_docs=optaplanner@filemgmt-prod-sync.jboss.org:/docs_htdocs/optaplanner

readonly version=$1
readonly optaplanner_ssh_key=$2

this_script_directory="${BASH_SOURCE%/*}"
if [[ ! -d "$this_script_directory" ]]; then
  this_script_directory="$PWD"
fi

readonly optaplanner_project_root=$this_script_directory/../..
readonly optaplanner_docs_build_dir="$optaplanner_project_root/optaplanner-docs/target"
readonly optaplanner_javadoc_build_dir="$optaplanner_project_root/build/optaplanner-javadoc/target"

assert_directory_exists "$optaplanner_docs_build_dir"
assert_directory_exists "$optaplanner_javadoc_build_dir"

# Create the directory structure .../release/${version}
readonly temp_release_directory=/tmp/optaplanner-release-$version
readonly local_optaplanner_docs=$temp_release_directory/docs/release
if [ -d "$temp_release_directory" ]; then
  rm -Rf "$temp_release_directory";
fi
mkdir -p "$local_optaplanner_docs/$version/optaplanner-docs"
mkdir -p "$local_optaplanner_docs/$version/optaplanner-javadoc"

# Upload the documentation and Javadoc.
cp -r "$optaplanner_docs_build_dir/optaplanner-docs-$version"/* "$local_optaplanner_docs/$version/optaplanner-docs"
cp -r "$optaplanner_javadoc_build_dir/aggregated-javadocs/apidocs"/* "$local_optaplanner_docs/$version/optaplanner-javadoc"

readonly remote_shell="ssh -p 2222 -i $optaplanner_ssh_key"
create_latest_symlinks "$local_optaplanner_docs" "$version"
rsync -a -r -e "$remote_shell" --protocol=28 "$local_optaplanner_docs/.." "$remote_optaplanner_docs"
