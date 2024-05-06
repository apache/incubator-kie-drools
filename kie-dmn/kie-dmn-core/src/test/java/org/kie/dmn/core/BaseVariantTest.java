/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core;

import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.compiler.ExecModelCompilerOption;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder.DMNRuntimeBuilderConfigured;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.typesafe.DMNAllTypesIndex;
import org.kie.dmn.typesafe.DMNTypeSafePackageName;
import org.kie.dmn.typesafe.DMNTypeSafeTypeGenerator;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK_TYPESAFE;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_STRICT;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK_TYPESAFE;

public abstract class BaseVariantTest {

    private DMNTypeSafePackageName.Factory factory;

    public enum VariantTestConf implements VariantTest {
        KIE_API_TYPECHECK {
            @Override
            public DMNRuntime createRuntime(String string, Class<?> class1) {
                return DMNRuntimeUtil.createRuntime(string, class1);
            }

            @Override
            public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
                return DMNRuntimeUtil.createRuntimeWithAdditionalResources(string, class1, string2);
            }

            @Override
            public boolean isTypeSafe() {
                return false;
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

            @Override
            public boolean isTypeSafe() {
                return false;
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

            @Override
            public boolean isTypeSafe() {
                return false;
            }
        },
        KIE_API_TYPECHECK_TYPESAFE {
            @Override
            public DMNRuntime createRuntime(String string, Class<?> class1) {
                return DMNRuntimeUtil.createRuntime(string, class1);
            }

            @Override
            public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
                return DMNRuntimeUtil.createRuntimeWithAdditionalResources(string, class1, string2);
            }

            @Override
            public boolean isTypeSafe() {
                return true;
            }
        },
        BUILDER_DEFAULT_NOCL_TYPECHECK_TYPESAFE {
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

            @Override
            public boolean isTypeSafe() {
                return true;
            }
        }
    }

