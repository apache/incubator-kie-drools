package org.drools.compiler.kie.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.drools.compiler.kie.builder.impl.ClasspathKieProject;
import org.drools.compiler.kproject.AbstractKnowledgeTest;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;

/**
 * Test for kModule in jar (file or directory).
 */
public class JarFileOrDirTest {

    public static AbstractKnowledgeTest helper;

    private static final String MODULE_JARFILE_NAME = "jar1";
    private static final String MODULE_JARFILE_VERSION = "1.0";
    private static final String MODULE_JARDIR_NAME = "jar2";
    private static final String MODULE_JARDIR_VERSION = "2.0";

    @BeforeClass
    public static void beforeClass() {
        helper = new AbstractKnowledgeTest();
        try {
            helper.setUp();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        try {
            helper.createKieModule(MODULE_JARFILE_NAME, true, MODULE_JARFILE_VERSION);
            helper.createKieModule(MODULE_JARDIR_NAME, false, MODULE_JARDIR_VERSION);
            final File kModuleDir = helper.getFileManager().newFile(MODULE_JARDIR_NAME + "-" + MODULE_JARDIR_VERSION);
            assertNotNull(kModuleDir);
            assertTrue(kModuleDir.isDirectory());
            kModuleDir.renameTo(new File(kModuleDir.getAbsolutePath() + ".jar"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to build dynamic KieModules:\n" + e.toString());
        }

    }

    @AfterClass
    public static void afterClass() {
        try {
            helper.tearDown();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testkModuleJarFile() {
        final File kModuleFile = helper.getFileManager()
                .newFile(MODULE_JARFILE_NAME + "-" + MODULE_JARFILE_VERSION + ".jar");
        final String pomProperties = ClasspathKieProject.getPomProperties(kModuleFile.getAbsolutePath());
        checkPomProperties(pomProperties, MODULE_JARFILE_NAME, MODULE_JARFILE_VERSION);
    }

    @Test
    public void testkModuleJarDir() {
        final File kModuleFile = helper.getFileManager()
                .newFile(MODULE_JARDIR_NAME + "-" + MODULE_JARDIR_VERSION + ".jar");
        final String pomProperties = ClasspathKieProject.getPomProperties(kModuleFile.getAbsolutePath());
        checkPomProperties(pomProperties, MODULE_JARDIR_NAME, MODULE_JARDIR_VERSION);
    }

    private void checkPomProperties(final String pomProperties, final String groupId, final String version) {
        assertNotNull(pomProperties);
        final ReleaseId release = ReleaseIdImpl.fromPropertiesString(pomProperties);
        assertNotNull(release);
        assertTrue(groupId.equals(release.getGroupId()));
        assertTrue(version.equals(release.getVersion()));
    }

}
