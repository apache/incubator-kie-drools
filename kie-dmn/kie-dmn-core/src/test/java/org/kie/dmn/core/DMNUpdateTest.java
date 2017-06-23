package org.kie.dmn.core;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.KieHelper;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNUpdateTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNUpdateTest.class);

    @Test
    public void testRemoveAndAddSomething() {
        final KieServices ks = KieServices.Factory.get();
        
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test", "1.0.0"),
                ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
        
        DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Full Name", "John Doe");
        
        DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        assertThat( evaluateAll.getDecisionResultByName("Greeting Message").getResult(), is( "Hello John Doe" ) );
        
        ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        KieHelper.createAndDeployJar(ks,
                v101,
                ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass()));
        
        kieContainer.updateToVersion(v101);
        
        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
        
        DMNContext dmnContext2 = runtime.newContext();
        dmnContext2.set("Full Name", "John Doe");
        
        DMNResult evaluateAll2 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext2);
        assertThat( evaluateAll2.getDecisionResultByName("Greeting Message").getResult(), is( "Salve John Doe" ) );
    }
    
    @Test
    public void testReplace() {
        final KieServices ks = KieServices.Factory.get();
        
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test", "1.0.0"),
                ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
        
        DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Full Name", "John Doe");
        
        DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        assertThat( evaluateAll.getDecisionResultByName("Greeting Message").getResult(), is( "Hello John Doe" ) );
        
        ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        Resource newClassPathResource = ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass());
        newClassPathResource.setTargetPath("0001-input-data-string.dmn");
        KieHelper.createAndDeployJar(ks,
                v101,
                newClassPathResource);
        
        kieContainer.updateToVersion(v101);
        
        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
            
        DMNContext dmnContext2 = runtime.newContext();
        dmnContext2.set("Full Name", "John Doe");
        
        DMNResult evaluateAll2 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext2);
        assertThat( evaluateAll2.getDecisionResultByName("Greeting Message").getResult(), is( "Salve John Doe" ) );
    }
    
    @Test
    public void testFromClonedKiePackage() {
        final KieServices ks = KieServices.Factory.get();
        
        ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        KieHelper.createAndDeployJar(ks,
                v100,
                ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        KieContainer kieContainer = ks.newKieContainer(v100);
        
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
        
        kieContainer.dispose();
        
        kieContainer = ks.newKieContainer(v100);
        
        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
    }
}
