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

package org.jbpm.executor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@RunWith(Arquillian.class)
public class CDISimpleExecutorTest extends BasicExecutorBaseTest {

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "executor-service.jar")
                .addPackage("org.jboss.seam.transaction") //seam-persistence
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.kie.commons.java.nio.fs.jgit")
                .addPackage("org.jbpm.executor")
                .addPackage("org.jbpm.executor.api")
                .addPackage("org.jbpm.executor.impl")
                .addPackage("org.jbpm.executor.entities")
                .addPackage("org.jbpm.executor.commands")
                .addPackage("org.jbpm.executor.events.listeners")
                .addPackage("org.jbpm.executor.annotations")
                
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Executor-orm.xml", ArchivePaths.create("Executor-orm.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
        		.addAsManifestResource("META-INF/javax.enterprise.inject.spi.Extension", 
        				"services/javax.enterprise.inject.spi.Extension");

    }

    private static PoolingDataSource pds;
    
    @BeforeClass
    public static void beforeClass() {
    	pds = TestUtil.setupPoolingDataSource();
    }
    
    @AfterClass
    public static void afterClass() {
    	pds.close();
    }
}