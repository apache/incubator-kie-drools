Many thanks for submitting your Pull Request :heart:! 

Please make sure that your PR meets the following requirements:

- [ ] You have read the [contributors guide](https://github.com/kiegroup/kogito-runtimes#contributing-to-kogito)
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
  Please add comment: <b>Jenkins (re)run [kogito-apps|kogito-examples] tests</b>

- for <b>quarkus branch checks</b>  
  Run checks against Quarkus current used branch  
  Please add comment: <b>Jenkins run quarkus-branch</b>

- for a <b>quarkus branch specific check</b>  
  Run checks against Quarkus current used branch  
  Please add comment: <b>Jenkins (re)run [kogito-apps|kogito-examples] quarkus-branch</b>

- for <b>quarkus main checks</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins run quarkus-main</b>

- for a <b>specific quarkus main check</b>  
  Run checks against Quarkus main branch  
  Please add comment: <b>Jenkins (re)run [kogito-apps|kogito-examples] quarkus-main</b>

- for <b>quarkus lts checks</b>  
  Run checks against Quarkus lts branch  
  Please add comment: <b>Jenkins run quarkus-lts</b>

- for a <b>specific quarkus lts check</b>  
  Run checks against Quarkus lts branch  
  Please add comment: <b>Jenkins (re)run [kogito-apps|kogito-examples] quarkus-lts</b>
 
- for <b>native checks</b>  
  Run native checks  
  Please add comment: <b>Jenkins run native</b>

- for a <b>specific native check</b>  
  Run native checks 
  Please add comment: <b>Jenkins (re)run [kogito-apps|kogito-examples] native</b>

- for <b>mandrel checks</b>  
  Run native checks against Mandrel image
  Please add comment: <b>Jenkins run mandrel</b>

- for a <b>specific mandrel check</b>  
  Run native checks against Mandrel image  
  Please add comment: <b>Jenkins (re)run [kogito-apps|kogito-examples] mandrel</b>

- for <b>mandrel lts checks</b>  
  Run native checks against Mandrel image and quarkus lts branch
  Please add comment: <b>Jenkins run mandrel-lts</b>

- for a <b>specific mandrel lts check</b>  
  Run native checks against Mandrel image and quarkus lts branch
  Please add comment: <b>Jenkins (re)run [kogito-apps|kogito-examples] mandrel-lts</b>
 
</details>
