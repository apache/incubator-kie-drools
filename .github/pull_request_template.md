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

Many thanks for submitting your Pull Request :heart:! 

Please make sure that your PR meets the following requirements:

- [ ] You have read the [contributors guide](CONTRIBUTING.md)
- [ ] Your code is properly formatted according to [this configuration](https://github.com/apache/incubator-kie-kogito-runtimes/tree/main/kogito-build/kogito-ide-config)
- [ ] Pull Request title is properly formatted: `KOGITO-XYZ Subject`
- [ ] Pull Request title contains the target branch if not targeting main: `[0.9.x] KOGITO-XYZ Subject`
- [ ] Pull Request contains link to the JIRA issue
- [ ] Pull Request contains link to any dependent or related Pull Request
- [ ] Pull Request contains description of the issue
- [ ] Pull Request does not include fixes for issues other than the main ticket

<details>
<summary>
How to replicate CI configuration locally?
</summary>

Build Chain tool does "simple" maven build(s), the builds are just Maven commands, but because the repositories relates and depends on each other and any change in API or class method could affect several of those repositories there is a need to use [build-chain tool](https://github.com/kiegroup/github-action-build-chain) to handle cross repository builds and be sure that we always use latest version of the code for each repository.
 
[build-chain tool](https://github.com/kiegroup/github-action-build-chain) is a build tool which can be used on command line locally or in Github Actions workflow(s), in case you need to change multiple repositories and send multiple dependent pull requests related with a change you can easily reproduce the same build by executing it on Github hosted environment or locally in your development environment. See [local execution](https://github.com/kiegroup/github-action-build-chain#local-execution) details to get more information about it.
</details>

<details>
<summary>
How to retest this PR or trigger a specific build:
</summary>

- for <b>pull request checks</b>  
  Please add comment: <b>Jenkins retest this</b>

- for a <b>specific pull request check</b>  
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|kogito-apps|kogito-examples] tests</b>

- for <b>quarkus branch checks</b>  
  Run checks against Quarkus current used branch  
  Please add comment: <b>Jenkins run quarkus-branch</b>

- for a <b>quarkus branch specific check</b>  
  Run checks against Quarkus current used branch  
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|kogito-apps|kogito-examples] quarkus-branch</b>

- for <b>quarkus main checks</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins run quarkus-main</b>

- for a <b>specific quarkus main check</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|kogito-apps|kogito-examples] quarkus-main</b>

- for <b>quarkus lts checks</b>  
  Run checks against Quarkus lts branch  
  Please add comment: <b>Jenkins run quarkus-lts</b>

- for a <b>specific quarkus lts check</b>  
  Run checks against Quarkus lts branch  
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|kogito-apps|kogito-examples] quarkus-lts</b>

- for <b>native checks</b>  
  Run native checks  
  Please add comment: <b>Jenkins run native</b>

- for a <b>specific native check</b>  
  Run native checks 
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|kogito-apps|kogito-examples] native</b>

- for <b>native lts checks</b>  
  Run native checks against quarkus lts branch
  Please add comment: <b>Jenkins run native-lts</b>

- for a <b>specific native lts check</b>  
  Run native checks against quarkus lts branch
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|kogito-apps|kogito-examples] native-lts</b>
</details>

<details>
<summary>
How to backport a pull request to a different branch?
</summary>

In order to automatically create a **backporting pull request** please add one or more labels having the following format `backport-<branch-name>`, where `<branch-name>` is the name of the branch where the pull request must be backported to (e.g., `backport-7.67.x` to backport the original PR to the `7.67.x` branch).

> **NOTE**: **backporting** is an action aiming to move a change (usually a commit) from a branch (usually the main one) to another one, which is generally referring to a still maintained release branch. Keeping it simple: it is about to move a specific change or a set of them from one branch to another.

Once the original pull request is successfully merged, the automated action will create one backporting pull request per each label (with the previous format) that has been added.

If something goes wrong, the author will be notified and at this point a manual backporting is needed.

> **NOTE**: this automated backporting is triggered whenever a pull request on `main` branch is labeled or closed, but both conditions must be satisfied to get the new PR created.
</details>

<details>
<summary>
Quarkus-3 PR check is failing ... what to do ?
</summary>
The Quarkus 3 check is applying patches from the `.ci/environments/quarkus-3/patches`.

The first patch, called `0001_before_sh.patch`, is generated from Openrewrite `.ci/environments/quarkus-3/quarkus3.yml` recipe. The patch is created to speed up the check. But it may be that some changes in the PR broke this patch.  
No panic, there is an easy way to regenerate it. You just need to comment on the PR:
```
jenkins rewrite quarkus-3
```
and it should, after some minutes (~20/30min) apply a commit on the PR with the patch regenerated.

Other patches were generated manually. If any of it fails, you will need to manually update it... and push your changes.
</details>
