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

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jbpm.test.container.handlers.ListWorkItemHandler;
import org.jbpm.test.container.listeners.TrackingAgendaEventListener;
import org.jbpm.test.container.listeners.TrackingProcessEventListener;
import org.jbpm.test.container.webspherefix.WebSphereFixedJtaPlatform;
import org.jbpm.test.container.tools.IntegrationMavenResolver;
import org.jbpm.test.container.tools.KieUtils;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTransactions {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTransactions.class);

    public static final String ARCHIVE_NAME = "local-transactions";
    public static final String SERVICE_URL = "http://localhost:" + System.getProperty("container.port") +
            "/" + ARCHIVE_NAME + "/";

    public static final String PROCESS_TRANSACTIONS = "transactions";

    public static final String BPMN_TRANSACTIONS = "transactions-process.bpmn";
    public static final String BPMN_HELLO_WORLD = "hello-world_1.0.bpmn";
    public static final String RULES_TRANSACTIONS = "transactions-rules.drl";

    public static final String LOCAL_TRANSACTIONS_PATH = "org/jbpm/test/container/archive/localtransactions/";


    private WebArchive war;

    public WebArchive buildArchive() {
        System.out.println("### Building archive '" + ARCHIVE_NAME + ".war'");

        PomEquippedResolveStage resolver = IntegrationMavenResolver.get("jbpm", "jbpm-persistence");
        File[] dependencies = resolver.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();
        LOGGER.debug("Archive dependencies:");
        for (File d : dependencies) {
            LOGGER.debug(d.getName());
        }

        war = ShrinkWrap.create(WebArchive.class, ARCHIVE_NAME + ".war")
                .addAsResource(LOCAL_TRANSACTIONS_PATH + BPMN_TRANSACTIONS)
                .addAsResource(LOCAL_TRANSACTIONS_PATH + BPMN_HELLO_WORLD)
                .addAsResource(LOCAL_TRANSACTIONS_PATH + RULES_TRANSACTIONS)
                .addAsLibraries(dependencies);

        war.addClass(LocalTransactions.class)
                // Workaroud for https://hibernate.atlassian.net/browse/HHH-11606
                .addClass(WebSphereFixedJtaPlatform.class)
                .addClass(ListWorkItemHandler.class)
                .addClass(TrackingAgendaEventListener.class)
                .addClass(TrackingProcessEventListener.class)
                .addClass(KieUtils.class);

        war.addPackages(true, "org.jbpm.test.container.groups");

        // WEB-INF resources
        war.addAsWebResource(getClass().getResource("/logback.xml"), ArchivePaths.create("logback.xml"));

        // CDI beans.xml
        war.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("classes/META-INF/beans.xml"));

        war.addAsResource(getClass().getResource("/persistence.xml"),
                ArchivePaths.create("META-INF/persistence.xml"));

        war.addAsWebResource(getClass().getResource("localtransactions/tomcat-context.xml"),
                ArchivePaths.create("META-INF/context.xml"));

        war.addAsResource(getClass().getResource("localtransactions/jbossts-properties.xml"), ArchivePaths.create("jbossts-properties.xml"));

        return war;
    }

    public Resource getResource(String resourceName) {
        return KieServices.Factory.get().getResources()
                .newClassPathResource(LOCAL_TRANSACTIONS_PATH + resourceName);
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
