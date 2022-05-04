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
changes that span multiple kiegroup repositories and depend on each other. -->
<!-- Example:
- https://github.com/kiegroup/droolsjbpm-build-bootstrap/pull/1234
- https://github.com/kiegroup/drools/pull/3000
- https://github.com/kiegroup/optaplanner/pull/899
- etc.
-->

### Checklist
- [ ] Documentation updated if applicable.
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
  please add comment: <b>Jenkins (re)run [optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] tests</b>
  
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
  Please add comment: <b>Jenkins (re)run [optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] quarkus-branch</b>

- for <b>quarkus main checks</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins run quarkus-main</b>

- for a <b>specific quarkus main check</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins (re)run [optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] quarkus-branch</b>

- for <b>native checks</b>  
  Run native checks  
  Please add comment: <b>Jenkins run native</b>

- for a <b>specific native check</b>  
  Run native checks 
  Please add comment: <b>Jenkins (re)run [optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] native</b>

- for <b>mandrel checks</b>  
  Run native checks against Mandrel image
  Please add comment: <b>Jenkins run mandrel</b>

- for a <b>specific mandrel check</b>  
  Run native checks against Mandrel image  
  Please add comment: <b>Jenkins (re)run [optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] mandrel</b>
</details>

### CI Status

 You can check OptaPlanner repositories CI status from [Chain Status webpage](https://kiegroup.github.io/optaplanner/).