**Thank you for submitting this pull request**

**NOTE!:** Double-check the target branch for this PR.
The default is `main` so it will target Drools 8 / Kogito.

**Ports** If a forward-port or a backport is needed, paste the forward port PR here

* [link](https://www.example.com)

**Issue**: _(please edit the GitHub Issues link if it exists)_

* [link](https://www.example.com)

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

- for <b>pull request and downstream checks</b>  
  - Push a new commit to the PR. An empty commit would be enough.

- for a <b>full downstream build</b>
  - for <b>github actions</b> job: add the label `run_fdb`

- for <b>Jenkins PR check only</b>
  - If you are an ASF committer for KIE podling, login to Jenkins (https://ci-builds.apache.org/job/KIE/job/drools/), go to the specific PR job, and click on `Build Now` button.
</details>
