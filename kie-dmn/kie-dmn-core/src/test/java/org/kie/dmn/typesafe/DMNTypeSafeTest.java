package org.kie.dmn.typesafe;

import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.core.impl.FEELPropertyAccessible;
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

        DMNInputSetGenerator sourceCode = new DMNInputSetGenerator(dmnModel, "org.kie.dmn.typesafe");

        Map<String, String> allTypesSourceCode = sourceCode.generateSourceCodeOfAllTypes();

        ClassLoader thisDMNClassLoader = this.getClass().getClassLoader();
        Map<String, Class<?>> compiledClasses = RegressionCompiler.compile(allTypesSourceCode, thisDMNClassLoader);

        FEELPropertyAccessible tPersonInstance = createTPerson(compiledClasses);
        FEELPropertyAccessible context = createInputSet(compiledClasses, tPersonInstance);

        DMNResult evaluateAll = runtime.evaluateAll(dmnModel, new DMNContextFPAImpl(context));
        LOG.info("{}", evaluateAll);
    }

    private FEELPropertyAccessible createTPerson(Map<String, Class<?>> compile) throws Exception {
        Class<?> tPersonClass = compile.get("org.kie.dmn.typesafe.TPerson");
        assertThat(tPersonClass, notNullValue());
        Object tPersonInstance = tPersonClass.getDeclaredConstructor().newInstance();
        FEELPropertyAccessible feelPropertyAccessible = (FEELPropertyAccessible) tPersonInstance;
        feelPropertyAccessible.setFEELProperty("name", "Mr. x");

        return feelPropertyAccessible;
    }

    private FEELPropertyAccessible createInputSet(Map<String, Class<?>> compile, FEELPropertyAccessible tPersonInstance) throws Exception {
        Class<?> inputSetClass = compile.get("org.kie.dmn.typesafe.InputSet");
        assertThat(inputSetClass, notNullValue());
        Object inputSetInstance = inputSetClass.getDeclaredConstructor().newInstance();
        FEELPropertyAccessible feelPropertyAccessible = (FEELPropertyAccessible) inputSetInstance;

        feelPropertyAccessible.setFEELProperty("p", tPersonInstance);

        return feelPropertyAccessible;
    }
}
