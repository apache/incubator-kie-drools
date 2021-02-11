/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.stronglytyped;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.typesafe.DMNAllTypesIndex;
import org.kie.dmn.typesafe.DMNTypeSafePackageName;
import org.kie.dmn.typesafe.DMNTypeSafeTypeGenerator;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK_TYPESAFE;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DMNTypeSafeTest extends BaseVariantTest {

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK_TYPESAFE};
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNTypeSafeTest.class);

    private DMNTypeSafePackageName packageName;
    private DMNModel dmnModel;
    private DMNRuntime runtime;

    private DMNTypeSafePackageName.ModelFactory modelFactory;

    public DMNTypeSafeTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Before
    public void setUp() {
        runtime = DMNRuntimeUtil.createRuntime("a.dmn", this.getClass());
        String namespace = "http://www.trisotech.com/definitions/_2ceee5b6-0f0d-41ef-890e-2cd6fb1adb10";
        String modelName = "Drawing 1";

        dmnModel = runtime.getModel(namespace, modelName);
        modelFactory = new DMNTypeSafePackageName.ModelFactory();
        packageName = modelFactory.create(dmnModel);
    }

    @Test
    public void test() throws Exception {

        assertValidDmnModel(dmnModel);

        DMNAllTypesIndex index = new DMNAllTypesIndex(new DMNTypeSafePackageName.ModelFactory(), dmnModel);

        Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(dmnModel, index, modelFactory)
                .processTypes()
                .generateSourceCodeOfAllTypes();

        ClassLoader thisDMNClassLoader = this.getClass().getClassLoader();
        Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(allTypesSourceCode, thisDMNClassLoader);

        FEELPropertyAccessible street1 = tAddress(compiledClasses, "Street1", 1);
        FEELPropertyAccessible street2 = tAddress(compiledClasses, "Street2", 2);

        FEELPropertyAccessible tPersonInstance = tPerson(compiledClasses, asList(street1, street2));
        FEELPropertyAccessible context = outputSet(compiledClasses, tPersonInstance);

        DMNResult evaluateAll = evaluateTyped(context, runtime, dmnModel);
        convertContext(evaluateAll, createInstanceFromCompiledClasses(compiledClasses, packageName, "OutputSet"));

        DMNContext result = evaluateAll.getContext();

        Map<String, Object> d = (Map<String, Object>) result.get("d");
        assertThat(d.get("Hello"), is("Hello Mr. x"));

        FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)result).getFpa();

        assertThat(outputSet.getFEELProperty("p").toOptional().get(), equalTo(tPersonInstance));
        Map<String, Object> dContext = (Map<String, Object>)outputSet.getFEELProperty("d").toOptional().get();
        assertThat(dContext.get("Hello"), is("Hello Mr. x"));
        assertThat(dContext.get("the person"), equalTo(tPersonInstance));
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

    private FEELPropertyAccessible outputSet(Map<String, Class<?>> compile, FEELPropertyAccessible tPersonInstance) throws Exception {
        FEELPropertyAccessible feelPropertyAccessible = createInstanceFromCompiledClasses(compile, packageName, "OutputSet");
        feelPropertyAccessible.setFEELProperty("p", tPersonInstance);
        return feelPropertyAccessible;
    }

    @Test
    public void testDynamic() throws Exception {

        assertValidDmnModel(dmnModel);

        Map<String, Class<?>> classes = generateSourceCodeAndCreateInput(dmnModel, modelFactory, this.getClass().getClassLoader());

        FEELPropertyAccessible context = createInstanceFromCompiledClasses(classes, packageName, "OutputSet");

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
        convertContext(evaluateAll, createInstanceFromCompiledClasses(classes, packageName, "OutputSet"));

        DMNContext result = evaluateAll.getContext();

        Map<String, Object> d = (Map<String, Object>) result.get("d");
        assertThat(d.get("Hello"), is("Hello Mr. x"));

        FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)result).getFpa();

        assertThat(outputSet.getFEELProperty("p").toOptional().get(), equalTo(context.getFEELProperty("p").toOptional().get()));
        Map<String, Object> dContext = (Map<String, Object>)outputSet.getFEELProperty("d").toOptional().get();
        assertThat(dContext.get("Hello"), is("Hello Mr. x"));
        assertThat(dContext.get("the person"), equalTo(context.getFEELProperty("p").toOptional().get()));
    }

    @Test
    public void testMetadata() throws Exception {

        assertValidDmnModel(dmnModel);

        Map<String, Class<?>> classes = generateSourceCodeAndCreateInput(dmnModel, modelFactory, this.getClass().getClassLoader());
        FEELPropertyAccessible feelPropertyAccessibleContext = createInstanceFromCompiledClasses(classes, packageName, "InputSet");

        String metadataKey = "test";
        String metadataValue = "value";
        DMNContext context = new DMNContextFPAImpl(feelPropertyAccessibleContext);
        context.getMetadata().set(metadataKey, metadataValue);

        assertEquals(metadataValue, context.getMetadata().get(metadataKey));
        assertEquals(metadataValue, context.clone().getMetadata().get(metadataKey));
    }

    private void assertValidDmnModel(DMNModel dmnModel){
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));
    }

    private static DMNResult evaluateTyped(FEELPropertyAccessible context, DMNRuntime runtime, DMNModel dmnModel) {
        return runtime.evaluateAll(dmnModel, new DMNContextFPAImpl(context));
    }

    public static Map<String, Class<?>> generateSourceCodeAndCreateInput(DMNModel dmnModel, DMNTypeSafePackageName.ModelFactory packageName, ClassLoader classLoader) {
        DMNAllTypesIndex index = new DMNAllTypesIndex(packageName, dmnModel);
        Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(
                dmnModel,
                index, packageName)
                .processTypes()
                .generateSourceCodeOfAllTypes();

        return KieMemoryCompiler.compile(allTypesSourceCode, classLoader);
    }
}
