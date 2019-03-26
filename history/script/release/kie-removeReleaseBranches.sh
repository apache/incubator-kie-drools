#!/bin/bash -e

if [ "$target" == "community" ]; then
   source=kiegroup
else
   source=jboss-integration
fi

blessed-branches=("6.2.x" "6.3.x" "6.4.x" "6.5.x" "7.0.x" "7.1.x" "7.2.x" "master")

if [[ " ${blessed-branches[*]} "  ==  *"$releaseBranch"*  ]]; then
    echo "Branch $baseBranch can't be removed"
    exit 1
else
   echo "$baseBranch will be removed"

   git clone git@github.com:"$source"/droolsjbpm-build-bootstrap.git --branch $releaseBranch --depth 50

   # clone rest of the repos and checkout to this branch
   ./droolsjbpm-build-bootstrap/script/git-clone-others.sh --branch $releaseBranch --depth 50

   # remove release-branches on kiegroup or on jboss-integration
   ./droolsjbpm-build-bootstrap/script/git-all.sh push $source :$releaseBranch
fi
