jBPM in container tests
=====================

**This test suite tests the jBPM engine inside various containers, namely WildFly 11, EAP 7, Tomcat 9,
Oracle WebLogic 12 and IBM WebSphere 9, using Maven Cargo and Arquillian.** Tests are focused on various aspects of jBPM engine 
such as transactions, EJB APIs, tasks, REST and WebServices integration.

This module consists of three submodules:
* jbpm-container-integration-deps - Various dependencies for Arquillian archives generated using ShrinkWrap
* jbpm-container-test-suite - Test suite itself
* shrinkwrap-war-profiles - Maven profiles for ShrinkWrap, they group various jbpm-container-integration-deps dependencies together

Tests are run very easily using the command

```mvn clean install -Dcontainer.profile=<container-profile> <container-specific-params>```

where `<container-profile>` is simply a particular container. Another container-specific parameters may also be configured (see the table below).
WildFly11, EAP 7 and Tomcat 9 do not have to be pre-installed, they will be downloaded automatically (in case of EAP 7, download URL has to be provided).
Oracle WebLogic 12 and IBM WebSphere 9 have to be pre-installed and the installation path has to be provided using a Maven property `weblogic.home` or `websphere.home` respectively.

The following table lists all currently supported combinations of parameters:

| Container to run    | \<container-profile\> | \<container-specific params\>                                                   |
| -----------------   | --------------------- | ------------------------------------------------------------------------------- |
|     WildFly11       | wildfly11             | *none*                                                                          |
|     EAP 7           | eap7                  | eap7.download.url                                                               |
|     Tomcat 9        | tomcat9               | *none*                                                                          |
| Oracle WebLogic 12  | oracle-wls-12         | weblogic.home                                                                   |
| IBM WebSphere 9     | was9                  | websphere.home, env.WAS9_HOME<sup>1</sup>, ws_admin_client_jar_name<sup>2</sup> |

<sup>1</sup> Special property for Arquillian adapter for WebSphere, the value is the same as websphere.home  
<sup>2</sup> Needed to override dependency used by Arquillian, because by default it contains Python JSR 223 implementation which causes problems for some engine tests,
its value should be **com.ibm.ws.admin.client.forJython21_9.0.jar**

## Database configuration
By default, the tests are run with the H2 database. If you want to change the database, simply override **Datasource properties** in the **jbpm-container-test/pom.xml** file.

## WebSphere and Arquillian adapter
Currently, Arquillian adapter for WebSphere is not publicly available in Central Maven Repository. Instead, you have to build your own adapter and add it as a dependency to the profile **was9** in the **jbpm-container-test-suite/pom.xml** file.
This adapter's source code is available on [GitHub](https://github.com/arquillian/arquillian-container-was/tree/master/was-remote-9) as well.

## Running a single test
If you want to run a single test, just specify it via an additional parameter ```-Dit.test=<test-name>``` and add ```-DfailIfNoTests=false```.
The latter has to be provided because of maven failsafe plugin which will fail by default if no tests are run on other modules.
