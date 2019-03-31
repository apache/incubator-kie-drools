#!/bin/bash

set -euxo pipefail

# ***************************************************************************
#                                                                           *
# This script branches and upgrades to the next SNAPSHOT version            *
# the repositories                                                          *
#   kie-docker-ci-images (https://github.com/kiegroup/kie-docker-ci-images) *
#   kie-benchmarks (https://github.com/kiegroup/kie-benchmarks)             *
#   kie-cloud-test (https://github.com/kiegroup/kie-cloud-tests)            *
# These repositories are not in the repository-list.txt, but an upgrade and *
# a branching is needed.                                                    *
#                                                                           *
# ***************************************************************************

# verify the number of changed files is greater checks how many files have changed after version upgrade
verifyNumberOfUpdatedFiles () {
   # count pom.xml files with SNAPSHOT version
   pomsWithSnapshotVersionCount=$(grep snapshot -rli --include=pom.xml --exclude-dir=src | wc -l)
   # count number of files changed in git
   changedFilesCount=$(git status -s | wc -l)
   if [ "$changedFilesCount" -gt "$pomsWithSnapshotVersionCount" ]; then
     echo "Version upgrade of $repo to $newSnapshot failed."
     echo "Expected number of changed poms ($changedFilesCount) to be <= number of poms containing SNAPSHOT version ($pomsWithSnapshotVersionCount)"
     exit 1
   fi
}

cloneRepoAndCheckoutBranches () {
    git clone git@github.com:kiegroup/"$repo".git
    cd "$repo"
    git checkout -b "$newBranch" master
    git checkout master
}

commitAndPushBranches () {
    git add .
    git commit -m "upgraded to new $newSnapshot version"
    git push origin "$newBranch"
    git push origin master
    cd ..
}

# ******************** kie-docker-ci-images ************************************
repo="kie-docker-ci-images"
cloneRepoAndCheckoutBranches

# upgrade the versions to next SNAPSHOT
./scripts/update-versions.sh $newSnapshot -U

verifyNumberOfUpdatedFiles
commitAndPushBranches

# ******************** kie-benchmarks ************************************

repo="kie-benchmarks"
cloneRepoAndCheckoutBranches

# upgrade the versions to next SNAPSHOT
mvn -B -N -e versions:update-parent -Dfull -DparentVersion="[$newSnapshot]" -DallowSnapshots=true -DgenerateBackupPoms=false
mvn -B -N -e versions:update-child-modules -Dfull -DallowSnapshots=true -DgenerateBackupPoms=false

# do manual changes to pom files that were not changed in versin upgrade
sed -i "s/<version>$oldSnapshot<\/version>/<version>$newSnapshot<\/version>/;P;D" jbpm-benchmarks/kieserver-assets/pom.xml

verifyNumberOfUpdatedFiles
commitAndPushBranches

# ********************* kie-cloud-tests ***********************************
repo="kie-cloud-tests"
cloneRepoAndCheckoutBranches

# upgrade the versions to the next SNAPSHOT
mvn -B -N -e versions:update-parent -Dfull -DparentVersion="[$newSnapshot]" -DallowSnapshots=true -DgenerateBackupPoms=false
mvn -B -N -e versions:update-child-modules -Dfull -DallowSnapshots=true -DgenerateBackupPoms=false

verifyNumberOfUpdatedFiles
commitAndPushBranches