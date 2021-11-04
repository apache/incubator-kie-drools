Many thanks for submitting your Pull Request :heart:! 

Please make sure that your PR meets the following requirements:

- [ ] You have read the [contributors guide](CONTRIBUTING.md)
- [ ] Your code is properly formatted according to [this configuration](https://github.com/kiegroup/kogito-runtimes/tree/main/kogito-build/kogito-ide-config)
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

* <b>Run all builds</b>  
  Please add comment: <b>Jenkins retest this</b>

* <b>Run (or rerun) specific test(s)</b>  
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] tests</b>
 
* <b>Quarkus LTS checks</b>  
  Please add comment: <b>Jenkins run LTS</b>

* <b>Run (or rerun) LTS specific test(s)</b>  
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] LTS</b>

* <b>Native checks</b>  
  Please add comment: <b>Jenkins run native</b>

* <b>Run (or rerun) native specific test(s)</b>  
  Please add comment: <b>Jenkins (re)run [kogito-runtimes|optaplanner|kogito-apps|kogito-examples|optaplanner-quickstarts|optaweb-employee-rostering|optaweb-vehicle-routing] native</b>

* <b>Full Kogito testing</b> (with cloud images and operator BDD testing)  
  Please add comment: <b>Jenkins run BDD</b>  
  <b>This check should be used only if a big change is done as it takes time to run, need resources and one full BDD tests check can be done at a time ...</b>
</details>
