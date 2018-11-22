package test20181121;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;

public class Test {

    @org.junit.Test
    public void test() {
        KieServices kieServices = KieServices.Factory.get();

        KieContainer kieContainer = kieServices.newKieContainer(kieServices.newReleaseId("dmn", "car-damage-responsibility", "1.2"));

        DMNRuntime dmnRuntime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);

        DMNModel dmnModel = dmnRuntime.getModel("http://www.trisotech.com/dmn/definitions/_73732c1d-f5ff-4219-a705-f551a5161f88", "Bank monthly fees");
    }
}
