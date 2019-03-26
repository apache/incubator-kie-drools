#!/bin/bash -e

# pushes the local release branches to kiegroup or Gerrit [IMPORTANT: "push -n" (--dryrun) should be replaced by "push" when script will be in production]
if [ "$target" == "community" ]; then
  ./droolsjbpm-build-bootstrap/script/git-all.sh push origin $releaseBranch
else
  # adds a new remote Gerrit
  ./droolsjbpm-build-bootstrap/script/git-add-remote-gerrit.sh
  ./droolsjbpm-build-bootstrap/script/git-all.sh push gerrit $releaseBranch
fi