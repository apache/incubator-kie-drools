#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

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
## (build-chain) Build upstream projects from the same branch. If needed, you can modify the `build_chain_file`, `build_chain_group` and `build_chain_branch`. See `build_chain_file` for setting correct environment variables
build-upstream: build-chain
	build-chain build cross_pr -f ${build_chain_file} -o /tmp/bc -p ${build_chain_project} -b ${build_chain_branch} -g ${build_chain_group} --skipParallelCheckout --skipProjectExecution kiegroup/kogito-runtimes --skipProjectCheckout kiegroup/kogito-runtimes

.PHONY: build-pr
pr_link=
## (build-chain) Build projects from a given `pr_link` argument. If needed, you can also modify the `build_chain_file` and `build_chain_project`. See `build_chain_file` for setting correct environment variables
build-pr: build-chain
	$(if $(pr_link),,$(error Please provide the 'pr_link' argument))
	build-chain build cross_pr -f ${build_chain_file} -o /tmp/bc -p ${build_chain_project} -u ${pr_link} --skipParallelCheckout

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

.PHONY: update-quarkus
## Update the quarkus version. Needs the `quarkus_version` argument set. See also '.ci/environments/common/update_quarkus.sh' for setting correct environment variables
update-quarkus:
	.ci/environments/common/update_quarkus.sh ${quarkus_version}
	$(MAKE) show-diff

.PHONY: prepare-env
## Prepare the repository for a specific environment. Needs the `environment` argument set. See also '.ci/environments/{environment}' script for setting correct environment variables
prepare-env:
	.ci/environments/update.sh ${environment}

.PHONY: tree
## Show project dependencies
tree:
	$(mvn_cmd) dependency:tree

.PHONY: show-diff
## Show Git diff
show-diff:
	git status
	git diff

.PHONY: help
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

.PHONY: build-chain
build-chain:
	which build-chain || npm i @kie/build-chain-action -g || printf "\nERROR: Cannot install build-chain. Please run \`npm i @kie/build-chain-action -g\` as sudo user\n"