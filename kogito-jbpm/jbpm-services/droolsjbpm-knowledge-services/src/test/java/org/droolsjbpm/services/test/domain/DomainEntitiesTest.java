/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droolsjbpm.services.test.domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.droolsjbpm.services.api.DomainManagerService;
import org.droolsjbpm.services.domain.entities.Domain;
import org.droolsjbpm.services.domain.entities.Organization;
import org.droolsjbpm.services.domain.entities.RuntimeId;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 */
@RunWith(Arquillian.class)
public class DomainEntitiesTest {

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "droolsjbpm-knowledge-services.jar")
                .addPackage("org.jboss.seam.persistence") //seam-persistence
                .addPackage("org.jboss.seam.transaction") //seam-persistence
                .addPackage("org.jbpm.services.task")
                .addPackage("org.jbpm.services.task.wih") // work items org.jbpm.services.task.wih
                .addPackage("org.jbpm.services.task.annotations")
                .addPackage("org.jbpm.services.task.api")
                .addPackage("org.jbpm.services.task.impl")
                .addPackage("org.jbpm.services.task.events")
                .addPackage("org.jbpm.services.task.exception")
                .addPackage("org.jbpm.services.task.identity")
                .addPackage("org.jbpm.services.task.factories")
                .addPackage("org.jbpm.services.task.internals")
                .addPackage("org.jbpm.services.task.internals.lifecycle")
                .addPackage("org.jbpm.services.task.lifecycle.listeners")
                .addPackage("org.jbpm.services.task.query")
                .addPackage("org.jbpm.services.task.util")
                .addPackage("org.jbpm.services.task.commands") // This should not be required here
                .addPackage("org.jbpm.services.task.deadlines") // deadlines
                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.services.task.subtask")
                .addPackage("org.droolsjbpm.services.api")
                .addPackage("org.droolsjbpm.services.api.bpmn2")
                .addPackage("org.droolsjbpm.services.impl")
                .addPackage("org.droolsjbpm.services.impl.bpmn2")
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.droolsjbpm.services.impl.vfs")
                .addPackage("org.kie.commons.java.nio.fs.jgit")
                .addPackage("org.droolsjbpm.services.test")
                .addPackage("org.droolsjbpm.services.impl.event.listeners")
                .addPackage("org.droolsjbpm.services.impl.example")
                .addPackage("org.droolsjbpm.services.impl.audit")
                .addPackage("org.droolsjbpm.services.impl.util")
                
                .addPackage("org.kie.internal.runtime")
                .addPackage("org.kie.internal.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
                
                .addPackage("org.jbpm.runtime.manager")
                .addPackage("org.jbpm.runtime.manager.impl")
                .addPackage("org.jbpm.runtime.manager.impl.cdi.qualifier")
                
                .addPackage("org.jbpm.runtime.manager.impl.context")
                .addPackage("org.jbpm.runtime.manager.impl.factory")
                .addPackage("org.jbpm.runtime.manager.impl.jpa")
                .addPackage("org.jbpm.runtime.manager.impl.manager")
                .addPackage("org.jbpm.runtime.manager.mapper")
                .addPackage("org.jbpm.runtime.manager.impl.task")
                .addPackage("org.jbpm.runtime.manager.impl.tx")
                
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
                .addAsManifestResource("META-INF/services/org.kie.commons.java.nio.file.spi.FileSystemProvider", ArchivePaths.create("org.kie.commons.java.nio.file.spi.FileSystemProvider"));

    }
    @Inject
    protected DomainManagerService domainService;

    @Test
    public void simpleDomainTest() {
        Organization organization = new Organization();
        organization.setName("JBoss");
        Domain domain = new Domain();
        domain.setName("My First Domain");

        List<RuntimeId> runtimes = new ArrayList<RuntimeId>();
        RuntimeId runtime1 = new RuntimeId();
        runtime1.setReference("vfs://support.bpmn");
        runtime1.setDomain(domain);
        RuntimeId runtime2 = new RuntimeId();
        runtime2.setReference("org.jbpm:examples:1.0-SNAPSHOT");
        runtime2.setDomain(domain);
        runtimes.add(runtime1);
        runtimes.add(runtime2);
        domain.setRuntimes(runtimes);
        domain.setOrganization(organization);


        List<Domain> domains = new ArrayList<Domain>();
        domains.add(domain);
        organization.setDomains(domains);

        long storedOrganization = domainService.storeOrganization(organization);
        assertEquals(1, storedOrganization);

        List<Domain> allDomains = domainService.getAllDomains();
        assertEquals(1, allDomains.size());
        List<Organization> allOrganizations = domainService.getAllOrganizations();
        assertEquals(1, allOrganizations.size());

        List<Domain> allDomainsByOrganization = domainService.getAllDomainsByOrganization(storedOrganization);

        assertEquals(1, allDomainsByOrganization.size());
        Domain domainById = domainService.getDomainById(domain.getId());
        assertEquals("My First Domain", domainById.getName());

        assertEquals("JBoss", domainById.getOrganization().getName());


    }

}
