/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder.DMNRuntimeBuilderConfigured;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.typesafe.DMNAllTypesIndex;
import org.kie.dmn.typesafe.DMNTypeSafePackageName;
import org.kie.dmn.typesafe.DMNTypeSafeTest;
import org.kie.dmn.typesafe.DMNTypeSafeTypeGenerator;
import org.kie.memorycompiler.KieMemoryCompiler;


@RunWith(Parameterized.class)
public abstract class BaseVariantTest implements VariantTest {

    public static enum VariantTestConf implements VariantTest {
        KIE_API_TYPECHECK {

            @Override
            public DMNRuntime createRuntime(String string, Class<?> class1) {
                return DMNRuntimeUtil.createRuntime(string, class1);
            }

            @Override
            public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
                return DMNRuntimeUtil.createRuntimeWithAdditionalResources(string, class1, string2);
            }
        },
        BUILDER_STRICT {

            private DMNRuntimeBuilderConfigured builder() {
                return DMNRuntimeBuilder.usingStrict();
            }

            @Override
            public DMNRuntime createRuntime(String string, Class<?> class1) {
                return builder().fromClasspathResource(string, class1).getOrElseThrow(RuntimeException::new);
            }

            @Override
            public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
                return builder().fromClasspathResources(string, class1, string2).getOrElseThrow(RuntimeException::new);
            }
        },
        BUILDER_DEFAULT_NOCL_TYPECHECK {

            private DMNRuntimeBuilderConfigured builder() {
                return DMNRuntimeBuilder.fromDefaults().setRootClassLoader(null).setOption(new RuntimeTypeCheckOption(true)).buildConfiguration();
            }

            @Override
            public DMNRuntime createRuntime(String string, Class<?> class1) {
                return builder().fromClasspathResource(string, class1).getOrElseThrow(RuntimeException::new);
            }

            @Override
            public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
                return builder().fromClasspathResources(string, class1, string2).getOrElseThrow(RuntimeException::new);
            }
        };

    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{VariantTestConf.KIE_API_TYPECHECK, VariantTestConf.BUILDER_STRICT, VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK};
    }

    private final VariantTestConf testConfig;

    public BaseVariantTest(final VariantTestConf testConfig) {
        this.testConfig = testConfig;
    }

    @Override
    public DMNRuntime createRuntime(String string, Class<?> class1) {
        DMNRuntime runtime = testConfig.createRuntime(string, class1);
        createTypeSafeInput(runtime);

        return runtime;
    }

    protected Map<String, String> allSources;
    protected Map<String, Class<?>> allCompiledClasses;

    private void createTypeSafeInput(DMNRuntime runtime) {
        DMNAllTypesIndex index = new DMNAllTypesIndex(runtime.getModels(), testConfig.name());
        allSources = new HashMap<>();

        for (DMNModel m : runtime.getModels()) {
            String packageName = new DMNTypeSafePackageName(m, testConfig.name()).packageName();
            Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(m, index, packageName)
                    .generateSourceCodeOfAllTypes();
            allSources.putAll(allTypesSourceCode);
        }

        allCompiledClasses = KieMemoryCompiler.compile(allSources, this.getClass().getClassLoader());
    }

    @Override
    public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
        DMNRuntime runtimeWithAdditionalResources = testConfig.createRuntimeWithAdditionalResources(string, class1, string2);
        createTypeSafeInput(runtimeWithAdditionalResources);
        return runtimeWithAdditionalResources;
    }

    protected DMNResult evaluateModel(DMNRuntime runtime, DMNModel dmnModel, DMNContext context) {
        Map<String, Object> inputMap = context.getAll();
        FEELPropertyAccessible inputSet;
        try {
            String packageName = new DMNTypeSafePackageName(dmnModel, testConfig.name()).packageName();
            inputSet = DMNTypeSafeTest.createInstanceFromCompiledClasses(allCompiledClasses, packageName, "InputSet");
            inputSet.fromMap(inputMap);
            return runtime.evaluateAll(dmnModel, new DMNContextFPAImpl(inputSet));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

interface VariantTest {

    DMNRuntime createRuntime(String string, Class<?> class1);

    DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2);
}
