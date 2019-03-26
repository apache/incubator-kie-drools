#!/bin/bash -e

TARGET_USER=kiereleaseuser
DATE=$(date "+%Y-%m-%d")
KIE_BRANCH=$kieMainBranch

for REPOSITORY_URL in `cat $ZANATA` ; do
   echo

   if [ ! -d $REPOSITORY_URL ]; then
      echo "==============================================================================="
      echo "Repository: $REPOSITORY_URL"
      echo "==============================================================================="

      cd $WORKSPACE
      git clone $REPOSITORY_URL --branch $KIE_BRANCH
      echo $REPOSITORY_URL > rep.txt
      REP_DIR=$(sed -e 's/.*\///' -e 's/.\{4\}$//' rep.txt)
      echo "rep_dir="$REP_DIR
      cd $REP_DIR
      ZANATA_BRANCH="$REP_DIR-ZanataChanges-$DATE-$KIE_BRANCH"
      git checkout -b $ZANATA_BRANCH $KIE_BRANCH

      if [ "$REP_DIR" == "appformer" ]; then
        mvn -B zanata:pull-module -pl '!uberfire-bom'
        mvn replacer:replace -N
      else
        mvn -B zanata:pull-module
        mvn replacer:replace -N
      fi

      #where to apply native 2ascii
      if [ "$REP_DIR" == "jbpm-designer" ]; then
         cd $WORKSPACE/jbpm-designer/jbpm-designer-api
         mvn replacer:replace -N
         cd ..
      fi

      if [ "$REP_DIR" == "kie-wb-distributions" ]; then
         mvn native2ascii:native2ascii
         mvn -B zanata:pull-module -Dproductized
         mvn replacer:replace -N -Dproductized
         mvn native2ascii:native2ascii -Dproductized
         mvn copy-rename:copy@copy-zanata-files
         mvn copy-rename:rename@rename-zanata-files

      fi

      git add .

      toDo=$(git status)
      NOTHING="nothing to commit"

      if [[ $toDo =~ $NOTHING ]]; then
         echo  "no commits so nothing to do"
         continue;
      else
         git commit -m "Zanata translation changes of $DATE"
      fi

      LOCAL=$(git rev-parse HEAD)
      REMOTE=$(git rev-parse $KIE_BRANCH)
      BLESSED_BRANCH=$KIE_BRANCH
      SOURCE=kiegroup

      if [ "$LOCAL" == "$REMOTE" ]; then
         echo "branches are the UP TO DATE"
         continue;
      else
         echo "branches have diverged"

         COMMIT_MSG="latest Zanata translation changes in $REP_DIR from $DATE"

         # create a PR on branch

         git remote add $TARGET_USER git@github.com:$TARGET_USER/$REP_DIR.git
         git push -f $TARGET_USER $ZANATA_BRANCH
         hub pull-request -m "$COMMIT_MSG" -b $SOURCE:$BLESSED_BRANCH -h $TARGET_USER:$ZANATA_BRANCH
      fi
   fi
done
