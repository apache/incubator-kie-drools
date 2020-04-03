package org.kie.dmn.typesafe;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DMNTypeSafeTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNTypeSafeTest.class);

    private String packageName;
    private DMNModel dmnModel;
    private DMNRuntime runtime;

    @Before
    public void setUp() throws Exception {
        runtime = DMNRuntimeUtil.createRuntime("a.dmn", this.getClass());
        String namespace = "http://www.trisotech.com/definitions/_2ceee5b6-0f0d-41ef-890e-2cd6fb1adb10";
        String modelName = "Drawing 1";

        dmnModel = runtime.getModel(namespace, modelName);
        packageName = DMNClassNamespaceTypeIndex.namespace(dmnModel);
    }

    @Test
    public void test() throws Exception {

        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(dmnModel, new DMNClassNamespaceTypeIndex(dmnModel)).generateSourceCodeOfAllTypes();

        ClassLoader thisDMNClassLoader = this.getClass().getClassLoader();
        Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(allTypesSourceCode, thisDMNClassLoader);

        FEELPropertyAccessible street1 = tAddress(compiledClasses, "Street1", 1);
        FEELPropertyAccessible street2 = tAddress(compiledClasses, "Street2", 2);

        FEELPropertyAccessible tPersonInstance = tPerson(compiledClasses, asList(street1, street2));
        FEELPropertyAccessible context = inputSet(compiledClasses, tPersonInstance);

        DMNResult evaluateAll = evaluateTyped(context, runtime, dmnModel);

        DMNContext result = evaluateAll.getContext();
        Map<String, Object> d = (Map<String, Object>) result.get("d");
        assertThat(d.get("Hello"), is("Hello Mr. x"));
        LOG.info("{}", evaluateAll);
    }

    private FEELPropertyAccessible tAddress(Map<String, Class<?>> compile, String streetName, int streetNumber) throws Exception {
        FEELPropertyAccessible feelPropertyAccessible = createInstanceFromCompiledClasses(compile, packageName, "TAddress");
        feelPropertyAccessible.setFEELProperty("streetName", streetName);
        feelPropertyAccessible.setFEELProperty("streetNumber", streetNumber);

        return feelPropertyAccessible;
    }

    private FEELPropertyAccessible tPerson(Map<String, Class<?>> compile, List<FEELPropertyAccessible> addresses) throws Exception {
        FEELPropertyAccessible feelPropertyAccessible = createInstanceFromCompiledClasses(compile, packageName, "TPerson");
        feelPropertyAccessible.setFEELProperty("name", "Mr. x");
        feelPropertyAccessible.setFEELProperty("addresses", addresses);

        return feelPropertyAccessible;
    }

    private FEELPropertyAccessible inputSet(Map<String, Class<?>> compile, FEELPropertyAccessible tPersonInstance) throws Exception {
        FEELPropertyAccessible feelPropertyAccessible = createInstanceFromCompiledClasses(compile, packageName, "InputSet");
        feelPropertyAccessible.setFEELProperty("p", tPersonInstance);
        return feelPropertyAccessible;
    }

    @Test
    public void testDynamic() throws Exception {

        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        FEELPropertyAccessible context = createInputSet(dmnModel, packageName, this.getClass().getClassLoader());

        Map<String, Object> inputSetMap = new HashMap<>();

        inputSetMap.put("p", mapOf(
                entry("age", new BigDecimal(35)),
                entry("name", "Mr. x"),
                entry("addresses", asList(mapOf(entry("streetName", "Street1"),
                                                entry("streetNumber", 1)),
                                          mapOf(entry("streetName", "Street2"),
                                                entry("streetNumber", 2))

                ))));

        context.fromMap(inputSetMap);

        DMNResult evaluateAll = evaluateTyped(context, runtime, dmnModel);

        DMNContext result = evaluateAll.getContext();
        Map<String, Object> d = (Map<String, Object>) result.get("d");
        assertThat(d.get("Hello"), is("Hello Mr. x"));
        LOG.info("{}", evaluateAll);
    }

    private static DMNResult evaluateTyped(FEELPropertyAccessible context, DMNRuntime runtime, DMNModel dmnModel) {
        return runtime.evaluateAll(dmnModel, new DMNContextFPAImpl(context));
    }

    public static FEELPropertyAccessible createInputSet(DMNModel dmnModel, String packageName, ClassLoader classLoader) throws Exception {
        Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(
                dmnModel,
                new DMNClassNamespaceTypeIndex(dmnModel))
                .generateSourceCodeOfAllTypes();

        Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(allTypesSourceCode, classLoader);

        return createInstanceFromCompiledClasses(compiledClasses, packageName, "InputSet");
    }

    private static String classWithPackage(String packageName, String className) {
        return packageName + "." + className;
    }

    private static FEELPropertyAccessible createInstanceFromCompiledClasses(Map<String, Class<?>> compile, String packageName, String className) throws Exception {
        Class<?> inputSetClass = compile.get(classWithPackage(packageName, className));
        assertThat(inputSetClass, notNullValue());
        Object inputSetInstance = inputSetClass.getDeclaredConstructor().newInstance();
        return (FEELPropertyAccessible) inputSetInstance;
    }
}
