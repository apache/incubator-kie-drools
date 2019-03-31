Table of content
================
* **[Releasing](#releasing)**

* **[Building a Product Tag](#building-a-product-tag)**

Releasing
=========

Expecting a release
-------------------

One week in advance:

* Announce on the upcoming release on all the developer mailing lists and in the IRC channel topics.

    * Include a list of projects on Jenkins that are yellow or red.

        * Daily remind the lead of any project that is red.

    * For a CR/Final, also mention the FindBugs reports on Jenkins.

* All external dependencies must be on a non-SNAPSHOT version, to avoid failing to *close* the staging repo on nexus near the end of the release.

    * Get those dependencies (errai) released if needed, preferably 1 week before the kie release. This way, those released artifacts gets tested by our tests.

* Ask kie-wb module (kie-uberfire-extensions, uberfire, kie-wb-common, drools-wb, jbpm-wb, jbpm-designer, optaplanner-wb and kie-wb-distributions) leads to update the translations with Zanata:

    * Translations into different locales are handled within Zanata (https://vendors.zanata.redhat.com)

    * Email Zanata mailing list that a release is about to be made.

    * The most recent translations need to be pulled into the release branch. Assuming you have set-up your Zanata configuration correctly, this can be achieved with:

        ```shell
        $ mvn zanata:pull-module
        ```

    * NOTE: If releasing a new version number (major, minor or micro) a new version of the translations should be setup in Zanata.

    * Automatically fix simple errors in the translations using the following:

        ```shell
        $ mvn replacer:replace -N
        ```

    * NOTE: For the repositories kie-wb-distributions it has to be added to the workflow

        ```shell
        $ mvn native2ascii:native2ascii
        ```

    * NOTE: jbpm-designer has it's own workflow

        ```shell
        $ cd ../jbpm-designer
        $ mvn zanata:pull-module
        $ mvn replacer:replace-N
        $ mvn native2ascii:native2ascii
        $ cd jbpm-designer-api
        $ mvn replacer:replace-N
        ```

    * Zanata workflow is:

        ```shell
        $ mvn zanata:pull-module
        $ mvn replacer:replace-N
        $ mvn native2ascii:native2ascii # In repositories where this has to be executed, please
                                        # pay attention to jbpm-designer.
        $ mvn clean install -Dfull -DskipTests # To see if everything compiles after Zanata changes were pulled.
                                               # In kie-wb-distribution has to be added -Dcustom-container for preventing
                                               # not building the repo cause possibly hanging at kie-smoke-tests
        $ git commit -a # add & commit the changes
        $ git push <upstream> <branch> # push changes to blessed repository
        ```

* Since Zanata translations was outsourced it have to be clafirfied before a release if the Zanata translations will be needed.
  (mvn -B zanata:pull-module).
  
* Get access to `filemgmt.jboss.org`

    * Create an SSH key (if not already done)

        * Key must:

            * be RSA-2 (default for many keygen apps)
            * have 1024+ bit (2048 is preferred)
            * have comment with user email address

        * Using many keygen tools the following command will work

                $ ssh-keygen -C your@email.com -b 2048

            * enter key name
            * enter passcode you want

        * Send ticket to IT

            * Have it forwarded to https://engineering.redhat.com/rt/Ticket/Create.html?Queue=58 (RT3 eng-ops-mw) queue
            * Specify that you would like access to drools@filemgmt.jboss.org
            * Attach the *.pub that you created above

48 hours in advance:

* Push deadline: Announce on the upcoming push deadline on all the developer mailing lists and in the IRC channel topics.

    * Commits pushed before the deadline will make the release, the rest won't.

* Pull the latest changes.

    ```shell
    $ git-all.sh pull --rebase
    ```

* Do a sanity check.

    * Produce the distribution zips, build with `-Dfull`:

        ```shell
        $ droolsjbpm-build-bootstrap/script/mvn-all.sh clean install -Dfull -Dcustom-container -DskipTests
        ```

        * Warning: It is not uncommon to run out of either PermGen space or Heap Space. The following settings are known (@Sept-2012) to work:-

            ```shell
            $ export MAVEN_OPTS='-Xms512m -Xmx2200m -XX:MaxPermSize=512m'
            ```

        * Warning: Verify that workspace contains no uncommitted changes or rogue module directories of older branches:

            ```shell
            $ droolsjbpm-build-bootstrap/script/git-all.sh status
            ```

            * Specifically watch out for an uncommitted `*/target` directory: that's the result of a build of an older branch that didn't get cleaned.

                * If the root of that directory gets zipped, binaries of that older branch leak into today's distribution zip.

    * Do a sanity check of the artifacts by running each runExamples.sh from the zips.

        * Go to `kie-wb-distributions/droolsjbpm-uber-distribution/target/*/download_jboss_org`:

            * Unzip the zips to a temporary directory.

            * Start the `runExamples.sh` script for drools, droolsjbpm-integration and optaplanner

            * Deploy the guvnor WildFly 8 war and surf to it:

                * Install the mortgages examples, build it and run the test scenario's

            * Verify that the reference manuals open in a browser (HTML) and Adobe Reader (PDF).

Creating a new branch 
---------------------

A new branch name should always end with `.x` so it looks different from a tag name and a topic branch name.

* When do we create a new branch?

    * We only create a new branch just before releasing CR1.

        * For example, just before releasing 6.5.0.CR1, we created the release branch 6.5.x

            * The new branch 6.2.x contained the releases 6.5.0.CR1, 6.5.0.Final, 6.5.1-SNAPSHOT, ...

    * Alpha/Beta releases are released directly from master, because we don't backport commits to Alpha/Beta's.

* Alert the IRC dev channels that you're going to branch master.

* Pull the latest changes.

    ```shell
    $ git-all.sh pull --rebase
    ```

* Create a new branch using the script kie-createNewBranches.sh:

    ```shell
    $ ./droolsjbpm-build-bootstrap/script/release/kie-createNewBranch.sh <new branch> 
    ```

    * Note: this script creates a new branch, pushes it to origin and sets the upstream from local new branch to remote new branch


* Switch back and forth from master to the new branches for all git repositories

    * If you haven't made the branches yourself, first make sure your local repository knows about them:

        ```shell
        $ droolsjbpm-build-bootstrap/script/git-all.sh fetch
        ```

    * Switch to master with `script/git-checkout-all.sh`

        ```shell
        $ droolsjbpm-build-bootstrap/script/git-checkout-all.sh <new branch>
        ```

    * Update master to the next SNAPSHOT version to avoid clashing the artifacts on nexus of master and the release branch:

        ```shell
        $ droolsjbpm-build-bootstrap/script/release/update-version-all.sh 7.6.1-SNAPSHOT 2.2.1-SNAPSHOT 
        ```

        * Note: the arguments are `kie Version` `uberfire Version`

        * WARNING: script update-version-all.sh did not update all versions in all modules for 7.6.0-SNAPSHOT. Check all have been updated with the following and re-run if required.

            ```shell
            $ grep -r '7.6.0-SNAPSHOT' **/pom.xml
            # or
            $ for i in $(find . -name "pom.xml"); do grep '7.6.0-SNAPSHOT' $i; done
            ```
            or
            ```shell
            $ grep -ER --exclude-dir=*git* --exclude-dir=*target* --exclude-dir=*idea* --exclude=*ipr --exclude=*iws --exclude=*iml --exclude=workspace* --exclude-dir=*.errai 6.3.0-SNAPSHOT . | grep -v ./kie-wb-distributions/kie-eap-integration/kie-eap-modules/kie-jboss-eap-base-modules
            ```

        * Note: in either case it is important to search for `-SNAPSHOT`, as there are various hidden `-SNAPSHOT` dependencies in some pom.xml files and they should be prevented for releases

        * Commit those changes (so you can tag them properly):

            * Add changes from untracked files if there are any. WARNING: DO NOT USE `git add .`. You may accidentally add files that are not meant to be added into git.

                ```shell
                $ git add {filename}
                ```

            * Commit all changes

                ```shell
                $ droolsjbpm-build-bootstrap/script/git-all.sh commit -m "Set release version: 6.3.0-SNAPSHOT"
                ```

            * Check if all repositories build after version upgrade

                ```shell
                $ sh droolsjbpm-build-bootstrap/mvnall.sh mvn clean install -Dfull -DskipTests
                ```

    * Push the new `-SNAPSHOT` version to `master` of the blessed directory

        ```shell
        $ sh droolsjbpbm-build-bootstrap/script/git-all.sh pull --rebase origin master (pulls all changes for master that could be commited in the meantime and prevents merge problems when pushing commits)
        $ sh droolsjbpm-build-bootstrap/script/git-all.sh push origin master (pushes all commits to master)
        ```

    * Switch back to the *release branch name* with `script/git-checkout-all.sh` with drools and jbpm *release branch name*:

        ```shell
        $ sh droolsjbpm-build-bootstrap/script/git-checkout-all.sh 7.6.x
        ```

* Push the created release branches to the blessed directory

    ```shell
    $ sh droolsjbm-build-bootstrap/script/git-all.sh push origin 7.6.x
    ```

* Set up Jenkins build jobs for the new branch.

    * Add a new branch to kiegroup/kie-jenkins-jobs

    * Edit the [jobs](https://github.com/kiegroup/kie-jenkins-scripts/tree/master/job-dsls) and do the needed adaptations for the new branch

    * Note:since all these Jenkins Jobs are done with a DSL Plugin there are two things that should be done so all jobs are available:
   
        - the file [branch-mapping.yaml](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/script/branch-mapping.yaml) should be upgraded to the new branch
        - the Jenkins Jobs
        
          https://kie-jenkins.rhev-ci-vms.eng.rdu2.redhat.com/view/DSL/job/DSL-KIE-releases-master/
        
          https://kie-jenkins.rhev-ci-vms.eng.rdu2.redhat.com/view/DSL/job/DSL-kieAllBuild-FlowJob-master/
        
          https://kie-jenkins.rhev-ci-vms.eng.rdu2.redhat.com/view/DSL/job/DSL-seed-job-master/
        
          should be updated to the new branch
      
* Set up a new Jenkins view for the related release builds

    * https://jenkins.mw.lab.eng.bos.redhat.com/hudson/me/my-views/view/All/ (i.e. [7.5.x](https://kie-jenkins.rhev-ci-vms.eng.rdu2.redhat.com/view/7.5.x/))

        * Note: Add kie <new branch> jobs manually or use a regex pattern similar to `^((kie).*7).*$`

* Alert the dev mailing list and the IRC channel that the branch has been made.

    * Remind everyone clearly that every new commit to `master` will not make the upcoming CR and Final release, unless they cherry-pick it to this new branch.


#### NOTE:
* at this point we have created a release branch
* we have updated the master branch to the new development version (`*-SNAPSHOT`)
* we have pushed the created release branches to origin
* we have set up a new Jenkins view for the created "release branch"


Releasing from a release branch
-------------------------------

* Alert the IRC dev channels that you're starting the release.

* Pull the latest changes of the branch that will be the base for the release (branchName == master or i.e. 7.6.x)

    ```shell
    $ git-all.sh checkout <branchName>
    $ git-all.sh pull --rebase
    ```

* Create a local release branch

    Name should begin with r, i.e if the release will be 7.6.0.Final the name should be r7.6.0.Final (localReleaseBranchName == r7.6.0.Final)

    ```shell
    $ git-all.sh checkout -b <localReleaseBranchName> <branchname>    
    ```

* Check if everything builds after the last pull & execute all unit tests

    ```shell
    $ mvn-all.sh clean install -Dfull -Dmaven.test.failure.ignore=true > testResult.txt
    # This will execute the build and execute the unit tests and write all logs into testResult.txt.
    ```

* Explore testResult.txt to see if the build breaks or which unit tests are failing.

* Mail to leads of projects the failed unit tests.

* Do another sanity check.

If everything is perfect (compiles, Jenkins is all blue, sanity checks succeed and there is nothing to do about the failed unit tests):

* Define the version and adjust the sources accordingly:

    * First define the version.

        * There are only 4 acceptable patterns:

            * `major.minor.micro.Alpha[n]`, for example `1.2.3.Alpha1`

            * `major.minor.micro.Beta[n]`, for example `1.2.3.Beta1`

            * `major.minor.micro.CR[n]`, for example `1.2.3.CR1`

            * `major.minor.micro.Final`, for example `1.2.3.Final`

        * See the [JBoss version conventions](http://community.jboss.org/wiki/JBossProjectVersioning)

            * Not following those, for example `1.2.3` or `1.2.3.M1` results in OSGi eclipse updatesite corruption.

        * **The version has 3 numbers and qualifier. The qualifier is case-sensitive and starts with a capital.**

            * Use the exact same version everywhere (especially in URL's).

    * Adjust the version in the poms, manifests and other eclipse stuff.

            $ droolsjbpm-build-bootstrap/script/release/update-version-all.sh 6.2.0-SNAPSHOT 6.2.0.Final

        * Note: the arguments are `releaseOldVersion releaseNewVersion`

        * WARNING: FIXME the update-version-all script does not work correctly if you are releasing a hotfix version.

        * WARNING: Guvnor has a hard-coded version number in org.drools.guvnor.server.test.GuvnorIntegrationTest.createDeployment. This must be changed manually and committed.

        * WARNING: script update-version-all.sh did not update automatically all versions in all modules. Check all have been updated with the following and re-run if required.

            ```shell
            $ grep -r '6.2.0-SNAPSHOT' **/pom.xml
            # or
            for i in $(find . -name "pom.xml"); do grep '6.2.0-SNAPSHOT' $i; done
            ```
            OR
            ```shell
            $ grep -ER --exclude-dir=*git* --exclude-dir=*target* --exclude-dir=*idea* --exclude=*ipr --exclude=*iws --exclude=*iml --exclude=workspace* --exclude-dir=*.errai 6.3.0-SNAPSHOT . | grep -v ./kie-wb-distributions/kie-eap-integration/kie-eap-modules/kie-jboss-eap-base-modules.
            ```

    * versions that have to be changed manually

        NOTE: in droolsjbpm-build-bootstrap pom.xml there are some properties where you should pay attention to:

        1. jboss-ip bom version (https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/pom.xml#L11)
           the version of jboss-integration-platform-bom. should be the most recent version released  in jboss-ip-bom

        2. org.kie version (https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/pom.xml#L48)
           org.kie version sometimes has to be changed manually, if needed, should be updated to release version

        3. uberfire version (https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/pom.xml#L53)
           the uberfire version has to be updated manually to the last released version

        4. errai version (https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/pom.xml#L99)
           the errai version has to be updated manually to the last released version

    * Commit those changes (so you can tag them properly):

        * Add changes from untracked files if there are any. WARNING: DO NOT USE `git add .` . You may accidentally add files that are not meant to be added into git.

            ```shell
            $ git add {filename}
            ```

        * Commit all changes

            ```shell
            $ droolsjbpm-build-bootstrap/script/git-all.sh commit -m "Set release version: 6.2.0.Final"
            ```

        * Adjust the property *`<latestReleasedVersionFromThisBranch>`* in *droolsjbpm-build-bootstrap/pom.xml*

         This should be the version that will be released now.
         This is important as productisation takes this version to define theirs.

         * Add this change
         * Commit this change.


        
* Push release branches to github repository

    The release branches rX.X.X.Y should be pushed to the github repository (community=kiegroup/... or product=jboss-integration/...), so the branch
    is available for all future steps. People can access it to review, if all commits that should be in the release were commited.<br>
    This branch has to be removed when doing the next release as a new branch starting with "r" will be pushed and we want prevent having a bunch of "obsolete" release branches.


* Create the tag locally. The arguments are the Drools version, the jBPM version:

    ```shell
    $ droolsjbpm-build-bootstrap/script/release/git-tag-locally-all.sh 6.2.0.Final 6.2.0.Final
    ```


* Go to [nexus](https://repository.jboss.org/nexus), menu item *Staging repositories*, drop all your old staging repositories.


* Deploy the artifacts:

    ```shell
    ./droolsjbpm-build-bootstrap/script/mvn-all.sh -B -e -U clean deploy -Dfull -Drelease -T2 -Dmaven.test.failure.ignore=true -Dgwt.memory.settings="-Xmx4g -Xms1g -Xss1M" -Dgwt.compiler.localWorkers=2
    ```
    * Note: add this parameter -Dproductized if this is a release/tag for prod

    * This will take a long while (3+ hours)

    * If it fails for any reason, go to nexus and drop your stating repositories again and start over.

* Go to [nexus](https://repository.jboss.org/nexus), menu item *Staging repositories*, find your staging repository.

    * Look at the files in the repository.

        * Sometimes they are split into 2 staging repositories (with no intersecting files): just threat those 2 as 1 staging repository.

    * Button *close*

        * This will validate the nexus rules. If any fail: fix the issues, and force a git retag locally.


* Do another sanity check of the artifacts by running the examples and opening the manuals from the zips. See above.


* This is **the point of no return**.

    * Warning: The slightest change after this requires the use of the next version number!

        * **NEVER TAG OR DEPLOY A VERSION THAT ALREADY EXISTS AS A PUSHED TAG OR A DEPLOY!!!**

            * Except deploying `SNAPSHOT` versions.

            * Git tags are cached on developer machines forever and are never refreshed.

            * Maven non-snapshot versions are cached on developer machines and proxies forever and are never refreshed.

        * So even if the release is broken, do not reuse the same version number! Create a hotfix version.


* Define the next development version an adjust the sources accordingly:

    * Checkout to the master-branch or the branch which is the base for this release.

        ```shell
        $ git-all.sh checkout master (or base release branch i.e. 6.2.x)
        ```

    * Define the next development version on the branch from which you are releasing.

        * There are only 1 acceptable pattern:

            * `major.minor.micro-SNAPSHOT`, for example `1.2.0-SNAPSHOT` or `1.2.1-SNAPSHOT`

        * Warning: The release branch should never have the same SNAPSHOT version as any other branch.

            * If you're releasing a Final, increment the micro number, not the minor number.

    * Adjust the version in the poms, manifests and other eclipse stuff:

        ```shell
        $ droolsjbpm-build-bootstrap/script/release/update-version-all.sh 6.2.0.Final 6.3.0-SNAPSHOT 6.2.0.Final 6.3.0-SNAPSHOT
        ```

        * Commit those changes:

            ```shell
            $ droolsjbpm-build-bootstrap/script/git-all.sh add .

            $ droolsjbpm-build-bootstrap/script/git-all.sh commit -m "Set next development version: 6.3.0-SNAPSHOT"
            ```

        * Push all changes to the blessed repository:

            ```shell
            $ droolsjbpm-build-bootstrap/script/git-all.sh push
            ```

        * Warning: Guvnor has a hard-coded version number in org.drools.guvnor.server.test.GuvnorIntegrationTest.createDeployment. This must be changed manually and committed.

        * Warning: script update-version-all.sh did not update all versions in all modules for 6.2.0.Final. Check all have been updated with the following and re-run if required.

            ```shell
            $ grep -r '6.2.0-SNAPSHOT' **/pom.xml
            # or
            for i in $(find . -name "pom.xml"); do grep '6.2.0-SNAPSHOT' $i; done
            ```

        * Warning: If releasing from master (i.e. a Beta release) and the push fails as there have been other commits to the remote master branch it might be necessary to pull.

            ```shell
            $ droolsjbpm-build-bootstrap/script/git-all.sh pull
            ```

    * Checkout back to your local release branch.

        ```shell
        $ git-all.sh checkout r6.2.0.Final
        ```


* Push the local tag from the local release branch to the remote blessed repository.

      $ droolsjbpm-build-bootstrap/script/release/git-push-tag-all.sh 6.2.0.Final 6.2.0.Final

    * Push your changes to the release branch:

        * Especially if the release branch is master: First pull any latest changes **without `--rebase`**, .

            ```shell
            $ git-all.sh pull
            ```

            * Without the `--rebase` it's a merge, and their commits will not be rebased before your version-changing commits.

        * Push your version-changing commits to the release branch:

            ```shell
            $ git-all.sh push origin 5.2.x
            ```

* Release your staging repository on [Nexus](https://repository.jboss.org/nexus)

    * Button *release*

* Go to [JIRA](https://issues.jboss.org) and for each of our JIRA projects (DROOLS, PLANNER, JBPM, GUVNOR):

    * Open menu item *Administration*, link *Manage versions*, release the version.

    * Create a new version if needed. There should be at least 2 unreleased non-FUTURE versions.

* Upload the zips, documentation and javadocs to filemgmt and update the website.

    * Go to `kie-wb-distributions/droolsjbpm-uber-distribution/target`.

    * To get access to `filemgmt.jboss.org`, see preparation above.

    * Folder `download_jboss_org` should be uploaded to `filemgmt.jboss.org/downloads_htdocs/drools/release`
    which ends up at [download.jboss.org](http://download.jboss.org/drools/release/)

        * Update [the download webpage](http://www.jboss.org/drools/downloads) accordingly.

    * Folder `docs_jboss_org` should be uploaded to `filemgmt.jboss.org/docs_htdocs/drools/release`
    which ends up at [docs.jboss.org](http://download.docs.org/drools/release/)

        * Use `documentation_table.txt` to update [the documentation webpage](http://www.jboss.org/drools/documentation).

* Update the symbolic links `latest` and `latestFinal` links on filemgmt, if and only if there is no higher major or minor release was already released.

    ```shell
    $ droolsjbpm-build-bootstrap/script/release/create_filemgmt_links.sh 7.6.0.Final
    ```

    * Wait 5 minutes and then check these URL's. Hit ctrl-F5 in your browser to do a hard refresh:

        * [http://download.jboss.org/drools/release/latest/](http://download.jboss.org/drools/release/latest/)

        * [http://download.jboss.org/drools/release/latestFinal/](http://download.jboss.org/drools/release/latestFinal/)

        * [http://docs.jboss.org/drools/release/latest/](http://docs.jboss.org/drools/release/latest/)

        * [http://docs.jboss.org/drools/release/latestFinal/](http://docs.jboss.org/drools/release/latestFinal/)

* If it's a Final, non-hotfix release: publish the XSD file(s), by copying each XSD file to its website.

    * The Drools XSD files are at http://www.drools.org/xsd/[http://www.drools.org/xsd/]
    
    * Go to the https://github.com/kiegroup/droolsjbpm-knowledge/blob/master/kie-api/src/main/resources/org/kie/api/kmodule.xsd[kmodule.xsd] file (on master) and switch to the release tag.
    
    * Copy the raw file to https://github.com/kiegroup/drools-website/tree/master/xsd[drools-website's `xsd` directory].
    
    * Rename it from `kmodule.xsd` to `kmodule_<major>_<minor>.xsd` so it includes its version (major and minor only, not hotfixes or quantifiers). For example for release `6.3.0.Final` it is renamed to `kmodule_6_3.xsd`. Do not overwrite an existing file as there should never be an existing file (because the XSD is only copied for Final, non-hotfix releases).
    
    * Publish drools.org
    
* Protect the new creaed branches on github against forced pushes
    
    - https://github.com/kiegroup/<rep>/settings/branches
    - choose the new branch 
    
    
Announcing the release
----------------------

* Create a blog entry on [the kiegroup blog](http://blog.athico.com/)

    * Include a direct link to the new and noteworthy section and to that blog entry in all other correspondence.

    * Twitter and Google+ the links.

        * Most people just want to read the new and noteworthy, so link that first.

    * Mail the links to the user list.

* If it's a Final, non-hotfix release:

    * Notify TheServerSide and Dzone's Daily Dose.


Building a Product Tag
======================
**This paragraph describes the building of a product tag**

The community code repositories under the @kiegroup account contains all the code released as part of the community projects for Drools and jBPM. Every time a new minor or major version is released,
a new community branch is created for that version. For instance, at the time of this writing, we have, for instance, branches **master, 7.5.x, 6.5.x**, etc for each minor/major version released and
the *master* branch for future releases. Red Hat also has a mirror private repository that is used as a base for the product releases. This mirror repository contains all the code from the community
repositories, plus a few product specific commits, comprising branding commits (changing names, for instance from Drools to BRMS), different icons/images, etc.

This new tag will usually be based on the HEAD of a specific community branch with the product specific commits applied on top of it.

Follows an instruction on how to do that. These instructions assume:

* You have a local clone of all Drools/jBPM repositories
* The clones have a remote repository reference to the @kiegroup repositories that we will name **origin**
* The clones have a remote repository reference to the @jboss-integration (> 6.5.x) or Gerrit (>7.5.x) (remote: **jboss-integration** OR **gerrit**)

Here are the steps:

**1 - clone droolsjbpm-build-bootstrap repository (base branch = master or 6.5.x or 7.5.x)**

    $ git clone git@github.com:kiegroup/droolsjbpm-build-bootstrap.git --branch <base branch> --depth 100

**2 - cd into the droolsjbpm-build-bootstrap repository**

    $ cd droolsjbpm-build-bootstrap

**3 - Fetch the changes from the repository:**

    $ .script/git-all.sh fetch <remote> (i.e. remote = orgin)

**4 - Rebase the corresponding branches (base branch = master or 6.5.x or 7.5.x)**

    $ .script/git-all.sh rebase <remote>/<base branch> <base branch> (i.e. remote = origin)

**5 - Create a local branch to base the tag on. I usually name the base branch as "bsync-base branch-YYYY.MM.DD" where YYYY.MM.DD is the year, month and day when the tag is being created.**

    $ .script/git-all.sh checkout -b bsync-<base-branch>-YYYY.MM.DD <branch to base the tag on> (i.e. bsync-7.5.x-2017.01.15)

**6 - Build local branch with product specific commits to make sure it is working. Fix any problems in case it is not working.**

    $ .script/mvn-all.sh -B -e -U clean install -Dfull -Drelease -Dproductized -T2 -Dgwt.memory.settings="-Xmx4g -Xms1g -Xss1M" -Dgwt.compiler.localWorkers=2

**7 - Create the tag for all repositories. For product tags, we use a naming standard of "sync-<base branch>-YYYY.MM.DD", where YYYY.MM.DD is the date the tag is created. If for any reason more than one tag needs to be created on the same day, add a sequential counter sufix: "sync-<base branch>-YYYY.MM.DD.C"**

    $ tag=sync-<base branch>-YYYY.MM.DD (i.e. sync-7.5.x-2018.01.15)
    $ commitMsg="Tagging $tag"
    $ .script/git-add-remote-gerrit.sh - (adds a new remote gerrit) 
    ONLY for 6.5.x: .script/git-add-remote-jboss-integration.sh (add a new remote jboss-integration)
    $ .script/git-all.sh tag -a $tag -m "$commitMsg" - (creates the tags)

**8 - Push the tag and branches to the _product_ server.**

    $ .script/git-all.sh push gerrit $tag  - (pushes the tags to gerrit)
    ONLY for 6.5.x: .script/git-all.sh push jboss-integration $tag - (pushes the tags to jboss-integration)
