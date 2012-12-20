package org.drools.scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.builder.ReleaseId;
import org.kie.KieServices;
import org.kie.builder.impl.InternalKieModule;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class KieModuleMetaDataTest extends AbstractKieCiTest {

    @Test @Ignore
    public void testKieModuleMetaData() throws Exception {
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(releaseId);
        checkDroolsCoreDep(kieModuleMetaData);
    }

    @Test @Ignore
    public void testKieModuleMetaDataInMemoryWithJavaClass() throws Exception {
        testKieModuleMetaDataInMemory(false);
    }

    @Test @Ignore
    public void testKieModuleMetaDataInMemoryWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemory(true);
    }

    private void testKieModuleMetaDataInMemory(boolean useTypeDeclaration) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId dependency = ks.newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        ReleaseId releaseId = ks.newReleaseId("org.kie", "metadata-test", "1.0-SNAPSHOT");

        InternalKieModule kieModule = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7, dependency);
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        checkDroolsCoreDep(kieModuleMetaData);

        Collection<String> testClasses = kieModuleMetaData.getClasses("org.kie.test");
        assertEquals(1, testClasses.size());
        assertEquals("Bean", testClasses.iterator().next());
        Class<?> beanClass = kieModuleMetaData.getClass("org.kie.test", "Bean");
        assertNotNull(beanClass.getMethod("getValue"));
    }

    private void checkDroolsCoreDep(KieModuleMetaData kieModuleMetaData) {
        assertEquals(17, kieModuleMetaData.getClasses("org.drools.runtime").size());
        Class<?> statefulKnowledgeSessionClass = kieModuleMetaData.getClass("org.drools.runtime", "StatefulKnowledgeSession");
        assertTrue(statefulKnowledgeSessionClass.isInterface());
        assertEquals(2, statefulKnowledgeSessionClass.getDeclaredMethods().length);
    }
}