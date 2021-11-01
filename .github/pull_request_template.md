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
* https://github.com/kiegroup/droolsjbpm-build-bootstrap/pull/1234
* https://github.com/kiegroup/drools/pull/3000
* https://github.com/kiegroup/optaplanner/pull/899
* etc.
-->

### Checklist
- [ ] Documentation updated if applicable.
- [ ] Upgrade recipe provided if applicable.

<details>
<summary>
How to replicate CI configuration locally?
</summary>

We do "simple" maven builds, they are just basically maven commands, but just because we have multiple repositories related between them and one change could affect several of those projects by multiple pull requests, we use [build-chain tool](https://github.com/kiegroup/github-action-build-chain) to handle cross repository builds and be sure that we always use latest version of the code for each repository.

[build-chain tool](https://github.com/kiegroup/github-action-build-chain) is not only a github-action tool but a CLI one, so in case you posted multiple pull requests related with this change you can easily reproduce the same build by executing it locally. See [local execution](https://github.com/kiegroup/github-action-build-chain#local-execution) details to get more information about it.
</details>

<details>
<summary>
How to retest this PR or trigger a specific build:
</summary>

* for a <b>pull request build</b> please add comment: <b>Jenkins retest this</b>
* for a <b>specific pull request build</b> please add comment: <b>Jenkins (re)run [optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] tests</b>
* for a <b>full downstream build</b> 
  * for <b>jenkins</b> job: please add comment: <b>Jenkins run fdb</b>
  * for <b>github actions</b> job: add the label `run_fdb`
* for a <b>compile downstream build</b> please add comment: <b>Jenkins run cdb</b>
* for a <b>full production downstream build</b> please add comment: <b>Jenkins execute product fdb</b>
* for an <b>upstream build</b> please add comment: <b>Jenkins run upstream</b>
* for a <b>Quarkus LTS check</b> please add comment: <b>Jenkins run LTS</b>
* for a <b>specific Quarkus LTS check</b> please add comment: <b>Jenkins (re)run [optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] LTS</b>
* for a <b>Native check</b> please add comment: <b>Jenkins run native</b>
* for a <b>specific Native LTS check</b> please add comment: <b>Jenkins (re)run [optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] native</b>
</details>
