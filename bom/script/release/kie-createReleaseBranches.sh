#!/bin/bash -e

# clone droolsjbpm-build-bootstrap
git clone git@github.com:kiegroup/droolsjbpm-build-bootstrap.git --branch $baseBranch --depth 70

# clone rest of the repos
./droolsjbpm-build-bootstrap/script/git-clone-others.sh --branch $baseBranch --depth 70

if [ "$source" == "community-branch" ]; then

   # checkout to local release names
   ./droolsjbpm-build-bootstrap/script/git-all.sh checkout -b $releaseBranch $baseBranch

fi

if [ "$source" == "community-tag" ]; then

   # get the tags of community
   ./droolsjbpm-build-bootstrap/script/git-all.sh fetch --tags origin
   
   # checkout to local release names
   ./droolsjbpm-build-bootstrap/script/git-all.sh checkout -b $releaseBranch $tag

fi
   
if [ "$source" == "production-tag" ]; then

   # add new remote pointing to gerrit
   ./droolsjbpm-build-bootstrap/script/git-add-remote-gerrit.sh
   
   # get the tags of gerrit
   ./droolsjbpm-build-bootstrap/script/git-all.sh fetch gerrit --tags
   
   # checkout to local release names
   ./droolsjbpm-build-bootstrap/script/git-all.sh checkout -b $releaseBranch $tag

fi

# upgrades the version to the release/tag version
./droolsjbpm-build-bootstrap/script/release/update-version-all.sh $releaseVersion $uberfireVersion custom


# change properties via sed as they don't update automatically
#appformer
cd appformer
sed -i \
-e "$!N;s/<version.org.kie>.*.<\/version.org.kie>/<version.org.kie>$releaseVersion<\/version.org.kie>/;" \
-e "s/<version.org.jboss.errai>.*.<\/version.org.jboss.errai>/<version.org.jboss.errai>$erraiVersion<\/version.org.jboss.errai>/;P;D" \
pom.xml
cd ..

#droolsjbpm-build-bootstrap
cd droolsjbpm-build-bootstrap/
sed -i \
-e "$!N;s/<version.org.uberfire>.*.<\/version.org.uberfire>/<version.org.uberfire>$uberfireVersion<\/version.org.uberfire>/;" \
-e "s/<version.org.kie>.*.<\/version.org.kie>/<version.org.kie>$releaseVersion<\/version.org.kie>/;" \
-e "s/<version.org.jboss.errai>.*.<\/version.org.jboss.errai>/<version.org.jboss.errai>$erraiVersion<\/version.org.jboss.errai>/;" \
-e "s/<latestReleasedVersionFromThisBranch>.*.<\/latestReleasedVersionFromThisBranch>/<latestReleasedVersionFromThisBranch>$releaseVersion<\/latestReleasedVersionFromThisBranch>/;P;D" \
pom.xml
cd ..

# git add and commit the version update changes 
./droolsjbpm-build-bootstrap/script/git-all.sh add .
commitMsg="Upgraded versions for release $releaseVersion"
./droolsjbpm-build-bootstrap/script/git-all.sh commit -m "$commitMsg"


