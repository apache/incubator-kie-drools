package org.kie.dmn.core;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DMNTypeSafeTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNTypeSafeTest.class);

    @Test
    public void test() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("a.dmn", this.getClass() );
        String namespace = "http://www.trisotech.com/definitions/_2ceee5b6-0f0d-41ef-890e-2cd6fb1adb10";
        String modelName = "Drawing 1";

        final DMNModel dmnModel = runtime.getModel(namespace, modelName);
        assertThat( dmnModel, notNullValue() );
        assertThat( DMNRuntimeUtil.formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );



//        InputSet context = new InputSet();
//        Person pojo = new Person();
//        pojo.setName("Mr. x");
//        context.setP(pojo);
//
//        DMNResult evaluateAll = dmnRuntime.evaluateAll(dmnModel, new DMNContextFPAImpl(context));
//        LOG.info("{}", evaluateAll);
    }
}
