package org.drools.compiler.kproject;

import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KieProjectRuntimeModulesTest extends AbstractKnowledgeTest {

    @Test
    public void createMultpleJarAndFileResources() throws IOException,
                                                  ClassNotFoundException,
                                                  InterruptedException {
        KieModuleModel kProjModel1 = createKieModule( "jar1", true );
        KieModuleModel kProjModel2 = createKieModule( "jar2", true );
        KieModuleModel kProjModel3 = createKieModule( "jar3", true );
        KieModuleModel kProjModel4 = createKieModule( "fol4", false );

        ReleaseId releaseId1 = KieServices.Factory.get().newReleaseId("jar1",
                                                                      "art1",
                                                                      "1.0-SNAPSHOT");
        ReleaseId releaseId2 = KieServices.Factory.get().newReleaseId("jar2",
                                                                       "art1",
                                                                       "1.0-SNAPSHOT");
        ReleaseId releaseId3 = KieServices.Factory.get().newReleaseId("jar3",
                                                                      "art1",
                                                                      "1.0-SNAPSHOT");
        ReleaseId releaseId4 = KieServices.Factory.get().newReleaseId("fol4",
                                                                      "art1",
                                                                      "1.0-SNAPSHOT");

        java.io.File file1 = fileManager.newFile( "jar1-1.0-SNAPSHOT.jar" );
        java.io.File file2 = fileManager.newFile( "jar2-1.0-SNAPSHOT.jar" );
        java.io.File file3 = fileManager.newFile( "jar3-1.0-SNAPSHOT.jar" );
        java.io.File fol4 = fileManager.newFile( "fol4-1.0-SNAPSHOT" );

        ZipKieModule mod1 = new ZipKieModule(releaseId1,
                                              kProjModel1,
                                              file1 );
        ZipKieModule mod2 = new ZipKieModule(releaseId2,
                                              kProjModel2,
                                              file2 );
        ZipKieModule mod3 = new ZipKieModule(releaseId3,
                                              kProjModel3,
                                              file3 );
        FileKieModule mod4 = new FileKieModule(releaseId4,
                                                kProjModel4,
                                                fol4 );

        mod1.addKieDependency( mod2 );
        mod1.addKieDependency( mod3 );
        mod1.addKieDependency( mod4 );

        KieModuleKieProject kProject = new KieModuleKieProject(mod1);
        
        KieContainer kContainer = new KieContainerImpl( kProject,
                                                        null );

        KieBase kBase = kContainer.getKieBase( "jar1.KBase1" );
        ClassLoader cl = ((KnowledgeBaseImpl) kBase).getRootClassLoader();

        Class cls = cl.loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar1" );
        assertNotNull( cls );
        cls = cl.loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar2" );
        assertNotNull( cls );
        cls = cl.loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar3" );
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

    @Test
    public void createModuleAndFindResources() throws IOException,
                                                      ClassNotFoundException,
                                                      InterruptedException {
        createKieModule( "fol4", false );
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("fol4", "art1", "1.0-SNAPSHOT");

        KieContainer kieContainer = KieServices.Factory.get().newKieContainer(releaseId);
        assertNotNull(kieContainer);

        InputStream is = kieContainer.getClassLoader().getResourceAsStream("/META-INF/beans.xml");
        assertNotNull(is);
        byte[] bytesFromStream = readBytesFromInputStream(is);

        Enumeration<URL> foundResources = kieContainer.getClassLoader().getResources("/META-INF/beans.xml");
        assertNotNull(foundResources);

        List<URL> resourcesAsList = Collections.list(foundResources);
        assertNotNull(resourcesAsList);
        assertEquals(1, resourcesAsList.size());

        URL resourceUrl = resourcesAsList.get(0);
        byte[] bytesFromURL = readBytesFromInputStream(resourceUrl.openStream());
        assertTrue(Arrays.equals(bytesFromStream, bytesFromURL));
    }
}
