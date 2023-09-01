package org.kie.dmn.core;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNAndCanonicalModelTest extends BaseInterpretedVsCompiledTestCanonicalKieModule {

    public static final Logger LOG = LoggerFactory.getLogger(DMNAndCanonicalModelTest.class);

    public DMNAndCanonicalModelTest(final boolean useExecModelCompiler, boolean canonicalKieModule) {
        super(useExecModelCompiler, canonicalKieModule);
    }

    @Test
    public void testDMNAndCanonicalModel() {
        final KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-and-canonical", "1.0.0");
        final KieContainer kieContainer = KieHelper.getKieContainer(releaseId,
                                                                    wrapWithDroolsModelResource(ks,
                                                                                                releaseId,
                                                                                                ks.getResources().newClassPathResource("0001-input-data-string.dmn",
                                                                                                                                       this.getClass())));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertThat(runtime).isNotNull();
        assertThat(runtime.getModels()).hasSize(1);

        final DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Full Name", "John Doe");
        final DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Greeting Message").getResult()).isEqualTo("Hello John Doe");
    }

    @Test
    public void testDTAndCanonicalModel() {
        final KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-and-canonical", "1.0.0");
        final KieContainer kieContainer = KieHelper.getKieContainer(releaseId,
                                                                    wrapWithDroolsModelResource(ks,
                                                                                                releaseId,
                                                                                                ks.getResources().newClassPathResource("decisiontable-default-value.dmn",
                                                                                                                                       this.getClass())));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertThat(runtime).isNotNull();
        assertThat(runtime.getModels()).hasSize(1);

        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("Age", 18);
        dmnContext.set("RiskCategory", "Medium");
        dmnContext.set("isAffordable", true);
        final DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Approval Status").getResult()).isEqualTo("Approved");
    }
}
