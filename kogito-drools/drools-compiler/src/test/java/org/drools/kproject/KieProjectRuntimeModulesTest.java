package org.drools.kproject;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.builder.GAV;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieProjectModel;
import org.kie.builder.impl.ClasspathKieProject;
import org.kie.builder.impl.FileKieModule;
import org.kie.builder.impl.InternalKieModule;
import org.kie.builder.impl.KieContainerImpl;
import org.kie.builder.impl.ZipKieModule;

public class KieProjectRuntimeModulesTest extends AbstractKnowledgeTest {
    

    @Test
    public void createMultpleJarAndFileResources() throws IOException,
                       ClassNotFoundException,
                       InterruptedException {
        KieProjectModel kProjModel1 = createKieModule( "jar1", true );
        KieProjectModel kProjModel2 = createKieModule( "jar2", true );
        KieProjectModel kProjModel3 = createKieModule( "jar3", true );
        KieProjectModel kProjModel4 = createKieModule( "fol4", false );

        GAV gav1 = KieFactory.Factory.get().newGav( "jar1", "art1", "1.0-SNAPSHOT" );
        GAV gav2 = KieFactory.Factory.get().newGav( "jar2", "art1", "1.0-SNAPSHOT" );
        GAV gav3 = KieFactory.Factory.get().newGav( "jar3", "art1", "1.0-SNAPSHOT" );
        GAV gav4 = KieFactory.Factory.get().newGav( "fol4", "art1", "1.0-SNAPSHOT" );
        
//        ClassLoader origCl = Thread.currentThread().getContextClassLoader();
//        try {
            java.io.File file1 = fileManager.newFile( "jar1.jar" );
            java.io.File file2 = fileManager.newFile( "jar2.jar" );
            java.io.File file3 = fileManager.newFile( "jar3.jar" );
            java.io.File fol4 = fileManager.newFile( "fol4" );
        
//            URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{file1.toURI().toURL(), file2.toURI().toURL(), file3.toURI().toURL(), fol4.toURI().toURL() } );
//            Thread.currentThread().setContextClassLoader( urlClassLoader );
            
//            Class cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar1" );
//            assertNotNull( cls );
//            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar2" );
//            assertNotNull( cls );
//            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar3" );
//            assertNotNull( cls );
            
            ZipKieModule mod1 = new ZipKieModule( gav1, kProjModel1, file1);
            ZipKieModule mod2 = new ZipKieModule( gav2, kProjModel2, file2);
            ZipKieModule mod3 = new ZipKieModule( gav3, kProjModel3, file3);
            FileKieModule mod4 = new FileKieModule( gav4, kProjModel4, fol4);
            
            Map<GAV, InternalKieModule> deps = new HashMap<GAV, InternalKieModule>();
            deps.put( gav2, mod2 );
            deps.put( gav3, mod3 );
            deps.put( gav4, mod4 );            
            mod1.setDependencies( deps );
            
            //mod1.setDependencies( dependencies )
            
            //ClasspathKieProject kProject =  new ClasspathKieProject();
            
            KieContainer kContainer = new KieContainerImpl( mod1, null );
//            new ZipKieModule( gav, kieProject, file );
            
            //KieContainer kContainer = new KieContainerImpl(kProject, null);
            
             testEntry(new KProjectTestClassImpl( "jar1", kContainer ), "jar1");
             testEntry(new KProjectTestClassImpl( "jar2", kContainer ), "jar2");
             testEntry(new KProjectTestClassImpl( "jar3", kContainer ), "jar3");
             testEntry(new KProjectTestClassImpl( "fol4", kContainer ), "fol4");

//        } finally {
//            Thread.currentThread().setContextClassLoader( origCl );
//        }
    }
    
    
}
