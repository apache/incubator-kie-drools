package org.drools.scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.builder.GAV;
import org.kie.builder.KieFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class KieModuleMetaDataTest {

    @Test @Ignore
    public void testKScanner() throws Exception {
        KieFactory kf = KieFactory.Factory.get();
        GAV gav = kf.newGav("org.drools", "drools-core", "5.5.0.Final");
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(gav);

        assertEquals(17, kieModuleMetaData.getClasses("org.drools.runtime").size());

        Class<?> statefulKnowledgeSessionClass = kieModuleMetaData.getClass("org.drools.runtime", "StatefulKnowledgeSession");
        assertTrue(statefulKnowledgeSessionClass.isInterface());
        assertEquals(2, statefulKnowledgeSessionClass.getDeclaredMethods().length);
    }
}