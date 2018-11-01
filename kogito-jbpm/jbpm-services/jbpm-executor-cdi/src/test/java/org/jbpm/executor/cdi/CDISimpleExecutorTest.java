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

package org.jbpm.executor.cdi;

import javax.inject.Inject;
import javax.persistence.Persistence;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.executor.BasicExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.kie.api.executor.ExecutorService;

@RunWith(Arquillian.class)
public class CDISimpleExecutorTest extends BasicExecutorBaseTest {

    @Deployment()
    public static Archive<?> createDeployment() {
    	// setup data source as part of the deployment as it requires to be already active while boostraping archive
    	pds = ExecutorTestUtil.setupPoolingDataSource();
        return ShrinkWrap.create(JavaArchive.class, "executor-cdi-service.jar")
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.jbpm.executor")                
                .addPackage("org.jbpm.executor.impl")
                .addPackage("org.jbpm.executor.impl.jpa")
                .addPackage("org.jbpm.executor.impl.mem")
                .addPackage("org.jbpm.executor.entities")
                .addPackage("org.jbpm.executor.commands")
                .addPackage("org.jbpm.executor.cdi")
                .addPackage("org.jbpm.executor.cdi.commands")
                .addPackage("org.jbpm.executor.cdi.impl")
                .addPackage("org.jbpm.executor.cdi.impl.jpa")
                .addPackage("org.jbpm.executor.cdi.impl.mem")
                .addPackage("org.jbpm.executor.cdi.impl.runtime")
                
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Executor-orm.xml", ArchivePaths.create("Executor-orm.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
        		.addAsManifestResource("META-INF/javax.enterprise.inject.spi.Extension", 
        				"services/javax.enterprise.inject.spi.Extension");

    }

    private static PoolingDataSource pds;
    
    @BeforeClass
    public static void beforeClass() {

    }
    
    @AfterClass
    public static void afterClass() {
    	pds.close();
    }
    
    @Before
    public void setup() {
    	emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
    }
    
    @Inject
    public void setExecutorService(ExecutorService executorService) {
    	super.executorService = executorService;
    }
}