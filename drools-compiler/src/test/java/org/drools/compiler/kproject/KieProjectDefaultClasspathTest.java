package org.drools.compiler.kproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.compiler.kie.builder.impl.InternalKieServices;
import org.drools.compiler.kie.builder.impl.event.AbstractKieServicesEventListerner;
import org.drools.compiler.kie.builder.impl.event.KieModuleDiscovered;
import org.drools.compiler.kie.builder.impl.event.KieServicesEventListerner;
import org.junit.Test;
import org.drools.compiler.kie.builder.impl.ClasspathKieProject;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

public class KieProjectDefaultClasspathTest extends AbstractKnowledgeTest {

    private KieServicesEventListerner listener;

    @Test
    public void createMultpleJarAndFileResources() throws IOException,
                       ClassNotFoundException,
                       InterruptedException {
        createKieModule( "jar1", true );
        createKieModule( "jar2", true );
        createKieModule( "jar3", true );
        createKieModule( "fol4", false );

        ClassLoader origCl = Thread.currentThread().getContextClassLoader();
        try {
            java.io.File file1 = fileManager.newFile( "jar1-1.0-SNAPSHOT.jar" );
            java.io.File file2 = fileManager.newFile( "jar2-1.0-SNAPSHOT.jar" );
            java.io.File file3 = fileManager.newFile( "jar3-1.0-SNAPSHOT.jar" );
            java.io.File fol4 = fileManager.newFile( "fol4-1.0-SNAPSHOT" );
        
            URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{file1.toURI().toURL(), file2.toURI().toURL(), file3.toURI().toURL(), fol4.toURI().toURL() } );
            Thread.currentThread().setContextClassLoader( urlClassLoader );


            Class cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar1" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar2" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar3" );
            assertNotNull( cls );

            InternalKieServices ks = (InternalKieServices) KieServices.Factory.get();

            final AtomicInteger kieModulesCounter = new AtomicInteger(0);
            listener = new AbstractKieServicesEventListerner() {
                @Override
                public void onKieModuleDiscovered(KieModuleDiscovered event) {
                    // skip kmodule.xml contained in test/resources
                    if (!event.getKieModuleUrl().contains("test-classes")) {
                        kieModulesCounter.incrementAndGet();
                    }
                }
            };
            ks.registerListener(listener);

            KieContainer kContainer = ks.newKieClasspathContainer();

            assertEquals(4, kieModulesCounter.get());

            testEntry(new KProjectTestClassImpl( "jar1", kContainer ), "jar1");
            testEntry(new KProjectTestClassImpl( "jar2", kContainer ), "jar2");
            testEntry(new KProjectTestClassImpl( "jar3", kContainer ), "jar3");
            testEntry(new KProjectTestClassImpl("fol4", kContainer), "fol4");
        } finally {
            // FIXME Java 7+
            // on Windows, the URLClassLoader will not release all resources,
            // so the attempt to delete the temporary files will fail.
            // an explicit dispose call is needed, but it has not been introduced until Java7+
            // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4950148

            /*
            ((URLClassLoader) Thread.currentThread().getContextClassLoader()).close();
            */

            Thread.currentThread().setContextClassLoader( origCl );
        }
    }
    
}
