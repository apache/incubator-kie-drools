/bin/bash -e

# removing errai artifacts from local maven repo (basically all possible SNAPSHOTs)
if [ -d $MAVEN_REPO_LOCAL ]; then
  rm -rf $MAVEN_REPO_LOCAL/org/jboss/errai/
fi

# clone the Errai repository
git clone https://github.com/errai/errai.git -b $erraiBranch --depth 100

# checkout the release branch
cd errai
git checkout -b $erraiVersionNew $erraiBranch

# update versions
sh updateVersions.sh $erraiVersionOld $erraiVersionNew

# add pom.versionsBackup to gitignore as these files are not desired to push
echo "*.versionsBackup" >> \.gitignore

# git add . and commit all changes
git add \.
git commit -m "upgraded to $erraiVersionNew version"

# build the repos & deploy into local dir (will be later copied into staging repo)
deployDir=$WORKSPACE/deploy-dir

# we will deploy into remote staging repo only once the whole build passed (to save time and bandwith)
mvn -U -B -e clean deploy -T2 -Dfull -Drelease -DaltDeploymentRepository=local::default::file://$deployDir\
 -Dmaven.test.failure.ignore=true -Dgwt.compiler.localWorkers=3

# upload the content to remote staging repo
cd $deployDir
mvn -B -e org.sonatype.plugins:nexus-staging-maven-plugin:1.6.8:deploy-staged-repository -DnexusUrl=https://repository.jboss.org/nexus -DserverId=jboss-releases-repository -DrepositoryDirectory=$deployDir\
 -DstagingProfileId=15c3321d12936e -DstagingDescription="errai $erraiVersionNew" -DstagingProgressTimeoutMinutes=30


# tag errai for prod
cd $WORKSPACE/errai
git tag -a $erraiTag -m "tagged $erraiTag"

# add a new remote ponting to gerrit
git remote add gerrit ssh://jb-ip-tooling-jenkins@code.engineering.redhat.com/errai/errai

# push the tag to gerrit
git push gerrit $erraiTag
