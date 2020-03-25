package org.kie.dmn.typesafe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.typesafe.compilation.RegressionCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DMNTypeSafeTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNTypeSafeTest.class);
    private String PACKAGE_NAME = "http_58_47_47www_46trisotech_46com_47definitions_47_2ceee5b6_450f0d_4541ef_45890e_452cd6fb1adb10";


        @Test
    public void test() throws Exception {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("a.dmn", this.getClass());
        String namespace = "http://www.trisotech.com/definitions/_2ceee5b6-0f0d-41ef-890e-2cd6fb1adb10";
        String modelName = "Drawing 1";

        final DMNModel dmnModel = runtime.getModel(namespace, modelName);
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNTypeSafeTypeGenerator sourceCode = new DMNTypeSafeTypeGenerator(dmnModel, PACKAGE_NAME);

        Map<String, String> allTypesSourceCode = sourceCode.generateSourceCodeOfAllTypes();

        ClassLoader thisDMNClassLoader = this.getClass().getClassLoader();
        Map<String, Class<?>> compiledClasses = RegressionCompiler.compile(allTypesSourceCode, thisDMNClassLoader);

        FEELPropertyAccessible viaVigorelli = createTAddress(compiledClasses, "Via Vigorelli", 1);
        FEELPropertyAccessible viaVerdi = createTAddress(compiledClasses, "Via Verdi", 2);

        FEELPropertyAccessible tPersonInstance = createTPerson(compiledClasses, Arrays.asList(viaVigorelli, viaVerdi));
        FEELPropertyAccessible context = createInputSet(compiledClasses, tPersonInstance);

        DMNResult evaluateAll = runtime.evaluateAll(dmnModel, new DMNContextFPAImpl(context));
        LOG.info("{}", evaluateAll);
    }

    private FEELPropertyAccessible createTAddress(Map<String, Class<?>> compile, String streetName, int streetNumber) throws Exception {
        Class<?> clazz = compile.get(classWithPackage("TAddress"));
        assertThat(clazz, notNullValue());
        Object tPersonInstance = clazz.getDeclaredConstructor().newInstance();
        FEELPropertyAccessible feelPropertyAccessible = (FEELPropertyAccessible) tPersonInstance;
        feelPropertyAccessible.setFEELProperty("streetName", streetName);
        feelPropertyAccessible.setFEELProperty("streetNumber", streetNumber);

        return feelPropertyAccessible;
    }

    private String classWithPackage(String className) {
        return PACKAGE_NAME + "." + className;
    }

    private FEELPropertyAccessible createTPerson(Map<String, Class<?>> compile, List<FEELPropertyAccessible> addresses) throws Exception {
        Class<?> tPersonClass = compile.get(classWithPackage("TPerson"));
        assertThat(tPersonClass, notNullValue());
        Object tPersonInstance = tPersonClass.getDeclaredConstructor().newInstance();
        FEELPropertyAccessible feelPropertyAccessible = (FEELPropertyAccessible) tPersonInstance;
        feelPropertyAccessible.setFEELProperty("name", "Mr. x");
        feelPropertyAccessible.setFEELProperty("addresses", addresses);

        return feelPropertyAccessible;
    }

    private FEELPropertyAccessible createInputSet(Map<String, Class<?>> compile, FEELPropertyAccessible tPersonInstance) throws Exception {
        Class<?> inputSetClass = compile.get(classWithPackage("InputSet"));
        assertThat(inputSetClass, notNullValue());
        Object inputSetInstance = inputSetClass.getDeclaredConstructor().newInstance();
        FEELPropertyAccessible feelPropertyAccessible = (FEELPropertyAccessible) inputSetInstance;

        feelPropertyAccessible.setFEELProperty("p", tPersonInstance);

        return feelPropertyAccessible;
    }
}
