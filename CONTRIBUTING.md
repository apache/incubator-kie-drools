<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

# Contribution guide

**Want to contribute? Great!** 
We try to make it easy, and all contributions, even the smaller ones, are more than welcome.
This includes bug reports, fixes, documentation, examples... 
But first, read this page (including the small print at the end).

## Legal

All original contributions to Kogito are licensed under the
[ASL - Apache License](https://www.apache.org/licenses/LICENSE-2.0),
version 2.0 or later, or, if another license is specified as governing the file or directory being
modified, such other license.

## Issues

Kogito uses [Issues](https://github.com/apache/incubator-kie-kogito-runtimes/issues).

If you believe you found a bug, please indicate a way to reproduce it, what you are seeing and what you would expect to see. Don't forget to indicate your Kogito, Java, Maven, Quarkus/Spring, GraalVM version. 

### Checking an issue is fixed in main

Sometimes a bug has been fixed in the `main` branch of Kogito and you want to confirm it is fixed for your own application. Testing the `main` branch is easy and you have two options:

* either use the snapshots we publish daily on https://repository.jboss.org/nexus/content/repositories/snapshots/
* or build Kogito all by yourself

If you are interested in having more details, refer to the [Build section](#build) and the [Usage section](#usage).

## Creating a Pull Request (PR)

To contribute, use GitHub Pull Requests, from your **own** fork. 

- PRs should be always related to an open an [issue](https://github.com/apache/incubator-kie-kogito-runtimes/issues). If there is none, you should [create one](https://github.com/apache/incubator-kie-kogito-runtimes/issues/new) by describing what problem you see that we need to fix.
- Try to fix only one issue per PR.
- Make sure to create a new branch. Usually branches are named after the github issue they are addressing. E.g. for ticket "Fix_#issue An example issue". E.g.:

        git checkout -b Fix_#XYZ
        # or
        git checkout -b Fix_#XYZ-my-fix

- When you submit your PR, make sure to include the ticket ID, and its title; e.g., "Fix_#XYZ An example issue".
- The description of your PR should describe the code you wrote. The issue that is solved should be at least described properly in the corresponding github issue. 
- If your contribution spans across multiple repositories, 
  use the same branch name (e.g. `Fix_#XYZ`) in each PR so that our CI (Jenkins) can build them all at once.
- If your contribution spans across multiple repositories, make sure to list all the related PRs.

### Java Coding Guidelines

We decided to disallow `@author` tags in the Javadoc: they are hard to maintain, especially in a very active project, and we use the Git history to track authorship. GitHub also has [this nice page with your contributions](https://github.com/kiegroup/kogito-runtimes/graphs/contributors).

Copyright headers format is enforced during build time. In order to automatically format your files, you could run the following Maven command:
```bash
mvn com.mycila:license-maven-plugin:format
```

Make sure you have configured your IDE according to the [project codestyle](https://github.com/kiegroup/kogito-runtimes/tree/main/kogito-build/kogito-ide-config).

### Requirements for Dependencies

Any dependency used in any KIE project must fulfill these hard requirements:

- The dependency must have **an Apache 2.0 compatible license**.
    - Good: BSD, MIT, Apache 2.0
    - Avoid: EPL, LGPL
        - Especially LGPL is a last resort and should be abstracted away or contained behind an SPI.
        - Test scope dependencies pose no problem if they are EPL or LPGL.
    - Forbidden: no license, GPL, AGPL, proprietary license, field of use restrictions ("this software shall be used for good, not evil"), ...
        - Even test scope dependencies cannot use these licenses.
    - To check the ALS compatibility license please visit these links:[Similarity in terms to the Apache License 2.0](http://www.apache.org/legal/resolved.html#category-a)&nbsp; 
    [How should so-called "Weak Copyleft" Licenses be handled](http://www.apache.org/legal/resolved.html#category-b)

- The dependency shall be **available in [Maven Central](http://search.maven.org/) or [JBoss Nexus](https://repository.jboss.org/nexus)**.
    - Any version used must be in the repository Maven Central and/or JBoss (Nexus) Public repository group
        - Never add a `<repository>` element in a `pom.xml` when the artifact is intended for public usages, samples/demos are excluded from this.
    - Why?
        - Build reproducibility. Any repository server we use, must still run in future from now.
        - Build speed. More repositories slow down the build.
        - Build reliability. A repository server that is temporarily down can break builds.
    - Workaround to still use a great looking jar as a dependency:
        - Get that dependency into JBoss Nexus as a 3rd party library.

- **Do not release the dependency yourself** (by building it from source).
    - Why? Because it's not an official release, by the official release guys.
        - A release must be 100% reproducible.
        - A release must be reliable (sometimes the release person does specific things you might not reproduce).

- **The sources are publicly available**
    - We may need to rebuild the dependency from sources ourselves in future. This may be in the rare case when
      the dependency is no longer maintained, but we need to fix a specific CVE there.
    - Make sure the dependency's pom.xml contains link to the source repository (`scm` tag).

- The dependency needs to use **reasonable build system**
    - Since we may need to rebuild the dependency from sources, we also need to make sure it is easily buildable.
      Maven or Gradle are acceptable as build systems.

Any dependency used in any KOGITO projects should fulfill these soft requirements:
- **Edit dependencies** in **[kogito-build-parent](https://github.com/kiegroup/kogito-runtimes/blob/main/kogito-build/kogito-build-parent/pom.xml)**.
    - Dependencies in subprojects should avoid overwriting the dependency versions of kogito-build-parent if there is no special case or need for that.

- Only use dependencies with **an active community**.
    - Check for activity in the last year through [Open Hub](https://www.openhub.net).

- Less is more: **less dependencies is better**. Bloat is bad.
    - Try to use existing dependencies if the functionality is available in those dependencies
        - For example: use `poi` instead of `jexcelapi` if `poi` is already a KIE dependency

- **Do not use fat jars, nor shading jars.**
    - A fat jar is a jar that includes another jar's content. For example: `weld-se.jar` which includes `org/slf4j/Logger.class`
    - A shaded jar is a fat jar that shades that other jar's content. For example: `weld-se.jar` which includes `org/weld/org/slf4j/Logger.class`
    - Both are bad because they cause dependency tree trouble. Use the non-fat jar instead, for example: `weld-se-core.jar`

There are currently a few dependencies which violate some of these rules. They should be properly commented with a
warning and explaining why are needed
If you want to add a dependency that violates any of the rules above, get approval from the project leads.

### Tests and Documentation 

Don't forget to include tests in your pull requests, and documentation (reference documentation, javadoc...). Guides and reference documentation should be submitted to the [Kogito Docs Repository](https://github.com/kiegroup/kie-docs/tree/main-kogito).
If you are contributing a new feature, we strongly advise submitting an [Example](https://github.com/kiegroup/kogito-examples). 

- For Quarkus tests, basically use `@QuarkusTest` as unit tests for surefire-plugin and `@QuarkusIntegrationTest` as integration tests (`*IT.java`) for failsafe-plugin. Static http resources generated by `kogito-codegen` (`META-INF/resources/`) are available with `@QuarkusIntegrationTest`. If you need to access static http resources in `@QuarkusTest`, add `quarkus-undertow` dependency with `test` scope. Also note that you cannot mix `@QuarkusTest` and `@QuarkusIntegrationTest` in the same `integration-test` phase.

### Code Reviews and Continuous Integration

All submissions, including those by project members, need to be reviewed by others before being merged. Our CI, Jenkins, should successfully execute your PR, marking the GitHub check as green.

## Feature Proposals

If you would like to see some feature in Kogito, start with an email to [our mailing list](https://groups.google.com/forum/#!forum/kogito-development) or just [pop into our Zulip chat](https://kie.zulipchat.com/) and tell us what you would like to see. 

Great feature proposals should include a short **Description** of the feature, the **Motivation** that makes that feature necessary and the **Goals** that are achieved by realizing it. If the feature is deemed worthy, then an [**Epic**](https://issues.redhat.com/issues/?filter=12347334) will be created.

## Setup

If you have not done so on this machine, you need to:
 
* Install Git and configure your GitHub access
* Install Java SDK (OpenJDK recommended)
* For Native Image, follow Quarkus instructions at [GraalVM](https://quarkus.io/guides/building-native-image)
* On MAC, check [Setup MAC for Native image build](./Develop_on_Mac.md) for further instructions.

Docker is not strictly necessary, but it is a required to run some of the integration tests. 
These tests can be skipped (see the [Build](#build) section), but we recommend to install it to run these tests locally.

* Check [the installation guide](https://docs.docker.com/install/), and [the MacOS installation guide](https://docs.docker.com/docker-for-mac/install/)
* If you just install docker, be sure that your current user can run a container (no root required). 
On Linux, check [the post-installation guide](https://docs.docker.com/install/linux/linux-postinstall/)


## Build

* Clone the repository, navigate to the directory, invoke `./mvnw clean install -Dquickly` from the root directory.

```bash
git clone https://github.com/kiegroup/kogito-runtimes.git
cd kogito-runtimes
./mvnw clean install -Dquickly
# Wait... success!
```

This build skipped all the plugins and just go straight to build and install phases.

By removing the flags, you will run the unit and integration tests. 
It will take much longer to build but will give you more guarantees on your code.

Alternatively, you can invoke `./mvnw clean install -DquickTests` from the root directory.
It will perform the basic formatting validation and will run all the unit tests.
Use this command for quick checks.

### Known Issues

- [TestContainers integration tests fail with "Can not connect to Ryuk at localhost"](https://github.com/testcontainers/testcontainers-java/issues/3166)

  This may happen with some versions of Docker for Mac, or when privileged containers are not allowed. In this case you may try [exporting the environment variable TESTCONTAINERS_RYUK_DISABLED=true](https://www.testcontainers.org/features/configuration/#disabling-ryuk). Some users of Docker for Mac also report success disabling [FUSE gRPC file sharing](https://github.com/testcontainers/testcontainers-java/issues/3166).

## Usage

After the build is successful, the artifacts are available in your local Maven repository.

### Test Coverage

Kogito uses Jacoco to generate test coverage. If you would like to generate the report run `mvn clean verify -Ptest-coverage`. 
The code coverage report will be generated in `target/site/jacoco/`.

## The small print

This project is an open source project, please act responsibly, be nice, polite and enjoy!