    protected static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK, BUILDER_STRICT, BUILDER_DEFAULT_NOCL_TYPECHECK, BUILDER_DEFAULT_NOCL_TYPECHECK_TYPESAFE, KIE_API_TYPECHECK_TYPESAFE};
    }

    protected VariantTestConf testConfig;

    public boolean isTypeSafe() {
        return testConfig.isTypeSafe();
    }

    public DMNRuntime createRuntime(final Class testClass) {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime(testClass);
        if (testConfig.isTypeSafe()) {
            createTypeSafeInput(runtime);
        }
        return runtime;
    }


    public DMNRuntime createRuntime(String string, Class<?> class1) {
        DMNRuntime runtime = testConfig.createRuntime(string, class1);
        if (testConfig.isTypeSafe()) {
            createTypeSafeInput(runtime);
        }
        return runtime;
    }

    public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
        DMNRuntime runtimeWithAdditionalResources = testConfig.createRuntimeWithAdditionalResources(string, class1, string2);
        if (testConfig.isTypeSafe()) {
            createTypeSafeInput(runtimeWithAdditionalResources);
        }
        return runtimeWithAdditionalResources;
    }

    protected Map<String, Class<?>> allCompiledClasses;

    protected Map<String, String> allSources;

    protected String testName = "";

    private void createTypeSafeInput(DMNRuntime runtime) {
        String prefix = String.format("%s%s", testName, testConfig.name());
        factory = new DMNTypeSafePackageName.ModelFactory(prefix);
        DMNAllTypesIndex index = new DMNAllTypesIndex(factory, runtime.getModels().toArray(new DMNModel[]{}));
        allSources = new HashMap<>();

        for (DMNModel m : runtime.getModels()) {
            Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(m, index, factory)
                    .withJacksonAnnotation()
                    .withMPAnnotation()
                    .withIOSwaggerOASv3()
                    .processTypes()
                    .generateSourceCodeOfAllTypes();
            allSources.putAll(allTypesSourceCode);
        }

        if(!allSources.isEmpty()) {
            allCompiledClasses = KieMemoryCompiler.compile(allSources, this.getClass().getClassLoader());
        }
    }

    protected DMNResult evaluateModel(DMNRuntime runtime, DMNModel dmnModel, DMNContext context) {
        if (testConfig.isTypeSafe()) {
            return evaluateTypeSafe(runtime, dmnModel, context);
        } else {
            return runtime.evaluateAll(dmnModel, context);
        }
    }

    protected DMNResult evaluateById(DMNRuntime runtime, DMNModel dmnModel, DMNContext context, String... decisionIds) {
        if (testConfig.isTypeSafe()) {
            return evaluateByIdTypeSafe(runtime, dmnModel, context, decisionIds);
        } else {
            return runtime.evaluateById(dmnModel, context, decisionIds);
        }
    }

    protected DMNResult evaluateByName(DMNRuntime runtime, DMNModel dmnModel, DMNContext context, String... decisionNames) {
        if (testConfig.isTypeSafe()) {
            return evaluateByNameTypeSafe(runtime, dmnModel, context, decisionNames);
        } else {
            return runtime.evaluateByName(dmnModel, context, decisionNames);
        }
    }

    protected DMNResult evaluateDecisionService(DMNRuntime runtime, DMNModel dmnModel, DMNContext context, String decisionServiceName) {
        if (testConfig.isTypeSafe()) {
            return evaluateDecisionServiceTypeSafe(runtime, dmnModel, context, decisionServiceName);
        } else {
            return runtime.evaluateDecisionService(dmnModel, context, decisionServiceName);
        }
    }

    private DMNResult evaluateTypeSafe(DMNRuntime runtime, DMNModel dmnModel, DMNContext context) {
        Map<String, Object> inputMap = context.getAll();
        FEELPropertyAccessible outputSet;
        try {
            outputSet = createOutputSetInstance(dmnModel);
            outputSet.fromMap(inputMap);
            DMNContext contextFpa = new DMNContextFPAImpl(outputSet);
            DMNResult result = runtime.evaluateAll(dmnModel, contextFpa);
            return convertContext(result, createOutputSetInstance(dmnModel));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected DMNResult convertContext(DMNResult result, FEELPropertyAccessible outputSet) {
        outputSet.fromMap(result.getContext().getAll());
        DMNContext newContext = new DMNContextFPAImpl(outputSet);
        ((DMNResultImpl)result).setContext(newContext);
        return result;
    }

    private DMNResult evaluateByIdTypeSafe(DMNRuntime runtime, DMNModel dmnModel, DMNContext context,
            String... decisionIds) {
        Map<String, Object> inputMap = context.getAll();
        FEELPropertyAccessible outputSet;
        try {
            outputSet = createOutputSetInstance(dmnModel);
            outputSet.fromMap(inputMap);
            DMNContext contextFpa = new DMNContextFPAImpl(outputSet);
            DMNResult result = runtime.evaluateById(dmnModel, contextFpa, decisionIds);
            return convertContext(result, createOutputSetInstance(dmnModel));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DMNResult evaluateByNameTypeSafe(DMNRuntime runtime, DMNModel dmnModel, DMNContext context, String... decisionNames) {
        Map<String, Object> inputMap = context.getAll();
        FEELPropertyAccessible outputSet;
        try {
            outputSet = createOutputSetInstance(dmnModel);
            outputSet.fromMap(inputMap);
            DMNContext contextFpa = new DMNContextFPAImpl(outputSet);
            DMNResult result = runtime.evaluateByName(dmnModel, contextFpa, decisionNames);
            return convertContext(result, createOutputSetInstance(dmnModel));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DMNResult evaluateDecisionServiceTypeSafe(DMNRuntime runtime, DMNModel dmnModel, DMNContext context, String decisionServiceName) {
        Map<String, Object> inputMap = context.getAll();
        FEELPropertyAccessible outputSet;
        try {
            outputSet = createOutputSetInstance(dmnModel);
            outputSet.fromMap(inputMap);
            DMNContext contextFpa =  new DMNContextFPAImpl(outputSet);
            DMNResult result = runtime.evaluateDecisionService(dmnModel, contextFpa, decisionServiceName);
            return convertContext(result, createOutputSetInstance(dmnModel));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Utility test method, for class inspection.
     */
    protected Class<?> getStronglyClassByName(DMNModel dmnModel, String className) {
        String fqn = factory.create(dmnModel).appendPackage(className);
        return allCompiledClasses.get(fqn);
    }

    protected FEELPropertyAccessible createInstanceFromCompiledClasses(Map<String, Class<?>> compile, DMNTypeSafePackageName packageName, String className) throws Exception {
        Class<?> inputSetClass = compile.get(packageName.appendPackage(className));
        assertThat(inputSetClass).isNotNull();
        Object inputSetInstance = inputSetClass.getDeclaredConstructor().newInstance();
        return (FEELPropertyAccessible) inputSetInstance;
    }

    protected FEELPropertyAccessible createOutputSetInstance(DMNModel dmnModel) throws Exception {
        return createInstanceFromCompiledClasses(allCompiledClasses, factory.create(dmnModel), "OutputSet");
    }
}

interface VariantTest {

    DMNRuntime createRuntime(String string, Class<?> class1);

    DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2);

    boolean isTypeSafe();
}
