# Kogito release procedure

## Release version

Follow the steps below to perform complete release of Kogito

NOTE: Make sure you always run the scripts from this (release) directory

* Set release version number as environment variable
`export RELEASE_VERSION=0.1.0`
* Set base branch as environment variable
`export BASE_BRANCH=master`
* Set target branch as environment variable
`export TARGET_BRANCH=0.1.x`

* create local release branches 
`./01-create-local-release-branches.sh $RELEASE_VERSION $BASE_BRANCH $TARGET_BRANCH`

* update version in all repositories of Kogito
`./02-update-version-all.sh $RELEASE_VERSION`

* build (maven build) of updated projects (skipping tests and using custom local repo in /tmp/kogito-release-repo)
`./03-build-local-release-branches.sh`

* upon successful build commit version updated
`./04-commit-local-release-branches.sh $RELEASE_VERSION`

* tag and push to origin updates
`./05-tag-and-push-local-release-branches.sh $RELEASE_VERSION $TARGET_BRANCH` 

* deploy to remote maven repository - this is pushing artifacts to remote repository with built version
`./06-deploy-release.sh`

## New development version

Once the release is done, upgrade version for next development version

* Set development version number as environment variable - make sure that the version number ends with -SNAPSHOT
`export DEVELOPMENT_VERSION=0.2.0-SNAPSHOT`

* switch back to master branch
`./07-back-to-master-branches.sh`

* upgrade to next development version
`./08-next-development-version.sh $DEVELOPMENT_VERSION`

* create pull requests for all repositories of Kogito with new development version 