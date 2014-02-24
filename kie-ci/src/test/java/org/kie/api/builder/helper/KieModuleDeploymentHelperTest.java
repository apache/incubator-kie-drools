package org.kie.api.builder.helper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.drools.core.impl.EnvironmentImpl;
import org.junit.After;
import org.junit.Test;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.helper.FluentKieModuleDeploymentHelper;
import org.kie.api.builder.helper.KieModuleDeploymentHelper;
import org.kie.api.builder.helper.SingleKieModuleDeploymentHelper;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.scanner.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieModuleDeploymentHelperTest {

    protected static Logger logger = LoggerFactory.getLogger(KieModuleDeploymentHelperTest.class);
    
    private ZipInputStream zip;

    @After
    public void cleanUp() {
        if (zip != null) {
            try {
                zip.close();
            } catch (IOException e) {
                // do nothing
            }

        }
    }

    @Test
    public void testSingleDeploymentHelper() throws Exception {
        int numFiles = 0;
        int numDirs = 0;
        SingleKieModuleDeploymentHelper deploymentHelper = KieModuleDeploymentHelper.newSingleInstance();

        List<String> resourceFilePaths = new ArrayList<String>();
        resourceFilePaths.add("builder/test/");
        numFiles += 2;
        resourceFilePaths.add("builder/simple_query_test.drl");
        ++numFiles;

        List<Class<?>> kjarClasses = new ArrayList<Class<?>>();
        kjarClasses.add(KieModuleDeploymentHelper.class);
        numDirs += 5; // org.kie.api.builder.helper
        kjarClasses.add(EnvironmentImpl.class);
        numDirs += 3; // (org.)drools.core.impl
        kjarClasses.add(org.drools.compiler.Cheese.class);
        numDirs += 1; // (org.drools.)compiler
        numFiles += 3;

        String groupId = "org.kie.api.builder";
        String artifactId = "test-kjar";
        String version = "0.1-SNAPSHOT";
        deploymentHelper.createKieJarAndDeployToMaven(groupId, artifactId, version, 
                "defaultKieBase", "defaultKieSession",
                resourceFilePaths, kjarClasses);
        // pom.xml, pom.properties
        numFiles += 2;
        // kmodule.xml, kmodule.info, kbase.cache
        numFiles +=3;
        
        // META-INF/maven/org.kie.api.builder/test-kjar
        numDirs += 4;
        // defaultKiebase, META-INF/defaultKieBase
        numDirs += 2;

        File artifactFile = MavenRepository.getMavenRepository().resolveArtifact(groupId + ":" + artifactId + ":" + version).getFile();
        zip = new ZipInputStream(new FileInputStream(artifactFile));

        Set<String> jarFiles = new HashSet<String>();
        Set<String> jarDirs = new HashSet<String>();
        ZipEntry ze = zip.getNextEntry();
        logger.debug("Getting files from deployed jar: " );
        while( ze != null ) { 
            String fileName = ze.getName();
            if( fileName.endsWith("drl")
                    || fileName.endsWith("class")
                    || fileName.endsWith("xml")
                    || fileName.endsWith("info")
                    || fileName.endsWith("properties")
                    || fileName.endsWith("cache") ) { 
                jarFiles.add(fileName);
                logger.debug("> " + fileName);
            } else { 
                jarDirs.add(fileName);
                logger.debug("] " + fileName);
            }
            ze = zip.getNextEntry();
        }
        assertEquals("Num files in kjar", numFiles, jarFiles.size());
        assertEquals("Num dirs in kjar", numDirs, jarDirs.size());
        System.out.println( "------------------------------------");
    }

    @Test
    public void testFluentDeploymentHelper() throws Exception {
        int numFiles = 0;
        int numDirs = 0;
        String content = "test file created by " + this.getClass().getSimpleName();
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tst");
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(content.getBytes());
        fos.close();
        
        FluentKieModuleDeploymentHelper deploymentHelper = KieModuleDeploymentHelper.newFluentInstance();

        String groupId = "org.kie.api.builder.fluent";
        String artifactId = "test-kjar";
        String version = "0.1-SNAPSHOT";
        deploymentHelper = deploymentHelper.setGroupId(groupId)
                .setArtifactId(artifactId)
                .setVersion(version)
                .addResourceFilePath("builder/test/", "builder/simple_query_test.drl")
                .addResourceFilePath(tempFile.getAbsolutePath())
                .addResourceFilePath("/META-INF/WorkDefinitions.conf") // from the drools-core jar
                .addClass(KieModuleDeploymentHelperTest.class)
                .addClass(KieModule.class)
                .addClass(org.drools.compiler.Cheese.class);
        // class dirs
        numDirs += 5; // org.kie.api.builder.helper
        numDirs += 2; // (org.)drools.compiler
        
        // pom.xml, pom.properties
        numFiles += 3;
        // kmodule.xml, kmodule.info
        numFiles += 2;
        // kbase.cache x 2
        numFiles += 2;
        // drl files
        numFiles += 3;
        // WorkDefinitions
        ++numFiles;
        // classes
        numFiles += 3;
        
        // META-INF/maven/org.kie.api.builder/test-kjar
        numDirs += 4;
        // defaultKiebase, META-INF/defaultKieBase
        numDirs += 2;
        
        KieBaseModel kbaseModel = deploymentHelper.getKieModuleModel().newKieBaseModel("otherKieBase");
        kbaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY).setEventProcessingMode(EventProcessingOption.STREAM);
        kbaseModel.newKieSessionModel("otherKieSession").setClockType(ClockTypeOption.get("realtime"));
        // META-INF/otherKieBase
        ++numDirs;

        deploymentHelper.getKieModuleModel().getKieBaseModels().get("defaultKieBase").newKieSessionModel("secondKieSession");

        deploymentHelper.createKieJarAndDeployToMaven();

        File artifactFile = MavenRepository.getMavenRepository().resolveArtifact(groupId + ":" + artifactId + ":" + version).getFile();
        zip = new ZipInputStream(new FileInputStream(artifactFile));

        Set<String> jarFiles = new HashSet<String>();
        Set<String> jarDirs = new HashSet<String>();
        ZipEntry ze = zip.getNextEntry();
        logger.debug("Getting files form deployed jar: ");
        while( ze != null ) { 
            String fileName = ze.getName();
            if( fileName.endsWith("drl")
                    || fileName.endsWith("class")
                    || fileName.endsWith("tst")
                    || fileName.endsWith("conf")
                    || fileName.endsWith("xml")
                    || fileName.endsWith("info")
                    || fileName.endsWith("properties")
                    || fileName.endsWith("cache") ) { 
                jarFiles.add(fileName);
                logger.debug("> " + fileName);
            } else { 
                jarDirs.add(fileName);
                logger.debug("] " + fileName);
            }
            ze = zip.getNextEntry();
        }
        assertEquals("Num files in kjar", numFiles, jarFiles.size());
        assertEquals("Num dirs in kjar", numDirs, jarDirs.size());
    }
}
