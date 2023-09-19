<!--
Thank you for submitting this pull request.

Please provide all relevant information as outlined below. Feel free to delete
a section if that type of information is not available.
-->

### JIRA

<!-- Add a JIRA ticket link if it exists. -->
<!-- Example: https://issues.redhat.com/browse/PLANNER-1234 -->

### Referenced pull requests

<!-- Add URLs of all referenced pull requests if they exist. This is only required when making
changes that span multiple apache repositories and depend on each other. -->
<!-- Example:
- https://github.com/kiegroup/droolsjbpm-build-bootstrap/pull/1234
- https://github.com/apache/incubator-kie-drools/pull/3000
- https://github.com/apache/incubator-kie-optaplanner/pull/899
- etc.
-->

### Checklist
- [ ] Documentation updated if applicable.
- [ ] Release notes updated if applicable.
- [ ] Upgrade recipe provided if applicable.

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
  please add comment: <b>Jenkins (re)run [optaplanner|optaplanner-quickstarts] tests</b>
  
- for a <b>full downstream build</b> 
  - for <b>jenkins</b> job: 
    please add comment: <b>Jenkins run fdb</b>
  - for <b>github actions</b> job: 
    add the label `run_fdb`

- for a <b>compile downstream build</b>  
  please add comment: <b>Jenkins run cdb</b>

- for a <b>full production downstream build</b>  
  please add comment: <b>Jenkins execute product fdb</b>

- for an <b>upstream build</b>  
  please add comment: <b>Jenkins run upstream</b>

- for <b>quarkus branch checks</b>  
  Run checks against Quarkus current used branch  
  Please add comment: <b>Jenkins run quarkus-branch</b>

- for a <b>quarkus branch specific check</b>  
  Run checks against Quarkus current used branch  
  Please add comment: <b>Jenkins (re)run [optaplanner|optaplanner-quickstarts] quarkus-branch</b>

- for <b>quarkus main checks</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins run quarkus-main</b>

- for a <b>specific quarkus main check</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins (re)run [optaplanner|optaplanner-quickstarts] quarkus-branch</b>

- for <b>quarkus lts checks</b>  
  Run checks against Quarkus lts branch  
  Please add comment: <b>Jenkins run quarkus-lts</b>

- for a <b>specific quarkus lts check</b>  
  Run checks against Quarkus lts branch  
  Please add comment: <b>Jenkins (re)run [optaplanner|optaplanner-quickstarts] quarkus-lts</b>

- for <b>native checks</b>  
  Run native checks  
  Please add comment: <b>Jenkins run native</b>

- for a <b>specific native check</b>  
  Run native checks 
  Please add comment: <b>Jenkins (re)run [optaplanner|optaplanner-quickstarts] native</b>

- for <b>native lts checks</b>  
  Run native checks against quarkus lts branch
  Please add comment: <b>Jenkins run native-lts</b>

- for a <b>specific native lts check</b>  
  Run native checks against quarkus lts branch
  Please add comment: <b>Jenkins (re)run [optaplanner|optaplanner-quickstarts] native-lts</b>

</details>

### CI Status

 You can check OptaPlanner repositories CI status from [Chain Status webpage](https://apache.github.io/incubator-kie-optaplanner/).

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
