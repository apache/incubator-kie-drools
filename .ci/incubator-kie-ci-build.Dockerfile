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

# CI builder image for incubator-kie (Drools) Jenkins jobs.
# Published per stream by .ci/jenkins/Jenkinsfile.ci-image-build, e.g.
#   docker.io/apache/incubator-kie-kogito-ci-build:10.3.x-latest
#
# Keep SDKMAN_MAVEN in sync with <version.org.apache.maven> in kie-parent/pom.xml
# (enforced by maven-enforcer-plugin, rule requireMavenVersion).
FROM cruizba/ubuntu-dind:jammy-26.1.4

SHELL ["/bin/bash", "-c"]

ARG SDKMAN_JAVA="17.0.11-tem"
ARG SDKMAN_MAVEN="3.9.16"

ENV LANG='C.UTF-8'

RUN apt-get update && apt-get upgrade -y && apt-get install -y --no-install-recommends \
    sudo bash wget curl gpg ca-certificates \
    git findutils tar gzip unzip zip jq fontconfig \
    && apt-get clean autoclean && apt-get autoremove --yes \
    && rm -rf /var/lib/apt/lists/*

# gh cli (used by the shared pipeline libraries for GitHub operations)
RUN curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg \
    && chmod go+r /usr/share/keyrings/githubcli-archive-keyring.gpg \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" > /etc/apt/sources.list.d/github-cli.list \
    && apt-get update && apt-get install -y gh \
    && rm -rf /var/lib/apt/lists/*

# User setup (non-root)
RUN groupadd -g 910 nonrootuser && useradd --create-home -u 910 -g 910 -s /bin/bash nonrootuser && \
    echo "nonrootuser ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# Docker user setup (non-root)
RUN groupadd docker && usermod -aG docker nonrootuser

USER nonrootuser

# Java and Maven setup
RUN curl -s "https://get.sdkman.io" | bash && \
    source "$HOME/.sdkman/bin/sdkman-init.sh" && \
    sdk install java ${SDKMAN_JAVA} && \
    sudo update-alternatives --install /usr/local/bin/java java $(which java) 1 && \
    sdk install maven ${SDKMAN_MAVEN} && \
    sudo update-alternatives --install /usr/local/bin/mvn mvn $(which mvn) 1 && \
    sdk flush

# Env vars
ENV HOME="/home/nonrootuser"
ENV JAVA_HOME="${HOME}/.sdkman/candidates/java/current/"
ENV MAVEN_HOME="${HOME}/.sdkman/candidates/maven/current/"
ENV MAVEN_OPTS="-Xms1024m -Xmx12g"

ENTRYPOINT [""]

CMD ["bash"]
