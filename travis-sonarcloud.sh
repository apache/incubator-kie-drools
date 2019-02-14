#!/usr/bin/env bash
if [ "$TRAVIS_EVENT_TYPE" == "pull_request" ]
then
  mvn --quiet jacoco:report jacoco:merge sonar:sonar -Preport-code-coverage \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=kiegroup \
  -Dsonar.projectKey=org.optaplanner:optaplanner \
  -Dsonar.login=$SONARCLOUD_TOKEN \
  -Dsonar.pullrequest.base=$TRAVIS_BRANCH \
  -Dsonar.pullrequest.branch=$TRAVIS_PULL_REQUEST_BRANCH \
  -Dsonar.pullrequest.key=$TRAVIS_PULL_REQUEST \
  -Dsonar.pullrequest.provider=GitHub \
  -Dsonar.pullrequest.github.repository=$TRAVIS_PULL_REQUEST_SLUG
else
  mvn --quiet jacoco:report jacoco:merge sonar:sonar -Preport-code-coverage \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=kiegroup \
  -Dsonar.projectKey=org.optaplanner:optaplanner \
  -Dsonar.login=$SONARCLOUD_TOKEN
fi
