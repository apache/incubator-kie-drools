package org.kie.dmn.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

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

    @Test
    public void testExtendedMode() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "strictMode.dmn", this.getClass() );
        DMNModel model = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_ecf4ea54-2abc-4e2f-a101-4fe14e356a46", "strictMode" );
        DMNContext ctx = runtime.newContext();
        ctx.set( "timestring", "2016-12-20T14:30:22z" );
        DMNResult result = runtime.evaluateAll(model, ctx);
        assertEquals( DateTimeFormatter.ISO_TIME.parse( "14:30:22z", OffsetTime::from ), result.getDecisionResultByName( "time" ).getResult() );
    }

    @Test
    public void testStrictMode() {
        System.setProperty("org.kie.dmn.strictConformance", "true");
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "strictMode.dmn", this.getClass() );
        DMNModel model = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_ecf4ea54-2abc-4e2f-a101-4fe14e356a46", "strictMode" );
        DMNContext ctx = runtime.newContext();
        ctx.set( "timestring", "2016-12-20T14:30:22z" );
        DMNResult result = runtime.evaluateAll(model, ctx);
        assertNull( result.getDecisionResultByName("time").getResult() );
    }

    @Test
    public void testStrictModeProp() {
        final KieServices services = KieServices.Factory.get();
        final KieFileSystem fileSystem = services.newKieFileSystem();
        KieModuleModel moduleModel = services.newKieModuleModel();
        moduleModel.setConfigurationProperty("org.kie.dmn.strictConformance", "true");
        fileSystem.writeKModuleXML(moduleModel.toXML());
        fileSystem.write(services.getResources().newClassPathResource("strictMode.dmn", this.getClass()));
        services.newKieBuilder(fileSystem).buildAll();
        final KieContainer container = services.newKieContainer(services.getRepository().getDefaultReleaseId());
        DMNRuntime runtime = container.newKieSession().getKieRuntime(DMNRuntime.class);
        DMNModel model = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_ecf4ea54-2abc-4e2f-a101-4fe14e356a46", "strictMode" );
        DMNContext ctx = runtime.newContext();
        ctx.set( "timestring", "2016-12-20T14:30:22z" );
        DMNResult result = runtime.evaluateAll(model, ctx);
        assertNull( result.getDecisionResultByName("time").getResult() );
    }

    @After
    public void clearSystemProperty() {
        System.clearProperty("org.kie.dmn.strictConformance");
    }
}
