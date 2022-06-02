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

- <b>a full production downstream build</b> please add comment: <b>Jenkins execute product fdb</b>

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
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] quarkus-branch</b>

- for <b>native checks</b>  
  Run native checks  
  Please add comment: <b>Jenkins run native</b>

- for a <b>specific native check</b>  
  Run native checks 
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] native</b>

- for <b>mandrel checks</b>  
  Run native checks against Mandrel image
  Please add comment: <b>Jenkins run mandrel</b>

- for a <b>specific mandrel check</b>  
  Run native checks against Mandrel image  
  Please add comment: <b>Jenkins (re)run [drools|kogito-runtimes|kogito-apps|kogito-examples] mandrel</b>
</details>
