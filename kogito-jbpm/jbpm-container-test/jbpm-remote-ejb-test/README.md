jBPM remote EJB container tests
=====================

*This test suite tests remote access to jBPM engine via jbpm-services-ejb. This access is supported for WildFly 10 and EAP 7.*

This test suite consists of four submodules:
* jbpm-remote-ejb-test-app - Standalone WAR test application containing jbpm-services-ejb.
* jbpm-remote-ejb-test-suite - Test suite itself.
* jbpm-remote-ejb-test-domain - Module with shared domain classes.
* test-kjar-parent - Module containing testing KJARs as submodules.

Tests are run using the command

```mvn clean install -Dcontainer.profile=<container-profile> <container-specific-params>```

where `<container-profile>` is simply a particular container. Another container-specific parameters may also be configured (see the table below).
Both supported containers (WildFly10 and EAP 7) will be downloaded automatically (in case of EAP 7, download URL has to be provided).

The following table lists all currently supported combinations of parameters:

| Container to run    | \<container-profile\> | \<container-specific params\>             |
| -----------------   | --------------------- | ----------------------------------------- |
|     WildFly10       | wildfly10             | *none*                                    |
|     EAP 7           | eap7                  | eap7.download.url                         |

## Database configuration
By default, the tests are run with the H2 database. If you want to change the database, simply override **Datasource properties** in the **jbpm-container-test/pom.xml** file.