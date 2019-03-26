Developing Drools and jBPM
==========================

**If you want to build or contribute to a kiegroup project, read this document.**

**This document will save you and us a lot of time by setting up your development environment correctly.**
It solves all known pitfalls that can disrupt your development.
It also describes all guidelines, tips and tricks.
If you want your pull requests (or patches) to be merged into master, please respect those guidelines.

If you are reading this document with a normal text editor, please take a look
at the more readable [formatted version](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md).

If you discover pitfalls, tips and tricks not described in this document,
please update it using the [markdown syntax](http://daringfireball.net/projects/markdown/syntax).

Table of content
----------------

* **[Source control with Git](#source-control-with-git)**

* **[Building with Maven](#building-with-maven)**

* **[Developing with Eclipse](#developing-with-eclipse)**

* **[Developing with IntelliJ](#developing-with-intellij)**

* **[Team communication](#team-communication)**

* **[Writing documentation](#writing-documentation)**

* **[FAQ](#faq)**


Quick start
===========

If you don't want to contribute to this project and you know git and maven, these build instructions should suffice:

* To build 1 repository, for example `guvnor`:

    ```shell
    $ git clone git@github.com:kiegroup/guvnor.git
    $ cd guvnor
    $ mvn clean install -DskipTests
    ```
* To build all repositories:

    ```shell
    $ git clone git@github.com:kiegroup/droolsjbpm-build-bootstrap.git
    $ droolsjbpm-build-bootstrap/script/git-clone-others.sh
    $ droolsjbpm-build-bootstrap/script/mvn-all.sh clean install -DskipTests
    ```

**If you want to contribute to this project, read the rest of this file!**

Source control with Git
=======================

Installing and configuring git
------------------------------

* Install git in your OS:

    * Linux: Install the package git

        ```shell
        $ sudo apt-get install git
        ```

        Tip: Also install *gitk* to visualize your git log:

        ```shell
        $ sudo apt-get install gitk
        ```

    * Windows, Mac OSX: Download from [the git website](http://git-scm.com).

        Tip for Mac OSX: Also install [*gitx*](http://gitx.frim.nl/) to visualize your git log.

    * More info in [GitHub's git installation instructions](http://help.github.com/git-installation-redirect).

* Check if git is installed correctly.

    ```shell
    $ git --version
    git version 1.7.1
    ```

* Configure git correctly:

    ```shell
    $ git config --global user.name "My Full Name"
    $ git config --global user.email myAccount@gmail.com
    $ git config --global -l
    user.name=Geoffrey De Smet
    user.email=gds...@gmail.com
    ```

    * Warning: the field `user.name` is your full name, *not your username*.

    * Note: the field `user.email` should match an email address of your github account.

    * More info on [GitHub](http://help.github.com/git-email-settings/).

* Get a github account

    * And add your public key on github: [Follow these instructions](http://github.com/guides/providing-your-ssh-key).

* To learn more about git, read the free book [Pro Git](http://progit.org/book/).

Getting the sources locally
---------------------------

Because you'll probably want to change our code, it's recommended to fork our code before cloning it,
so it's easier to share your changes with us later.
For more info on forking, read [GitHub's help on forking](http://help.github.com/fork-a-repo/).

* First fork the repository you want to work on, for example `guvnor`:

    * Surf to [the blessed repositories on github](https://github.com/kiegroup) and log in.

        * Note: **Every git repository can be build alone.**
        You only need to fork/clone the repositories you're interested in (`guvnor` in this case).

    * Surf to [the specific repository (guvnor)](https://github.com/kiegroup/guvnor)

    * Click the top right button *Fork*

    * Note: by forking the repository, you can commit and push your changes without our consent
    and we can easily review and then merge your changes into the blessed repository.

* **Clone your fork locally:**

    ```shell
    # First make a directory to hold all the kiegroup projects
    $ mkdir kiegroup
    $ cd kiegroup

    # Then clone the repository you want to clone.
    $ git clone git@github.com:MY_GITHUB_USERNAME/guvnor.git
    $ cd guvnor
    $ ls
    ```

    * Warning: Always clone with the *SSH URL*, never clone with the *HTTPS URL* because the latter is unreliable.

    * Note: it's highly recommended to name the cloned directory the same as the repository (which is the default), so the helper scripts work.

    * By default you will be looking at the sources of the master branch, which can be very unstable.

        * Use git checkout to switch to a more stable branch or tag:

            ```shell
            $ git checkout 5.2.0.Final
            ```

* Add the blessed repository as upstream (if you've directly cloned the blessed repository, don't do this):

    ```shell
    $ git remote add upstream git@github.com:kiegroup/guvnor.git
    $ git fetch upstream
    ```

Working with git
----------------

* First make a topic branch:

    ```shell
    $ git checkout master
    $ git checkout -b myFirstTopic
    ```

    * Don't litter your local `master` branch: keep it equal to `remotes/upstream/master`

    * 1 branch can have only 1 pull request, because the pull requests evolves as you add more commits on that branch.

* Make changes, run, test and document them, then commit them:

    ```shell
    $ git commit -m "Fix typo in documentation"
    ```

* Push those commits on your topic branch to your fork

    ```shell
    $ git push origin myFirstTopic
    ```

* Get the latest changes from the blessed repository

    * Set your master equal to the blessed master:

        ```shell
        $ git fetch upstream
        $ git checkout master
        # Warning: this deletes all changes/commits on your local master branch, but you shouldn't have any!
        $ git reset --hard upstream/master
        ```

    * Start a new topic branch and set the code the same as the blessed master:

        ```shell
        $ git fetch upstream && git checkout -b mySecondTopic && git reset --hard upstream/master
        ```

    * If you have a long-running topic branch, merge master into it:

        ```shell
        $ git fetch upstream
        $ git merge upstream/master
        ```

        * If there are merge conflicts:

            ```shell
            $ git mergetool
            $ git commit
            ```

            or

            ```shell
            $ git status
            $ gedit conflicted-file.txt
            $ git add conflicted-file.txt
            $ git commit
            ```

            Many people get confused when a merge conflict occurs, because you're *in limbo*.
            Just fix the merge conflicts and commit (even if the git seems to contain many files),
            only then is the merge over. Then run `git log` to see what happened.
            The many files in the merge conflict resolving commit are a side effect of non-linear history.

* You may delete your topic branch after your pull request is closed (first one deletes remotely, second one locally):

    ```shell
    $ git push origin :myTopicBranch
    $ git branch -D myTopicBranch
    ```

* Tips and tricks

    * To see the details of your local, unpushed commits:

        ```shell
        $ git diff origin...HEAD
        ```

    * To run a git command (except clone) over all repositories (only works if you cloned all repositories):

        ```shell
        $ cd ~/projects/kiegroup
        $ droolsjbpm-build-bootstrap/script/git-all.sh push
        ```

        * Note: the `git-all.sh` script is working directory independent.

        * Linux tip: Create a symbolic link to the `git-all.sh` script and place it in your `PATH` by linking it in `~/bin`:

            ```shell
            $ ln -s ~/projects/kiegroup/droolsjbpm-build-bootstrap/script/git-all.sh ~/bin/kiegroup-git
            ```

            For command line completion, add the following line in `~/.bashrc`:

            ```shell
            $ complete -o bashdefault -o default -o nospace -F _git kiegroup-git
            ```

Share your changes with a pull request
--------------------------------------

A pull request is like a patch file, but easier to apply, more powerful and you'll be credited as the author.

* Creating a pull request

    * Push all your commits to a topic branch on your fork on github (if you haven't already).

        * You can only have 1 pull request per branch, so it's advisable to use topic branches to avoid mixing your changes.

    * Surf to that topic branch on your fork on github.

    * Click the button *Pull Request* on the top of the page.

* Accepting a pull request

    * Surf to the pull request page on github.

    * Review the changes

    * Click the button *Merge help* on the bottom of the page and follow the instructions of github to apply those changes on the blessed master.

        * Or use the button *Merge* if there are no merge conflicts.

If the change being proposed is affecting more than a single repository, it will require creating a pull request for each of the repositories being affected; in this case, it is required for the *topic branch* to share the same name across all pull requests, in order for the CI build tool to include the necessary dependencies while performing the build with the proposed change. It is also highly recommended to use the github *Autolinked references* in the pull request comments, in order to make these depedencies explicit and emphasized during code reviews.

Building with Maven
===================

All projects use Maven 3 to build all their modules.

Installing Maven
----------------

* Get Maven

    * [Download Maven](http://maven.apache.org/) and follow the installation instructions.

* Linux

    * Note: the `apt-get` version of maven is probably not up-to-date enough.

    * Linux trick to easily upgrade to future versions later:

        * Unzip maven to `~/opt/build`

        * Create a version-independent link:

            ```shell
            $ cd ~/opt/build/
            $ ln -s apache-maven-3.3.9 apache-maven
            ```

            Next time you only have to remove the link and recreate the link to the new version.

        * Add this to your `~/.bashrc` file:

            ```shell
            export M3_HOME="~/opt/build/apache-maven"
            export PATH="$M3_HOME/bin:$PATH"
            ```

    * Give more memory to maven, so it can build the big projects too:

        * Add this to your `~/.bashrc` file:

            ```shell
            export MAVEN_OPTS="-Xms256m -Xmx1024m"
            ```

* Windows:

    * Give more memory to maven, so it can build the big projects too:

        * Open menu *Configuration screen*, menu item *System*, tab *Advanced*, button *environment variables*:

            ```shell
            set MAVEN_OPTS="-Xms256m -Xmx1024m"
            ```

* Check if maven is installed correctly.

    ```shell
    $ mvn --version
    Apache Maven 3.3.9 (...)
    Java version: 1.8.0_112
    ```

    Note: the enforcer plugin enforces a minimum maven and java version.

Running the build
-----------------

* Go into a project's base directory, for example `guvnor`:

    ```shell
    $ cd ~/projects/kiegroup
    $ ls
    drools  droolsjbpm-build-bootstrap droolsjbpm-integration  droolsjbpm-knowledge  droolsjbpm-tools  optaplanner  guvnor
    $ cd guvnor
    $ ls
    ...  guvnor-repository  guvnor-webapp-drools  pom.xml
    ```

    Notice you see a `pom.xml` file there. Those `pom.xml` files are the heart of Maven.

* **Run the build**:

    ```shell
    $ mvn clean install -DskipTests
    ```

    The first build will take a long time, because a lot of dependencies will be downloaded (and cached locally).

    It might even fail, if certain servers are offline or experience hiccups.
    In that case, you 'll see an IO error, so just run the build again.

    If you consistently get `Could not transfer artifact ... Connection timed out`
    and you are behind a non-transparent proxy server,
    [configure your proxy server in Maven](http://maven.apache.org/settings.html#Proxies).

    After the first successful build, any next build should be fast and stable.

* Try running a different profile by using the option `-D<profileActivationProperty>`:

    ```shell
    $ mvn clean install -DskipTests -Dfull
    ```

    There are 3 profile activation properties:

    * *none*: Fast, for during development

    * `full`: Slow, but builds everything (including documentation). Used by Jenkins and during releases.

    * `productized`: activates branding changes for productized version

* To run a maven build over all repositories (only works if you cloned all repositories):

    ```shell
    $ cd ~/projects/kiegroup
    $ droolsjbpm-build-bootstrap/script/mvn-all.sh -DskipTests clean install
    ```

    * Note: the `mvn-all.sh` script is working directory independent.

* You can use `mvn-all.sh` to compile a specific repository and all repositories that your target repository depends on.
  This is done using the `--target-repo` option which will invoke `repo-dep-tree.pl` script to discover cross-repository
  project dependencies. Use `--repo-list` to specify custom list of repositories. These options work for `git-all.sh`
  too.

* Warning: The first `mvn` build of a day will download the latest SNAPSHOT dependencies of other kiegroup projects,
unless you build all those kiegroup projects from source.
Those SNAPSHOTS were build and deployed last night by Jenkins jobs.

    * If you've pulled all changes (or cloned a repository) today, this is a good thing:
    it saves you from having to download and build all those other latest kiegroup projects from source.

    * If you haven't pulled all changes today, this is probably a bad thing:
    you 're probably not ready to deal with those new snapshots.

        In that case, add `-nsu` (= `--no-snapshot-updates`) to the `mvn` command to avoid downloading those snapshots:

        ```shell
        $ mvn clean install -DskipTests -nsu
        ```

        Note that using `-nsu` will also make the build faster.

Running tests
-------------

Guvnor uses Arquillian to run tests in a J2EE container and hence tests need to be ran differently to others.

* Guvnor

    ```shell
    $ cd ~/projects/kiegroup/guvnor/guvnor-webapp-drools
    $ mvn integration-test [-Dtest=ATestClassName]
    ```

* All other modules

    ```shell
    $ cd ~/projects/kiegroup/drools
    $ mvn test [-Dtest=ATestClassName]
    ```

Running code-coverage checks
----------------------------

JaCoCo plugin allows to measure code-coverage for any child of droolsjbpm-build-bootstrap. 
The check binds to the verify phase and for the plugin to run, the code-coverage profile has to be enabled.

* From the module/project folder run command:
   
    ```shell
    $ mvn clean verify -Pcode-coverage
    ```

* The coverage report is then generated in ./target/site/jacoco/index.html

Running Pitest mutation coverage analysis
-----------------------------------------

Mutation coverage is used to measure how good the tests are at making assertions about the tested code.
It is a good idea to check the mutation coverage of tests added together with any changes, be it a newly developed
feature or a bug fix. Code coverage is analyzed for free as part of the mutation analysis.

To analyze the complete module:

```shell
$ mvn verify -Dmutation-coverage
```

To limit analyzed classes to a sub-package:

```shell
$ mvn verify -Dmutation-coverage -DtargetClasses=org.drools*
```

The HTML report will be stored in `local/pit-reports/` directory.
Currently, it is not possible to get a report aggregated over multiple modules.
Learn more about using [Pitest](http://pitest.org/quickstart/maven/).

Configuring Maven
-----------------

To deploy snapshots and releases to nexus, you need to add this to the file `~/.m2/settings.xml`:

```xml
<settings>
  ...
  <servers>
    <server>
      <id>jboss-snapshots-repository</id>
      <username>jboss.org_username</username>
      <password>jboss.org_password</password>
    </server>
    <server>
      <id>jboss-releases-repository</id>
      <username>jboss.org_username</username>
      <password>jboss.org_password</password>
    </server>
    </servers>
    ...
</settings>
```

Furthermore, you'll need nexus rights to be able to do this.

More info in [the JBoss.org guide to get started with Maven](http://community.jboss.org/wiki/MavenGettingStarted-Developers).

Requirements for dependencies
-----------------------------

Any dependency used in any KIE project must fulfill these hard requirements:

* The dependency must have **an ASL compatible license**.

    * Good: BSD, MIT, ASL

    * Avoid: EPL, LGPL

        * Especially LGPL is a last resort and should be abstracted away or contained behind an SPI.

        * Test scope dependencies pose no problem if they are EPL or LPGL.

    * Forbidden: no license, GPL, AGPL, proprietary license, field of use restrictions ("this software shall be used for good, not evil"), ...

        * Even test scope dependencies cannot use these licenses.
        
    * To check the ALS compatibility license please visit these links:[Similarity in terms to the Apache License 2.0](http://www.apache.org/legal/resolved.html#category-a)&nbsp; 
    [How should so-called "Weak Copyleft" Licenses be handled](http://www.apache.org/legal/resolved.html#category-b)

* The dependency shall be **available in [Maven Central](http://search.maven.org/) or [JBoss Nexus](https://repository.jboss.org/nexus)**.

    * Any version used must be in the repository Maven Central and/or JBoss (Nexus) Public repository group

        * Never add a `<repository>` element in a `pom.xml`.

        * Note: JBoss Public repository group mirrors java.net, codehaus.org, ... Most jars are available there.

    * Why?

        * Build reproducibility. Any repository server we use, must still run 7 years from now.

        * Build speed. More repositories slow down the build.

        * Build reliability. A repository server that is temporary down can break builds.

    * Workaround to still use a great looking jar as a dependency:

        * Get that dependency into JBoss Nexus as a 3rd party library.

* The dependency must be able to run on any **JVM 1.8 and higher**.

    * It must be compiled for Java target 1.8 or lower (even if it's compiled with JDK 7 or JDK 8).

    * It must not use any JDK APIs that were not yet available in Java 1.8.

* **Do not release the dependency yourself** (by building it from source).

    * Why? Because it's not an official release, by the official release guys.

        * A release must be 100% reproducible.

        * A release must be reliable (sometimes the release person does specific things you might not reproduce).

* **No security issues** (CVE's) reported on that version of the dependency

    * We don't expect you to check this manually:
    The victims enforcer plugin will automatically fail the build if a known bad dependency is used.

* **The sources are publicly available**

    * We may need to rebuild the dependency from sources ourselves in future. This may be in the rare case when
      the dependency is no longer maintained, but we need to fix a specific CVE there. The other reason is that
      productisation needs to be able to easily rebuild the dependency internally.

    * Make sure the dependency's pom.xml contains link to the source repository (`scm` tag).

* The dependency needs to use **reasonable build system**

    * Since we may need to rebuild the dependency from sources, we also need to make sure it is easily buildable.
      Maven or Gradle are acceptable as build systems.

Any dependency used in any KIE project should fulfill these soft requirements:

* Use dependencies that are **acceptable for the [jboss-integration-platform-bom](https://github.com/jboss-integration/jboss-integration-platform-bom)**.

    * Do not override versions in `kie-parent`'s `pom.xml` unless an exception is granted

        * If a newer version of the ip-bom already uses the new version, it's of course fine to do a temporarily overwrite in `kie-parent`'s `pom.xml`.

* **Prefer dependencies with the groupId `org.jboss.spec`** over those with the groupId `javax.*`.

    * Dependencies with the groupId `javax.*` are unreliable and are missing metadata. No one owns/maintains them consistently.

    * Dependencies with the groupId `org.jboss.spec` are checked and fixed by JBoss.

* Only use dependencies with **an active community**.

    * Check for activity in the last year through [Open Hub](https://www.openhub.net).

* Less is more: **less dependencies is better**. Bloat is bad.

    * Try to use existing dependencies if the functionality is available in those dependencies

        * For example: use `poi` instead of `jexcelapi` if `poi` is already a KIE dependency

* **Do not use fat jars, nor shading jars.**

    * A fat jar is a jar that includes another jar's content. For example: `weld-se.jar` which includes `org/slf4j/Logger.class`

    * A shaded jar is a fat jar that shades that other jar's content. For example: `weld-se.jar` which includes `org/weld/org/slf4j/Logger.class`

    * Both are bad because they cause dependency tree trouble. Use the non-fat jar instead, for example: `weld-se-core.jar`

There are currently a few dependencies which violate some of these rules.
If you want to add a dependency that violates any of the rules above, get approval from the project leads.

Regenerating Protobuf Files
---------------------------

Some modules include Protobuf files (like drools-core and jbpm-flow). Every time a .proto file is changed, the java files have to be regenerated. In order to do that, on the module that contains the files to be regenerated, execute the following command:

```shell
$ mvn exec:exec -Dproto
```

After testing the regenerated files, don't forget to commit them.

**IMPORTANT:** before trying to regenerate the protobuf java files, you must install the protobuf compiler (protoc) in your machine. Please follow the instructions. You can download it from here: [https://developers.google.com/protocol-buffers/docs/downloads](https://developers.google.com/protocol-buffers/docs/downloads).

For Linux/Mac, you have to compile it yourself as there are no binaries available. Follow the instructions in the README file for that.

Developing with Eclipse
=======================

Before running Eclipse
----------------------

* Do not use an Eclipse version older than `3.6 (helios)`.

* Avoid an `OutOfMemoryException` and a `StackOverflowError` when building.

    Open `$ECLIPSE_HOME/eclipse.ini` and add/change this: on openFile -vmargs:

    ```shell
    openFile
    -vmargs
    ...
    -XX:MaxPermSize=512m
    -Xms512m
    -Xmx1024m
    -Xss1024k
    ```

Configuring the project with the m2eclipse plugin
-------------------------------------------------

The m2eclipse plugin is a plugin in Eclipse for Maven.
This is the new way (and compatible with tycho).

* Open Eclipse

* Follow [the installation instructions of m2eclipse](http://m2eclipse.sonatype.org/).

    * Follow the link *Installing m2eclipse* at the bottom.

* Click menu *File*, menu item *Import*, tree item *Maven*, tree item *Existing Maven Projects*.

* Click button *Browse*, select a repository directory. For example `~/projects/kiegroup/guvnor`.

* Unfold *Advanced*, textfield *Profiles*: `notSoaProfile,fullProfile`.

For more information, see [the m2eclipse book](http://www.sonatype.com/books/m2eclipse-book/reference/)

Configuring the project with the deprecated maven-eclipse-plugin
----------------------------------------------------------------

The maven-eclipse-plugin plugin is a plugin in Maven for Eclipse.
This is the old way (of which the development has stopped).

Run this command to generate `.project` and `.classpath` files:

```shell
$ mvn eclipse:eclipse
```

* Open Eclipse

* Menu item *Import existing projects*, navigate to the project base directory, select all the projects (= modules) it lists.

Important note: `mvn eclipse:eclipse` does not work for our eclipse plugins because it is not compatible with tycho
(and never will be).

Configuring Eclipse
-------------------

* Force language level 8, to fail-fast when (accidentally) using features available only in newer Java versions.

    * Open menu *Window*, menu item *Preferences*

    * Click tree item *Java*, tree item *Compiler*, section *JDK Compliance*, combobox *Compiler compliance level* should be `1.8`.

* Remove the test resources Java Build Path exclusion filter to ensure JUnit tests ran inside Eclipse can find the necessary resources.

    * Right-click the project

    * Select menu item *Build Path*, sub-menu item *Configure build path...*

    * On the *Sources* tab, scroll down to `<project>\src\test\resources` and expand tree

    * Select `Excluded` and click *Remove*. The filter should show as `(none)`

* Set the correct file encoding (UTF-8 except for properties files) and end-of-line characters (unix):

    * Open menu *Window*, menu item *Preferences*.

    * Click tree item *General*, tree item *Workspace*

        * Label *Text file encoding*, radiobutton *Other*, combobox `UTF-8`.

        * Label *New text file delimiter*, radiobutton *Other*, combobox `Unix`.

    * Click tree item *XML*, tree item *XML Files*.

        * Combobox *Encoding*: `ISO 10646/Unicode(UTF-8)`.

    * Click tree item *CSS*, tree item *CSS Files*.

        * Combobox *Encoding*: `ISO 10646/Unicode(UTF-8)`.

    * Open tree item *HTML*, tree item *HTML Files*.

        * Combobox *Encoding*: `ISO 10646/Unicode(UTF-8)`.

    * Note: normal i18n properties files must be in `ISO-8859-1` as specified by the java `ResourceBundle` contract.

        * Note on note: GWT i18n properties files override that and must be in `UTF-8` as specified by the GWT contract.

* Recommended: import our code style

    * Set up formatter for edited code only: Open menu *Window*, menu item *Preferences*, click tree item *Java*, tree item *Editor*, and click *Save actions*. Enable *Perform the selected actions on save*: select *Format source code* (*Format edited lines*) and *Organize imports*
    
    * **IMPORTANT** Eclipse uses three seperate formatters, and you need to setup each one seperately (click Import...,
    select the file, and click apply for each one):
    
        - Clean Up: Uses "eclipse-code-style-clean-up_droolsjbpm-java-conventions.xml"
        
        - Formatter: Uses "eclipse-code-style-formatter_droolsjbpm-java-conventions.xml"
        
        - Organize Imports: Uses "eclipse-code-style-organize-imports_droolsjbpm-java-conventions.importorder"

    * If you don't do this, you need to set the number of spaces correctly manually.

    * Use the one from `droolsjbpm-build-bootstrap/ide-configuration`

* Set the correct number of spaces when pressing tab:

    * Warning: If you imported the `eclipse-formatter.xml` file, you don't need to set it for Java, but you do need to set it for XML anyway!

    * Open menu *Window*, menu item *Preferences*.

        * If you have project specific settings enabled instead, right click on the project and click the menu item *Properties*.

    * Click tree item *Java*, tree item *Code Style*, tree item *Formatter*.

        * Click button *Edit* of the active profile, tab *Indentation*

        * Combobox *Tab policy*: `spaces only`

        * Textfield *Indentation size*: `4`

        * Textfield *Tab size*: `4`

        * Note: If it is a build-in profile, you 'll need to change its name with the textfield on top.

    * Click tree item *XML*, tree item *XML Files*, tree item *Editor*.

        * Radiobutton *Indent using space*: `on`

        * Textfield *Indentation size*: `2`

    * Click tree item *General*, tree item *Editors*, tree item *Text Editors*.

        * Checkbox *Insert spaces for tabs*: `on`

        * Textfield *Displayed tab width*: `4`

    * Click tree item *CSS Files*, tree item *Editor*.

        * Radiobutton *Indent using space*: `on`

        * Textfield *Indentation size*: `4`

* Set the correct file headers (do not include @author or a meaningless javadoc):

    * Open menu *Window*, menu item *Preferences*.

    * Click tree item *Java*, tree item *Code Style*, tree item *Code Templates*.

    * Click tree *Configure generated code and comments*, tree item *Comments*, tree item *types*.

    * Remove the line *@author Your Name*.

        * We do not accept `@author` lines in source files, see FAQ below.

    * Remove the entire javadoc as automatically templated data is meaningless.

* Set the correct license header

    Eclipse JEE Helios currently has no build-in support of license headers, but you can configure it for new files.

    * Open menu *Window*, menu item *Preferences*.

        * If you have project specific settings enabled instead, right click on the project and click the menu item *Properties*.

    * Click tree item *Java*, tree item *Code Style*, tree item *Copy templates*.

    * Click tree item *Comments*, tree item *Files*.

    * Replace the text area with the java multi-line comment version of
    ` droolsjbpm-build-bootstrap/ide-configuration/LICENSE-ASL-2.0-HEADER.txt`:

        ```
        /*
         * Copyright ${year} Red Hat, Inc. and/or its affiliates.
         * 
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         * 
         *     http://www.apache.org/licenses/LICENSE-2.0
         * 
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */
         ```

    * Note: Do not start or end with a newline character

    * Note: Do not start with `/**`: it is not a valid javadoc.
   

Extra Eclipse plugins
---------------------

* Enable git support

    * Open menu *Help*, menu item *Install new software*.

    * Click combobox *Update site* `Helios`, tree item *Collaboration*, tree item *Eclipse EGit*.

* GWT plugin

    * [Download and install the Eclipse GWT plugin](http://gwt-plugins.github.io/documentation/gwt-eclipse-plugin/Download.html)

        * Note: it is recommended to use the same [GWT SDK version](https://developers.google.com/eclipse/docs/using_sdks#selecting-sdks-for-a-project) like the GWT version it is used in [droolsjbpm-build-bootstrap/pom.xml](pom.xml) `version.com.google.gwt` property value.

    * In *Package Explorer*, right click on the project `guvnor-webapp`, menu item *Properties*.

        * Enable the GWT aspect:

            * Click tree item *Google*, tree item *Web Toolkit Settings...*

            * Checkbox *Use google Web Tookit*: `on`

            * List *Entry Point Modules* should contain `Guvnor - org.drools.guvnor` (and optionally `FastCompiledGuvnor` too).

        * The gwt-dev jar needs to be first on the compilation classpath (the `java.lang.NoSuchFieldError: warningThreshold` problem)

            * Click tree item *Java Build Path*

            * Tab *Libraries*, button *Add Library...*, list item *Google Web Toolkit*, button *Next*, button *Finish*

            * Tab *Order and Export*, select `GWT SDK ...`, button *Top*

    * Verify that you have a web browser configured in Eclipse:

        * Open menu *Window*, menu item *Preferences*.

        * Click tree *General*, tree item *Web Browser*, radiobutton *Use external web browser*.

        * Click button *New...*, textfield *Name* `firefox`, textfield *Location* `/usr/bin/firefox`, textfield *Parameters* `%URL%`, button *OK*.

        * Check the checkbox next to `firefox`.

    * Run GWT in hosted mode

        * Open project context menu *Properties*, Google->Web application->

            * This project has a WAR directory, tick

            * WAR directory, `target/guvnor-webapp-drools-5.4.0-SNAPSHOT` (this will differ for different releases)

            * You will need to have completed a maven install, as explained above to generate the `target/guvnor-webapp-drools-5.4.0-SNAPSHOT` directory

            * Launch and deploy from this directory, tick

        * Open menu *Run*, menu item *Run configurations...*

        * In the list, select *Web Application*, button *new launch configuration*

        * Tab *Main*, Project: `guvnor-webapp-drools`

        * Tab *Main*, Ensure `Main class` is: `com.google.gwt.dev.DevMode`

        * Tab *GWT*, list *Available Modules*: `Guvnor - org.drools.guvnor`

        * Tab *Arguments*, Ensure `Program Arguments` are :

            ```
            -war <path-to-war-folder> -remoteUI "${gwt_remote_ui_server_port}:${unique_id}" -startupUrl index.jsp -logLevel INFO -codeServerPort 9997 -port 8888 org.drools.guvnor.FastCompiledGuvnor org.drools.guvnor.Guvnor
            ```

            For example:

            ```
            -war /home/manstis/workspaces/git/kiegroup/guvnor/guvnor-webapp-drools/target/guvnor-webapp-drools-5.4.0-SNAPSHOT -remoteUI "${gwt_remote_ui_server_port}:${unique_id}" -startupUrl index.jsp -logLevel INFO -codeServerPort 9997 -port 8888 org.drools.guvnor.FastCompiledGuvnor org.drools.guvnor.Guvnor
            ```

        * Tab *Arguments*, it is recommended to set `VM Arguments` to: `-XX:MaxPermSize=512m -Xms512m -Xmx2048m`. You might be able to try smaller values, but these are known to work.

        * Button *Run*.

    * In your workspace, in the tab *Development Mode*, double click on the `Guvnor` URL.

    * If you encounter a java.lang.NoSuchFieldError: warningThreshold error you need to follow the steps [here](http://code.google.com/p/google-web-toolkit/issues/detail?id=4479), i.e.

        * Add GWT-SDK to your classpath (even though it is a Maven dependency)

        * On your Java Build Path, Order and Export tab, move GWT-SDK to the top

Eclipse plugin development
--------------------------

* Installing a kiegroup eclipse plugin into a fresh Eclipse from a local update site.

    * Follow the intructions in [the description entity in the org.drools.updatesite pom.xml file](https://github.com/kiegroup/droolsjbpm-tools/blob/master/drools-eclipse/org.drools.updatesite/pom.xml).

Developing with IntelliJ
========================

Before running IntelliJ
-----------------------

* Avoid an `OutOfMemoryException` while editing or building.

    Open `$IDEA_HOME/bin/idea.vmoptions` and change the first 3 values to this:

    ```shell
    -Xms512m
    -Xmx1024m
    -XX:MaxPermSize=512m
    ```

Configuring the project with the maven integration
--------------------------------------------------

IntelliJ has very good build-in support for Maven.

* Open IntelliJ.

* Click menu *File*, menu item *New project*.

    * Click radiobutton *Create project from scratch*, button *Next*

    * Textfield *name*: `kiegroup`

    * Textfield *Project files location*: `~/projects/kiegroup`

    * Checkbox *Create module*: `off`

Note: If you want to configure a main project that includes all projects you must create an empty project and add the
projects as modules.

* Click menu *File*, menu item *New module*

    * Radiobutton *Import from external model*, button *Next*, button *Next*

    * Textfield *Root directory*: `~/projects/kiegroup/guvnor`

        * That is the directory that contains the multiproject `pom.xml` file from a project base directory.

    * Button *Next*, check in the *Selected profiles* `notSoaProfile` and `fullProfile`, button *Next*, button *Finish*.

    * Go grab a coffee while it's indexing.

    * Repeat if you want to work on more than 1 kiegroup project.

Note: Don't use the `maven-idea-plugin` on the command line with `mvn`: it's dead.

Configuring IntelliJ
--------------------

* Force language level 8, to fail-fast when (accidentally) using features available only in newer Java versions.

    * Open menu *File*, menu item *Project Structure*

    * Click list item *Modules*, for each module, tab *Sources*, combobox *Language level* should be automatically set to `8.0 ...`

* Avoid that changes in some resources are ignored in the next run/debug (and you are forced to use mvn)

    * Open menu *File*, menu item *Settings*

    * Click tree item *Compiler*, textfield *Resource patterns*: change to `!?*.java` (remove other content)

* Avoid a `StackOverflowError` when building

    * Open menu *File*, menu item *Settings*

    * Click tree item *Compiler*, tree item *Java Compiler*, textfield *Additional command line parameters*

    * Add `-J-Xss1024k` so it becomes something like `-target 1.8 -J-Xss1024k`

* Include files with non-default extensions in your searches and refactors

    * Open menu *File*, menu item *Settings*

    * Click tree item *File Types*, in the list *Recognized File Types*:

        * Next to list *Recognized File Types*, click on the button *Add...*

            * Textfield *name*: `DRL files`

            * Textfield *Line comment*: `//`

            * Textfield *Block comment start*: `/*`

            * Textfield *Block comment end*: `*/`

            * Check the checkboxes *Support paired braces*, *Support paired brackets* and *Support parens*

            * Add some *keywords*: `rule`, `when`, `then`, `end`, ...

            * Click button *ok*

        * Next to the list *Registered Patterns*, use the button *Add...*:

            * For `DRL files`, add `*.drl`, `*.mvel`, `*.drt`, `*.dslr`

            * For `Text files`, add `*.md`

            * For `Properties files`, add `*.dsl`

            * For `XML Files`, add `*.rf`

* Recommended: import our code style

    * If you don't do this, you need to set the file encoding and number of spaces correctly manually.

    * Use the one from `droolsjbpm-build-bootstrap/ide-configuration`

    * Copy to `~/.IntelliJIdea*/config/codestyles/` (on mac: `~/Library/Preferences/IntelliJIdea*/config/codestyles/`)

    * Restart, open menu *File*, menu item *Settings*

    * Click tree item *Code Style* and select it.

        * Note: IntelliJ IDEA doesn't format your code automatically. You have to press Ctrl+Alt+L keyboard combination to trigger auto formatting when coding is done. Another option is to commit your code directly from IntelliJ IDEA (open menu *VCS*, menu item *Commit Changes...*), and select the option *Reformat Code*, which will reformat all files changed in the commit.

* Set the correct file encoding (UTF-8 except for properties files) and end-of-line characters (unix):

    * Open menu *File*, menu item *Settings*

    * Click tree item *Code Style*, tree item *General*

        * Combobox *Line separator (for new files)*: `Unix`

    * Click tree item *File Encodings*

        * Combobox *IDE Encoding*: `UTF-8`

        * Combobox *Default encoding for properties files*: `ISO-8859-1`

            * Note: normal i18n properties files must be in `ISO-8859-1` as specified by the java `ResourceBundle` contract.

                * Note on note: GWT i18n properties files override that and must be in `UTF-8` as specified by the GWT contract.

* Set the correct number of spaces when pressing tab:

    * Open menu *File*, menu item *Settings*

    * Click tree item *Code Style*, tree item *General*

    * Click tab *Java*

        * Checkbox *Use tab character*: `off`

        * Textfield *Tab size*: `4`

        * Textfield *Indent*: `4`

        * Textfield *Continuation indent*: `8`

    * Open tab *XML*

        * Checkbox *Use tab character*: `off`

        * Textfield *Tab size*: `2`

        * Textfield *Indent*: `2`

        * Textfield *Continuation indent*: `4`

* Set the correct file headers (do not include @author or a meaningless javadoc):

    * Open menu *File*, menu item *Settings*

    * Click tree item *File templates*, tab *Includes*, list item `File Header`

    * Remove the line *@author Your Name*.

        * We do not accept `@author` lines in source files, see FAQ below.

    * Remove the entire javadoc as automatically templated data is meaningless.

* Set the correct license header

    * Open menu *File*, menu item *Settings*

    * Click tree item *Copyright*, tree item *Copyright profiles*

        * Click button *+* to add a *Copyright profile*

        * Textfield *name*: `Red Hat, Inc. and/or its affiliates`

        * Textarea with content:

            ```
            Copyright $today.year Red Hat, Inc. and/or its affiliates.

            Licensed under the Apache License, Version 2.0 (the "License");
            you may not use this file except in compliance with the License.
            You may obtain a copy of the License at

                http://www.apache.org/licenses/LICENSE-2.0

            Unless required by applicable law or agreed to in writing, software
            distributed under the License is distributed on an "AS IS" BASIS,
            WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
            See the License for the specific language governing permissions and
            limitations under the License.
            ```

        * Note: Do not start or end with a newline character

        * Note: Do not start with `/**`: it is not a valid javadoc.

    * Click tree item *Copyright*

        * Combobox *Default project copyright*: `Red Hat, Inc. and/or its affiliates`

Extra IntelliJ plugins
----------------------

* Enable git support

    * Open menu *File*, menu item *Other Settings*, menu item *Configure plugins*.

    * Check *Git*.

* GWT plugin (to run in GWT hosted mode)

    * Open menu *File*, menu item *Project structure*

        * For the module `guvnor-webapp-drools`, add the new aspect *GWT* if you haven't already.

            * Textfield *Compiler maximum heap size (Mb)*: `512`

    * Open menu *Run*, menu item *Edit configurations*

        * Add new *GWT configuration*

            * Combobox *Module*: `guvnor-webapp-drools`

            * Combobox *GWT Module to load*: `org.drools.guvnor.FastCompiledGuvnor`

            * Textfield *VM options*: `-Xmx1024m -XX:MaxPermSize=256m`

            * Textfield *Start page*: `org.drools.guvnor.Guvnor/Guvnor.html` (Second entry, not the first)

        * Run that configuration.

* Tomcat exploded war deployment

    * Open menu *File*, menu item *Project structure*

        * Select tree item *Artifacts*, list item `guvnor-webapp-drools:war exploded`

            * Checkbox *Build on make*: `on`

    * Open menu *Run*, menu item *Edit configurations*

        * Add new *Tomcat server*, *local*

            * Tab *deployment*, add *Artifact* `guvnor-webapp-drools:war exploded`.

            * Panel *Before launch*, checkbox *Build 'guvnor-webapp-drools:war exploded' artifact*: `on`

        * Run that configuration.

Linux inotify
-------------

We have encountered some issues with different Linux distribution's *inotify* when running any of the Workbench's in Super Dev Mode from within InteliJ.

Error message `User limit of inotify instances reached or too many open files` has been observed with both Ubuntu and Fedora. Should you encounter this issue you will need to reconfigure your *inotify* settings.

Add the following to `/etc/sysctl.conf` and then run `sudo sysctl -p`:

`fs.inotify.max_user_watches = 524288`

`fs.inotify.max_user_instances = 524288`

You may also need to add the following lines (replacing `user-id` with your User Id) to `/etc/security/limits.conf`:

`user-id soft nofile 4096`

`user-id hard nofile 10240`


Team communication
==================

To develop a great project as a team, we need to communicate efficiently as a team.

Team workflows
--------------

* Fixing a community issue in JIRA:

    * Find/create the issue in JIRA ([Drools](https://issues.jboss.org/browse/DROOLS),
    [OptaPlanner](https://issues.jboss.org/browse/PLANNER), [jBPM](https://issues.jboss.org/browse/JBPM),
    [Guvnor](https://issues.jboss.org/browse/GUVNOR))

    * Fix the issue and push those changes to the appropriate branch(es) on github.

        * If you don't have push permissions, create a pull request (PR). See [Using pull requests](https://help.github.com/articles/using-pull-requests/) for more info.

    * Change the *Status* to `Resolved`.
        * When you file a PR, do not mark the issue as `Resolved` until the PR gets merged. Link the PR to the JIRA issue and wait till someone reviews the changes.

        * Once the reporter verifies the fix, he changes *Status* to `Closed`. Or we bulk change it to `Closed` after a year.

* (Red Hat developers only) Fixing BRMS issues in Bugzilla:

    * Find an issue in Bugzilla. Change *Status* to `ASSIGNED` and *Assigned To* to yourself.

    * Fix the issue and push those changes to the appropriate branch(es) on github.

        * This will likely require back porting or forward porting, because the issue must be fixed on master too.

    * Change the *Status* to `MODIFIED`.

        * Once the new product version is build, they change *Status* to `ON_QA`.

        * Once QA verifies the fix, they change *Status* to `VERIFIED`.

Knowing what's going on
-----------------------

* **Subscribe to the [Drools Development](https://groups.google.com/forum/#!forum/drools-development) Google Group and check it daily.**

    * Start a new topic for every important organizational or structural decision.

    * If you (accidentally) push a change that can severely hinder or disrupt other developers (such as a compilation failure), notify the Development group.

* Subscribe to the RSS feeds.

    * **It's recommend to subscribe at least to the RSS feeds of the project/repositories you're working on.**

    * Prefer an RSS reader which shows which RSS articles you've already read, such as:

        * Thunderbird

            * Open menu *File*, menu item *Subscribe*.

            * Tip: create a new, separate directory for each feed: some feeds (such as about the project you are working on) are more important to you than others.

        * [Google Reader](http://www.google.com/reader)

    * Subscribe to jira issue changes:

        * [DROOLS](https://issues.jboss.org/plugins/servlet/streams?key=DROOLS&os_authType=basic)

        * [PLANNER](https://issues.jboss.org/plugins/servlet/streams?key=PLANNER&os_authType=basic)

        * [JBPM](https://issues.jboss.org/plugins/servlet/streams?key=JBPM&os_authType=basic)

        * [GUVNOR](https://issues.jboss.org/plugins/servlet/streams?key=GUVNOR&os_authType=basic)

    * Subscribe to github repository commits:

        * [droolsjbpm-build-bootstrap](https://github.com/kiegroup/droolsjbpm-build-bootstrap/commits/master.atom)

        * [droolsjbpm-knowledge](https://github.com/kiegroup/droolsjbpm-knowledge/commits/master.atom)

        * [drools](https://github.com/kiegroup/drools/commits/master.atom)

        * [optaplanner](https://github.com/kiegroup/optaplanner/commits/master.atom)

        * [jbpm](https://github.com/kiegroup/jbpm/commits/master.atom)

        * [droolsjbpm-integration](https://github.com/kiegroup/droolsjbpm-integration/commits/master.atom)

        * [guvnor](https://github.com/kiegroup/guvnor/commits/master.atom)

        * [droolsjbpm-tools](https://github.com/kiegroup/droolsjbpm-tools/commits/master.atom)

        * [droolsjbpm-build-bootstrap](https://github.com/kiegroup/droolsjbpm-build-bootstrap/commits/master.atom)

    * Subscribe to [Jenkins](https://hudson.jboss.org/hudson/view/Drools%20jBPM/)

        * with [the Firefox plugin](https://addons.mozilla.org/en-us/firefox/addon/jenkins-build-monitor/) to easily see in your status bar which builds are failing (recommended):

            * After installation, right click on the Jenkins icon in the lower right corner.

            * Click menu item *Preferences*, tab *Feed*, textfield *poll interval* `30` *minutes*.

            * Click menu item *Preferences*, tab *Display*, combox *Display* `latest build` *on status bar*.

            * Go to the Jenkins job of the projects you're working on:

                * [guvnor](https://hudson.jboss.org/hudson/view/Drools%20jBPM/job/guvnor/)

            * Right click in the lower left corner on the *All* feed link, menu item *Add link to Jenkins build monitor*.

        * Otherwise, check [the Jenkins website](https://hudson.jboss.org/hudson/view/Drools%20jBPM/) often.

            * Note: the public Jenkins is a mirror of the VPN internal Red Hat Jenkins and is sometimes stale.

                * If you think this can be the case, check the build times.

* Join us on IRC: chat.freenode.net #drools #jbpm #guvnor #optaplanner

Writing documentation
=====================

* Optionally install a DocBook editor to write documentation more comfortably, such as:

    * [oXygen](http://www.oxygenxml.com/)

        * Open menu *Options*, menu item *Preferences...*.

        * Click tree item *Global*

            * Combobox *Line separator*: `Unix-like`

        * Click tree item *Format*

            * Checkbox *Detect indent on open*: `off`

            * Checkbox *Indent with tabs*: `off`

            * Combobox *Indent size*: `2`

            * Textfield *Line width - Format and Indent*: `120`

    * [XMLmind](http://www.xmlmind.com/xmleditor/)

        * Open menu *Options*, menu item *Preferences...*.

        * Click tree item *Save*

            * Combobox *Encoding*: `UTF-8`

            * Textfield *Identation*: `2`

            * Textfield *Max. line length*: `120`

            * Checkbox *Before saving, make a backup copy of the file*: `off`

                * To avoid committing backups to source control.

                * Source control history is better than backups.

* To generate the html and pdf output run maven with `-Dfull`:

    ```shell
    $ cd kiegroup
    $ cd optaplanner/optaplanner-docs
    $ mvn clean install -Dfull
    ...
    $ firefox target/docbook/publish/en-US/html_single/index.html
    ```

* **[Read and follow the documentation guidelines](documentation-guidelines.txt).**

* The Drools Expert manual uses railroad diagrams.

    These are generated from a BNF file into images files with the application
    [Ebnf2ps, Automatic Railroad Diagram Drawing](http://www.informatik.uni-freiburg.de/~thiemann/haskell/ebnf2ps/)

FAQ
===

* Why do you not accept `@author` lines in your source code?

    * Because the author tags in the java files are a maintenance nightmare

        * A large percentage is wrong, incomplete or inaccurate.

        * Most of the time, it only contains the original author. Many files are completely refactored/expanded by other authors.

        * Git is accurate, that is the canonical source to find the correct author.

    * Because the author tags promote *code ownership*, which is bad in the long run.

        * If people work on a piece they perceive as being owned by someone else, they tend to:

            * only fix what they are assigned to fix, instead of everything that's broken

            * discard responsibility if that code doesn't work properly

            * be scared of stepping on the feet of the owner.

        * For more motivation, see [this video on How to get a healthy open source project?](http://video.google.com/videoplay?docid=-4216011961522818645#)

    * Credit to the authors is given:

        * on [the team page](http://www.jboss.org/drools/team)

             * Please contact Geoffrey (or any of us) if you want to add/change/expand your entry in the team page. Don't be shy!

        * on [the blog](http://blog.athico.com)

            * Write an article about the improvements you did! Contact us if you don't have write authorization on the blog yet.

        * with [Open Hub](https://www.openhub.net/p/jboss-drools/contributors) which also has statistics

        * in [the GitHub web interface](https://github.com/kiegroup).
