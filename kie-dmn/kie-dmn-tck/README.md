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

# kie-dmn-tck

This is a Maven utility module for DMN TCK support; this module is used to integrate **stable DMN TCK tests** inside of kie-dmn CI.

The Drools DMN Engine aim to support the DMN Specification at the best compatibility level possible, that is defined as _Conformance Level 3_; the several test suites in the different kie-dmn modules contribute to demonstrate this compatibility and complete level of support of the DMN Specification.

The purpose of this _additional module_ is to help increase the level of confidence of conformance to the DMN specification, with additional Drools DMN Engine conformance checks, contributed by the DMN TCK initiative.

For more information about the DMN TCK: https://dmn-tck.github.io/tck/about.html

## Configuration

The git coordinates of the DMN TCK repository are configured in the `pom.xml`.

In the remainder of this documentation, the following conventions are assumed:

```
TCK repository URL: https://github.com/dmn-tck/tck
default TCK branch: master
```

## Testing *stable* DMN TCK tests locally

You can test this module locally by making use of the appropriate maven profile `-Pdmn-tck`.

NOTE: no TCK tests will be run without enabling the appropriate Maven profile.

Please reference the configuration of `pom.xml` for more details.

## Testing *new* DMN TCK tests contributions (without `kie-dmn-tck`)

New contributions to the DMN TCK are submitted in the form of classic GitHub pull requests, here: https://github.com/dmn-tck/tck/pulls

New DMN TCK tests currently under discussion at the TCK initiative must be tested manually, by checking out the specific pull request locally.
This `kie-dmn-tck` module will execute **stable DMN TCK tests** present on the default branch ONLY.

The following workflow is suggested in order to keep tabs on new DMN TCK tests being proposed, in the scope of checking Drools DMN Engine behaviour:

1. Identify an open PR at the DMN TCK which is not yet reviewed or commented on; check the PR against the latest Drools snapshot.  
Avoid PR labeled with `Waiting on RTF` or similar, as that label designates that the TCK group has identified a potential problem with the Specification itself, which needs to be handled at the OMG’s RTF.

2. If any test failure is detected, investigate the reason for the test failures: the failure could be due to Drools bug, or the test could be wrong according to the Specification.

   1. If the test is wrong because it's not accounting for some requirements of the DMN Specification, feel free to  argument directly on the TCK PR. ([example](https://github.com/dmn-tck/tck/pull/401#issuecomment-962982239))

   2. If the test is failing allegedly for some kind Drools bug, raise a JIRA and discuss potential fixes.

### Check out locally a TCK pull request

To checkout the TCK PR locally, you must first clone locally the DMN TCK repository.

Then, to checkout an open PR from **Step 1.**, from the cloned DMN TCK repository on your machine, you can follow the instructions in [this guide](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/reviewing-changes-in-pull-requests/checking-out-pull-requests-locally).

Alternatively, you might opt to setup a dedicated git remote named "pr" to track just pull request branches specifically with:

```
[remote "pr"]
	url = git@github.com:dmn-tck/tck.git
	fetch = +refs/pull/*/head:refs/remotes/pr/*
```

and you can checkout a specific pull request with `git checkout pr/<ID>`.

### Run locally the TCK pull request

After checkout of the specific pull request locally, you must first rebase it on the default branch with `git rebase master`.

The TCK PR is now checked out locally and rebased on the default branch. You can run the whole test suite with `mvn clean install -Pdrools --file runners/pom.xml`.

Alternatively you can:

```
cd runners
mvn clean install -Pdrools
```

You can also override the Drools version by passing the parameter `-Ddrools.version=<version>` to the Maven command. This is helpful when you want to work with the latest Drools DMN Engine snapshot version.
