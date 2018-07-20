package org.kie.pmml.pmml_4_2.model;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.pmml.pmml_4_2.model.ExternalBeanRef.ModelUsage;

public class ExternalBeanRefTest {

    private static final String pkgName = "org.drools.scorecard.example";
    private static final String clsName = "Attribute";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    

    @Test
    public void testValidExternalRef() {
        ExternalBeanRef ref;
        try {
            ref = new ExternalBeanRef("attribute", "org.drools.scorecard.example.Attribute", ModelUsage.MINING);
            assertNotNull(ref);
            assertEquals(pkgName, ref.getBeanPackageName());
            assertEquals(clsName, ref.getBeanName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testExternalRefMissingPackage() {
        ExternalBeanRef ref;
        try {
            ref = new ExternalBeanRef("attribute", "Attribute", ModelUsage.MINING);
            assertNotNull(ref);
            assertEquals(ExternalBeanDefinition.DEFAULT_BEAN_PKG, ref.getBeanPackageName());
            assertEquals(clsName, ref.getBeanName());
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testExternalRefEmptyName() throws Exception {
        ExternalBeanRef ref;
        thrown.expect(java.lang.IllegalArgumentException.class);
        thrown.expectMessage("Unable to construct ExternalBeanRef.");
        ref = new ExternalBeanRef("attribute", "", ModelUsage.MINING);
        fail("Expected an Exception due to empty bean information string");
    }
}
