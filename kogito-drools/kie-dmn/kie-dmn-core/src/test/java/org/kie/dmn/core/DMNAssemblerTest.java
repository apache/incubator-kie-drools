package org.kie.dmn.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNAssemblerTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNAssemblerTest.class);

    @Test
    public void testDuplicateModel() {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        
        kfs.write(ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));
        kfs.write(ks.getResources().newClassPathResource("duplicate.0001-input-data-string.dmn", this.getClass()));
        
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        
        LOG.info("buildAll() completed.");
        results.getMessages(Level.ERROR).forEach( e -> LOG.error("{}", e));
        
        assertTrue( results.getMessages(Level.ERROR).size() > 0 );
    }
}
