#!/bin/bash -e

for repositoryUrl in `cat $ZANATA` ; do
   echo

   if [ ! -d $repositoryUrl ]; then
      echo "==============================================================================="
      echo "Repository: $repositoryUrl"
      echo "==============================================================================="

      cd $WORKSPACE
      git clone $repositoryUrl --branch $kieMainBranch
      echo $repositoryUrl > rep.txt
      repDir=$(sed -e 's/.*\///' -e 's/.\{4\}$//' rep.txt)
      echo "repository="$repDir
      cd $repDir

      if [ "$repDir" == "kie-wb-distributions" ]; then

         mvn -B zanata:push-module -Dproductized

      elif [ "$repDir" == "appformer" ]; then

         mvn -B -U zanata:push-module -pl '!uberfire-bom'

      else

      mvn -B -U zanata:push-module

      fi

   fi
done
