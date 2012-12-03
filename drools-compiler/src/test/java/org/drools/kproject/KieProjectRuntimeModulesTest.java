package org.drools.kproject;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.drools.impl.KnowledgeBaseImpl;
import org.drools.reteoo.ReteooRuleBase;
import org.junit.Test;
import org.kie.KieBase;
import org.kie.builder.GAV;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieModuleModel;
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
        KieModuleModel kProjModel1 = createKieModule( "jar1",
                                                       true );
        KieModuleModel kProjModel2 = createKieModule( "jar2",
                                                       true );
        KieModuleModel kProjModel3 = createKieModule( "jar3",
                                                       true );
        KieModuleModel kProjModel4 = createKieModule( "fol4",
                                                       false );

        GAV gav1 = KieFactory.Factory.get().newGav( "jar1",
                                                    "art1",
                                                    "1.0-SNAPSHOT" );
        GAV gav2 = KieFactory.Factory.get().newGav( "jar2",
                                                    "art1",
                                                    "1.0-SNAPSHOT" );
        GAV gav3 = KieFactory.Factory.get().newGav( "jar3",
                                                    "art1",
                                                    "1.0-SNAPSHOT" );
        GAV gav4 = KieFactory.Factory.get().newGav( "fol4",
                                                    "art1",
                                                    "1.0-SNAPSHOT" );

        java.io.File file1 = fileManager.newFile( "jar1.jar" );
        java.io.File file2 = fileManager.newFile( "jar2.jar" );
        java.io.File file3 = fileManager.newFile( "jar3.jar" );
        java.io.File fol4 = fileManager.newFile( "fol4" );

        ZipKieModule mod1 = new ZipKieModule( gav1,
                                              kProjModel1,
                                              file1 );
        ZipKieModule mod2 = new ZipKieModule( gav2,
                                              kProjModel2,
                                              file2 );
        ZipKieModule mod3 = new ZipKieModule( gav3,
                                              kProjModel3,
                                              file3 );
        FileKieModule mod4 = new FileKieModule( gav4,
                                                kProjModel4,
                                                fol4 );

        Map<GAV, InternalKieModule> deps = new HashMap<GAV, InternalKieModule>();
        deps.put( gav2,
                  mod2 );
        deps.put( gav3,
                  mod3 );
        deps.put( gav4,
                  mod4 );
        mod1.setDependencies( deps );

        KieContainer kContainer = new KieContainerImpl( mod1,
                                                        null );

        KieBase kBase = kContainer.getKieBase( "jar1.KBase1" );
        ClassLoader cl = ((ReteooRuleBase) ((KnowledgeBaseImpl) kBase).getRuleBase()).getRootClassLoader();

        Class cls = cl.loadClass( "org.drools.cdi.test.KProjectTestClassjar1" );
        assertNotNull( cls );
        cls = cl.loadClass( "org.drools.cdi.test.KProjectTestClassjar2" );
        assertNotNull( cls );
        cls = cl.loadClass( "org.drools.cdi.test.KProjectTestClassjar3" );
        assertNotNull( cls );

        testEntry( new KProjectTestClassImpl( "jar1",
                                              kContainer ),
                   "jar1" );
        testEntry( new KProjectTestClassImpl( "jar2",
                                              kContainer ),
                   "jar2" );
        testEntry( new KProjectTestClassImpl( "jar3",
                                              kContainer ),
                   "jar3" );
        testEntry( new KProjectTestClassImpl( "fol4",
                                              kContainer ),
                   "fol4" );

    }

}
