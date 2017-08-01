/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.archive;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jbpm.test.container.webspherefix.WebSphereFixedJtaPlatform;
import org.jbpm.test.container.tools.IntegrationMavenResolver;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWebService.class);

    public static final String ARCHIVE_NAME = "hello-ws-1.0";
    public static final String SERVICE_URL = "http://localhost:" + System.getProperty("container.port") +
            "/" + ARCHIVE_NAME + "/";

    public static final String PROCESS_CALL_WEB_SERVICE = "org.jboss.qa.jbpm.CallWS";

    public static final String BPMN_CALL_WEB_SERVICE = "call-web-service.bpmn";
    public static final String BPMN_CALL_WEB_SERVICE_MULTI_IMPORTS = "call-web-service-multi-imports.bpmn";
    public static final String BPMN_CALL_WEB_SERVICE_NO_INTERFACE = "call-web-service-no-interface-name.bpmn";

    public static final String HELLO_WEB_SERVICE_PACKAGE = "org.jbpm.test.container.archive.hellowebservice";
    public static final String HELLO_WEB_SERVICE_PATH = "org/jbpm/test/container/archive/hellowebservice/";

    private WebArchive war;

    public WebArchive buildArchive() {
        System.out.println("### Building archive '" + ARCHIVE_NAME + ".war'");

        PomEquippedResolveStage resolver = IntegrationMavenResolver.get();
        File[] dependencies = resolver.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();
        LOGGER.debug("Archive dependencies:");
        for (File d : dependencies) {
            LOGGER.debug(d.getName());
        }
        
        war = ShrinkWrap
                .create(WebArchive.class, ARCHIVE_NAME + ".war")
                .addPackage(HELLO_WEB_SERVICE_PACKAGE)
                // Workaroud for https://hibernate.atlassian.net/browse/HHH-11606
                .addClass(WebSphereFixedJtaPlatform.class)
                .setWebXML(HELLO_WEB_SERVICE_PATH + "WEB-INF/web.xml")
                .addAsLibraries(dependencies);

        return war;
    }

    public Resource getResource(String resourceName) {
        return KieServices.Factory.get().getResources().newClassPathResource(HELLO_WEB_SERVICE_PATH + resourceName);
    }

    public WebArchive getWar() {
        return war;
    }

    public static String getContext() {
        return SERVICE_URL;
    }

    public void setWar(WebArchive war) {
        this.war = war;
    }

}
