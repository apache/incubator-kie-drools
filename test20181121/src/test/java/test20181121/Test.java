package test20181121;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Assert;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.KieHelper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class Test {

//    @org.junit.Test
//    public void test2() {
//        KieServices kieServices = KieServices.Factory.get();
//
//        KieContainer kieContainer = kieServices.newKieContainer(kieServices.newReleaseId("dmn", "car-damage-responsibility", "1.2"));
//
//        DMNRuntime dmnRuntime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
//
//        DMNModel dmnModel = dmnRuntime.getModel("http://www.trisotech.com/dmn/definitions/_73732c1d-f5ff-4219-a705-f551a5161f88", "Bank monthly fees");
//    }

    @org.junit.Test
    public void testSolutionCase1() {
        DMNRuntime runtime = createRuntime("META-INF/decision-1.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_73732c1d-f5ff-4219-a705-f551a5161f88", "Bank monthly fees");
        assertThat(dmnModel, notNullValue());

    }

    public static DMNRuntime createRuntime(final String resourceName, final Class testClass) {
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test-"+ UUID.randomUUID(), "1.0"),
                ks.getResources().newClassPathResource(resourceName, testClass));

        final DMNRuntime runtime = typeSafeGetKieRuntime(kieContainer);
        Assert.assertNotNull(runtime);
        return runtime;
    }

    public static DMNRuntime typeSafeGetKieRuntime(final KieContainer kieContainer) {
        DMNRuntime dmnRuntime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        ((DMNRuntimeImpl) dmnRuntime).setOption(new RuntimeTypeCheckOption(true));
        return dmnRuntime;
    }

}
