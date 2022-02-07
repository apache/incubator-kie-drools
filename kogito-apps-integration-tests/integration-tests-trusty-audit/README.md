# Trusty AI Console - end to end tests

This project runs Trusty AI Console and verifies it via Cypress E2E test suite which is located in [UI Packages](../../ui-packages/packages/trusty/it-tests).

## Requirements

- docker version >= 19.03.12
- java version >= 11
- maven version >= 3.6.3

Note: also previous versions of `docker` might work, but they were not tested.
Note: see [Manage Docker as a non-root user](https://docs.docker.com/engine/install/linux-postinstall/)

## Perform e2e test suite

The e2e test suite is performed during a build of whole project.

Go to the root folder of this project (kogito-apps) and run `mvn install` to build and performed all tests.

Note: We recommend running the e2e test suite after the `kogito-apps-ui-packages` module is built in your environment.
Otherwise, be sure that your environment is clean and contains all necessary tools.

In case that you want to run only the E2E test be sure that docker contains all images which are mentioned in the pom.xml file.

```
docker images
```

Use `mvn install -pl :integration-tests-trusty-audit -am` if you miss any docker image.
Go to this folder (integration-tests-trusty-audit) and perform `mvn install` directly in this module.

In case that you want to run just services (without test suite) to investigate problems then perform `mvn docker:run` when you interrupt this execution all services are stopped and removed. See [DMP docs](https://dmp.fabric8.io/#maven-goals).

## Clear docker

Remove obsolete image:

```
docker rmi $(docker images 'org.kie.kogito/integration-tests-trusty-service-quarkus' -a -q)
```

Or clear all docker images:

```
docker system prune --all
docker volume prune
docker network rm trusty-nw
```

Sometimes it seems that docker-maven-plugin does not stop all images so call

```
docker rm -fv $(docker ps -aq)
```
It is an issue of DMP [see](https://github.com/fabric8io/docker-maven-plugin/issues/552)