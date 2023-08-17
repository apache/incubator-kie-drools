mvn_opts=
mvn_cmd=mvn $(mvn_opts)

build_chain_branch=$(shell git branch --show-current)
build_chain_file='https://raw.githubusercontent.com/kiegroup/drools/main/.ci/pull-request-config.yaml'
build_chain_group='kiegroup'
build_chain_project='kiegroup/drools'

default: help

.PHONY: build
## Build the project
build:
	$(mvn_cmd) clean install

.PHONY: build-quickly
## Perform a quick build of the project
build-quickly:
	$(mvn_cmd) clean install -Dquickly

.PHONY: build-upstream
## Build (build-chain) upstream projects from the same branch. If needed, you can modify the `build_chain_file`, `build_chain_group` and `build_chain_branch` if needed.
build-upstream: build-chain
	export BUILD_MVN_OPTS="${mvn_opts}" && build-chain build cross_pr -f ${build_chain_file} -o /tmp/bc -p ${build_chain_project} -b ${build_chain_branch} -g ${build_chain_group} --skipParallelCheckout --skipProjectExecution kiegroup/drools --skipProjectCheckout kiegroup/drools

.PHONY: build-pr
pr_link=
## Build (build-chain) projects from a given `pr_link` argument. If needed, you can also modify the `build_chain_file` and `build_chain_project` if needed.
build-pr: build-chain
	$(if $(pr_link),,$(error Please provide the 'pr_link' argument))
	export BUILD_MVN_OPTS=${mvn_opts} && build-chain build cross_pr -f ${build_chain_file} -o /tmp/bc -p ${build_chain_project} -u ${pr_link} --skipParallelCheckout

.PHONY: test
## Launch full testing
test:
	$(mvn_cmd) clean verify

.PHONY: quick-test
## Launch unit testing
quick-test:
	$(mvn_cmd) clean verify -DquickTests

.PHONY: deploy
## Deploy the project
deploy:
	$(mvn_cmd) deploy

.PHONY: clean
## Clean the project
clean:
	$(mvn_cmd) clean

.PHONY: mvn
## Execute a Maven command with project configuration. Needs the `cmd` argument set
mvn:
	$(mvn_cmd) ${cmd}

## Update the quarkus version. Needs the `quarkus_version` argument set
update-quarkus:
	export BUILD_MVN_OPTS=${mvn_opts} && .ci/environments/common/update_quarkus.sh ${quarkus_version}
	$(MAKE) show-diff

## Prepare the repository for a specific environment. Needs the `environment` argument set
prepare-env:
    export BUILD_MVN_OPTS=${mvn_opts} && .ci/environments/update.sh ${environment}

## Show project dependencies
tree:
	$(mvn_cmd) dependency:tree

## Show Git diff
show-diff:
	git status
	git diff

## This help screen
help:
	@printf "Available targets:\n\n"
	@awk '/^[a-zA-Z\-_0-9%:\\]+/ { \
		helpMessage = match(lastLine, /^## (.*)/); \
		if (helpMessage) { \
			helpCommand = $$1; \
			helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
			gsub("\\\\", "", helpCommand); \
			gsub(":+$$", "", helpCommand); \
			printf "  \x1b[32;01m%-35s\x1b[0m %s\n", helpCommand, helpMessage; \
		} \
	} \
	{ lastLine = $$0 }' $(MAKEFILE_LIST)
	@printf "\n"
	@printf "All Maven commands can include some maven options via the \`mvn_opts\` argument !"
	@printf "\n"

build-chain:
	which build-chain || npm i @kie/build-chain-action -g || printf "\nERROR: Cannot install build-chain. Please run \`npm i @kie/build-chain-action -g\` as sudo user\n"
