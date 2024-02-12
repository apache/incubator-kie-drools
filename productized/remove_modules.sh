#!/usr/bin/env bash
#
# This script expects a configuration file separated by semicolon, where the first part is the pom and the second the modules to be removed from that pom
# It can be invoked from project root folder using: $ ./productized/remove_modules.sh ./productized/modules

# fast fail
set -e
set -o pipefail

if [ $# -lt 1 ];
then
  echo "$0: Missing arguments"
  exit
fi

config_file="${1}"
for line in `cat "$config_file"`
do
    pom=$(echo $line | cut -d ';' -f1)
    modules=$(echo $line | cut -d ';' -f2)

    modules_list=$(echo $modules | tr "," "\n")
    for module in ${modules_list}
    do
        if ! grep -Rq "<module>${module}</module>" ${pom} ; then
            echo "Could not find module ${module} in ${pom}. Exiting script..."
            exit 1
        fi
        echo "Removing module ${module} from ${pom}"
        sed -i "/<module>${module}<\/module>/d" ${pom}
    done
done