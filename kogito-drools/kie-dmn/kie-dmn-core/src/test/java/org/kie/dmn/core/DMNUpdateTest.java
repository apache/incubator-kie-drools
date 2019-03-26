package org.kie.dmn.core;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class DMNUpdateTest extends BaseInterpretedVsCompiledTestCanonicalKieModule {

    public static final Logger LOG = LoggerFactory.getLogger(DMNUpdateTest.class);

    public DMNUpdateTest(final boolean useExecModelCompiler, boolean canonicalKieModule) {
        super(useExecModelCompiler, canonicalKieModule);
    }

    @Test
    public void testRemoveAndAddSomething() {
        final KieServices ks = KieServices.Factory.get();

        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test", "1.0.0"),
                wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass())));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Full Name", "John Doe");

        final DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        assertThat(evaluateAll.getDecisionResultByName("Greeting Message").getResult(), is("Hello John Doe"));

        final ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        KieHelper.createAndDeployJar(ks,
                                     v101,
                                     wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass())));

        final Results updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext2 = runtime.newContext();
        dmnContext2.set("Full Name", "John Doe");

        final DMNResult evaluateAll2 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext2);
        assertThat(evaluateAll2.getDecisionResultByName("Greeting Message").getResult(), is("Salve John Doe"));
    }

    @Test
    public void testReplace() {
        final KieServices ks = KieServices.Factory.get();

        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test", "1.0.0"),
                wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass())));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Full Name", "John Doe");

        final DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        assertThat(evaluateAll.getDecisionResultByName("Greeting Message").getResult(), is("Hello John Doe"));

        final ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        final Resource newClassPathResource = ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass());
        newClassPathResource.setTargetPath("0001-input-data-string.dmn");
        KieHelper.createAndDeployJar(ks,
                                     v101,
                                     wrapWithDroolsModelResource(ks, newClassPathResource));

        final Results updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext2 = runtime.newContext();
        dmnContext2.set("Full Name", "John Doe");

        final DMNResult evaluateAll2 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext2);
        assertThat(evaluateAll2.getDecisionResultByName("Greeting Message").getResult(), is("Salve John Doe"));
    }

    @Test
    public void testReplaceDisposeCreateReplace() {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        KieContainer kieContainer = KieHelper.getKieContainer(
                v100,
                wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass())));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Full Name", "John Doe");

        final DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        assertThat(evaluateAll.getDecisionResultByName("Greeting Message").getResult(), is("Hello John Doe"));

        final ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        final Resource newClassPathResource = ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass());
        newClassPathResource.setTargetPath("0001-input-data-string.dmn");
        KieHelper.createAndDeployJar(ks,
                                     v101,
                                     wrapWithDroolsModelResource(ks, newClassPathResource));

        Results updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext2 = runtime.newContext();
        dmnContext2.set("Full Name", "John Doe");

        final DMNResult evaluateAll2 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext2);
        assertThat(evaluateAll2.getDecisionResultByName("Greeting Message").getResult(), is("Salve John Doe"));

        kieContainer.dispose();

        kieContainer = ks.newKieContainer(v100);
        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext3 = runtime.newContext();
        dmnContext3.set("Full Name", "John Doe");

        final DMNResult evaluateAll3 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext3);
        assertThat(evaluateAll3.getDecisionResultByName("Greeting Message").getResult(), is("Hello John Doe"));

        updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext4 = runtime.newContext();
        dmnContext4.set("Full Name", "John Doe");

        final DMNResult evaluateAll4 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext4);
        assertThat(evaluateAll4.getDecisionResultByName("Greeting Message").getResult(), is("Salve John Doe"));
    }

    @Test
    public void testFromClonedKiePackage() {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        KieHelper.createAndDeployJar(ks,
                                     v100,
                                     wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass())));

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

    @Test
    public void testFromClonedKiePackageThenUpgrade() {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        KieHelper.createAndDeployJar(ks,
                                     v100,
                                     wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass())));

        KieContainer kieContainer = ks.newKieContainer(v100);

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        kieContainer.dispose();

        kieContainer = ks.newKieContainer(v100);

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        final Resource newClassPathResource = ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass());
        newClassPathResource.setTargetPath("0001-input-data-string.dmn");
        KieHelper.createAndDeployJar(ks,
                                     v101,
                                     wrapWithDroolsModelResource(ks, newClassPathResource));

        final Results updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
    }
}
