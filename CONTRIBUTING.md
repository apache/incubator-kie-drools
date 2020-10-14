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

Kogito uses [JIRA to manage and report issues](https://issues.redhat.com/projects/KOGITO/).

If you believe you found a bug, please indicate a way to reproduce it, what you are seeing and what you would expect to see. Don't forget to indicate your Kogito, Java, Maven, Quarkus/Spring, GraalVM version. 

### Checking an issue is fixed in master

Sometimes a bug has been fixed in the `master` branch of Kogito and you want to confirm it is fixed for your own application. Testing the `master` branch is easy and you have two options:

* either use the snapshots we publish daily on https://repository.jboss.org/nexus/content/repositories/snapshots/
* or build Kogito all by yourself

If you are interested in having more details, refer to the [Build section](#build) and the [Usage section](#usage).

## Creating a Pull Request (PR)

To contribute, use GitHub Pull Requests, from your **own** fork. 

- PRs should be always related to an open JIRA issue. If there is none, you should create one.
- Try to fix only one issue per PR.
- Make sure to create a new branch. Usually branches are named after the JIRA ticket they are addressing. E.g. for ticket "KOGITO-XYZ An example issue" your branch should be at least prefixed with `KOGITO-XYZ`. E.g.:

        git checkout -b KOGITO-XYZ
        # or
        git checkout -b KOGITO-XYZ-my-fix

- When you submit your PR, make sure to include the ticket ID, and its title; e.g., "KOGITO-XYZ An example issue".
- The description of your PR should describe the code you wrote. The issue that is solved should be at least described properly in the corresponding JIRA ticket. 
- If your contribution spans across multiple repositories, 
  use the same branch name (e.g. `KOGITO-XYZ`) in each PR so that our CI (Jenkins) can build them all at once.
- If your contribution spans across multiple repositories, make sure to list all the related PRs.

### Coding Guidelines

We decided to disallow `@author` tags in the Javadoc: they are hard to maintain, especially in a very active project, and we use the Git history to track authorship. GitHub also has [this nice page with your contributions](https://github.com/kiegroup/kogito-runtimes/graphs/contributors). 

### Tests and Documentation 

Don't forget to include tests in your pull requests, and documentation (reference documentation, javadoc...). Guides and reference documentation should be submitted to the [Kogito Docs Repository](https://github.com/kiegroup/kie-docs/tree/master-kogito).
If you are contributing a new feature, we strongly advise submitting an [Example](https://github.com/kiegroup/kogito-examples). 

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

Docker is not strictly necessary, but it is a required to run some of the integration tests. 
These tests can be skipped (see the [Build](#build) section), but we recommend to install it to run these tests locally.

* Check [the installation guide](https://docs.docker.com/install/), and [the MacOS installation guide](https://docs.docker.com/docker-for-mac/install/)
* If you just install docker, be sure that your current user can run a container (no root required). 
On Linux, check [the post-installation guide](https://docs.docker.com/install/linux/linux-postinstall/)


## Build

* Clone the repository, navigate to the directory, invoke `./mvnw clean install -DskipTests -DskipITs` from the root directory.

```bash
git clone https://github.com/kiegroup/kogito-runtimes.git
cd kogito-runtimes
./mvnw clean install -DskipTests -DskipITs 
# Wait... success!
```

This build skipped all the tests:
- `-DskipTests` skips unit tests
- `-DskipITs` skips integration tests

By removing the flags, you will run the corresponding tests. 
It will take much longer to build but will give you more guarantees on your code. 

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

