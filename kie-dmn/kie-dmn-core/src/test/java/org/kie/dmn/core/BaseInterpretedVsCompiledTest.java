/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.compiler.ExecModelCompilerOption;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.typesafe.DMNAllTypesIndex;
import org.kie.dmn.typesafe.DMNTypeSafePackageName;
import org.kie.dmn.typesafe.DMNTypeSafeTest;
import org.kie.dmn.typesafe.DMNTypeSafeTypeGenerator;
import org.kie.memorycompiler.KieMemoryCompiler;



@RunWith(Parameterized.class)
public abstract class BaseInterpretedVsCompiledTest {

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{true, false};
    }

    private final boolean useExecModelCompiler;

    public BaseInterpretedVsCompiledTest(final boolean useExecModelCompiler) {
        this.useExecModelCompiler = useExecModelCompiler;
    }

    @Before
    public void before() {
        System.setProperty(ExecModelCompilerOption.PROPERTY_NAME, Boolean.toString(useExecModelCompiler));
    }

    @After
    public void after() {
        System.clearProperty(ExecModelCompilerOption.PROPERTY_NAME);
    }

    protected Map<String, String> allSources;
    protected Map<String, Class<?>> allCompiledClasses;

    protected void createTypeSafeInput(DMNRuntime runtime) {
        DMNAllTypesIndex index = new DMNAllTypesIndex(runtime.getModels(), "");
        allSources = new HashMap<>();

        for(DMNModel m : runtime.getModels()) {
            String packageName = new DMNTypeSafePackageName(m, "").packageName();
            Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(m, index, packageName).generateSourceCodeOfAllTypes();
            allSources.putAll(allTypesSourceCode);
        }

        allCompiledClasses = KieMemoryCompiler.compile(allSources, this.getClass().getClassLoader());
    }

    protected DMNResult evaluateModel(DMNRuntime runtime, DMNModel dmnModel, DMNContext context) {
        Map<String, Object> inputMap = context.getAll();
        FEELPropertyAccessible inputSet;
        try {
            String packageName = new DMNTypeSafePackageName(dmnModel, "").packageName();
            inputSet = DMNTypeSafeTest.createInstanceFromCompiledClasses(allCompiledClasses, packageName, "InputSet");
            inputSet.fromMap(inputMap);
            return runtime.evaluateAll(dmnModel, new DMNContextFPAImpl(inputSet));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
