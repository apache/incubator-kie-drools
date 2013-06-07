package org.drools.decisiontable.integrationtests;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Basic tests for creation of a KieBase from CSV and XLS resources.
 */
public class KModuleWithDecisionTablesTest {

    @Test
    public void testNonEmptyKieBaseWithXLS() throws Exception {
       testNonEmptyKieBase("kbaseXLS");
    }
    
    @Test
    public void testNonEmptyKieBaseWithCSV() throws Exception {
       testNonEmptyKieBase("kbaseCSV");
    }

    private void testNonEmptyKieBase(final String kieBaseName) throws Exception {
       KieServices ks = KieServices.Factory.get();
       KieContainer kContainer = ks.getKieClasspathContainer();

       KieBase kieBase = kContainer.getKieBase(kieBaseName);

       assertNotNull("KieBase not found", kieBase);
       assertEquals("Unexpected number of KiePackages in KieBase", 
               1, kieBase.getKiePackages().size());
    }
    
}
