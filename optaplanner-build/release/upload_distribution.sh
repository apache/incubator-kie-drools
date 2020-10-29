#!/bin/bash

function display_help() {
  readonly script_name="./$(basename "$0")"

  echo "This script uploads the documentation, javadocs and distribution.zip to the filemgmt.jboss.org."
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

if [[ $1 == "--help" ]]; then
  display_help
  exit 0
fi

if [[ $# -ne 2 ]]; then
  echo "Illegal number of arguments."
  display_help
  exit 1
fi

readonly remote_optaplanner_downloads=optaplanner@filemgmt.jboss.org:/downloads_htdocs/optaplanner
readonly remote_optaplanner_docs=optaplanner@filemgmt.jboss.org:/docs_htdocs/optaplanner

readonly version=$1
readonly optaplanner_ssh_key=$2

this_script_directory="${BASH_SOURCE%/*}"
if [[ ! -d "$this_script_directory" ]]; then
  this_script_directory="$PWD"
fi

readonly optaplanner_project_root=$this_script_directory/../..
readonly optaplanner_distribution=$optaplanner_project_root/optaplanner-distribution
readonly optaplanner_distribution_build_dir="$optaplanner_distribution/target/optaplanner-distribution-$version"

if [[ ! -d "$optaplanner_distribution_build_dir" ]]; then
  echo "The OptaPlanner distribution does not exist. Please run the build with the full profile enabled."
  exit 1
fi

# Create the directory structure .../release/${version}
readonly temp_release_directory=/tmp/optaplanner-release-$version
readonly local_optaplanner_downloads=$temp_release_directory/downloads/release
readonly local_optaplanner_docs=$temp_release_directory/docs/release
if [ -d "$temp_release_directory" ]; then
  rm -Rf "$temp_release_directory";
fi
mkdir -p "$local_optaplanner_docs/$version/optaplanner-docs"
mkdir -p "$local_optaplanner_docs/$version/optaplanner-javadoc"
mkdir -p "$local_optaplanner_downloads/$version"

# Upload the OptaPlanner distribution.zip.
cp "$optaplanner_distribution/target/optaplanner-distribution-$version.zip" "$local_optaplanner_downloads/$version"

readonly remote_shell="ssh -oKexAlgorithms=+diffie-hellman-group1-sha1 -i $optaplanner_ssh_key"
create_latest_symlinks "$local_optaplanner_downloads" "$version"
rsync -a -r -e "$remote_shell" --protocol=28 "$local_optaplanner_downloads/.." "$remote_optaplanner_downloads"

# Upload the documentation and Javadoc.
cp -r "$optaplanner_distribution_build_dir"/reference_manual/* "$local_optaplanner_docs/$version/optaplanner-docs"
cp -r "$optaplanner_distribution_build_dir"/javadocs/* "$local_optaplanner_docs/$version/optaplanner-javadoc"

create_latest_symlinks "$local_optaplanner_docs" "$version"
rsync -a -r -e "$remote_shell" --protocol=28 "$local_optaplanner_docs/.." "$remote_optaplanner_docs"
