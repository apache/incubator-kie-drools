/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 */

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
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
}