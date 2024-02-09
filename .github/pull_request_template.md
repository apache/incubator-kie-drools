**Thank you for submitting this pull request**

**NOTE!:** Double check the target branch for this PR.
The default is `main` so it will target Drools 8 / Kogito.
If this PR is not strictly related to drools and kogito project in `drools.git`, it should probably target `7.x`as a branch

**Ports** If a forward-port or a backport is needed, paste the forward port PR here

[link](https://www.example.com)

**JIRA**: _(please edit the JIRA link if it exists)_

[link](https://www.example.com)

**referenced Pull Requests**: _(please edit the URLs of referenced pullrequests if they exist)_

* paste the link(s) from GitHub here
* link 2
* link 3 etc.

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
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] tests</b>

- for a <b>full downstream build</b> 
  - for <b>jenkins</b> job: please add comment: <b>Jenkins run fdb</b>
  - for <b>github actions</b> job: add the label `run_fdb`

- <b>a compile downstream build</b> please  add comment: <b>Jenkins run cdb</b>

- <b>an upstream build</b> please add comment: <b>Jenkins run upstream</b>

- for <b>quarkus branch checks</b>  
  Run checks against Quarkus current used branch  
  Please add comment: <b>Jenkins run quarkus-branch</b>

- for a <b>quarkus branch specific check</b>  
  Run checks against Quarkus current used branch  
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] quarkus-branch</b>

- for <b>quarkus main checks</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins run quarkus-main</b>

- for a <b>specific quarkus main check</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] quarkus-main</b>

- for <b>quarkus lts checks</b>  
  Run checks against Quarkus lts branch  
  Please add comment: <b>Jenkins run quarkus-lts</b>

- for a <b>specific quarkus lts check</b>  
  Run checks against Quarkus lts branch  
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] quarkus-lts</b>

- for <b>native checks</b>  
  Run native checks  
  Please add comment: <b>Jenkins run native</b>

- for a <b>specific native check</b>  
  Run native checks 
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] native</b>

 - for <b>native lts checks</b>  
  Run native checks against quarkus lts branch
  Please add comment: <b>Jenkins run native-lts</b>

- for a <b>specific native lts check</b>  
  Run native checks against quarkus lts branch
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] native-lts</b>
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

<!-- TODO to uncomment if activating the quarkus-3 rewrite PR job -->
<!-- <details>
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
</details> -->