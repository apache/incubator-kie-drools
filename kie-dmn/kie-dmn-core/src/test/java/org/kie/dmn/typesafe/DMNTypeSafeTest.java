package org.kie.dmn.typesafe;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.typesafe.compilation.RegressionCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DMNTypeSafeTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNTypeSafeTest.class);

    @Test
    public void test() throws Exception {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("a.dmn", this.getClass());
        String namespace = "http://www.trisotech.com/definitions/_2ceee5b6-0f0d-41ef-890e-2cd6fb1adb10";
        String modelName = "Drawing 1";

        final DMNModel dmnModel = runtime.getModel(namespace, modelName);
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        String sourceCode = new DMNInputSetGenerator(dmnModel).getType("tPerson");

        Map<String, String> sourceCodes = Collections.singletonMap("org.kie.dmn.typesafe.TPerson", sourceCode);

        ClassLoader thisDMNClassLoader = this.getClass().getClassLoader();
        RegressionCompiler.compile(sourceCodes, thisDMNClassLoader);

        Class<?> aClass = thisDMNClassLoader.loadClass("org.kie.dmn.typesafe.TPerson");
        assertThat(aClass, notNullValue());

        Method getName = aClass.getMethod("getName");
        assertThat(getName, notNullValue());



//        InputSet context = new InputSet();
//        Person pojo = new Person();
//        pojo.setName("Mr. x");
//        context.setP(pojo);
//
//        DMNResult evaluateAll = dmnRuntime.evaluateAll(dmnModel, new DMNContextFPAImpl(context));
//        LOG.info("{}", evaluateAll);
    }
}
