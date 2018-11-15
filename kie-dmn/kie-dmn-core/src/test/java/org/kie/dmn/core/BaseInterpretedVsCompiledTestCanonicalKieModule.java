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

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.core.compiler.ExecModelCompilerOption;

@RunWith(Parameterized.class)
public abstract class BaseInterpretedVsCompiledTestCanonicalKieModule {

    @Parameterized.Parameters
    public static Object[] params() {
        return new Object[][]{ { true, true}, {true, false}, {false, true}, {false, false}};
    }

    private final boolean useExecModelCompiler;
    protected final boolean canonicalKieModule;

    public BaseInterpretedVsCompiledTestCanonicalKieModule(boolean useExecModelCompiler, boolean canonicalKieModule) {
        this.useExecModelCompiler = useExecModelCompiler;
        this.canonicalKieModule = canonicalKieModule;
    }

    @Before
    public void before() {
        System.setProperty(ExecModelCompilerOption.PROPERTY_NAME, Boolean.toString(useExecModelCompiler));
    }

    @After
    public void after() {
        System.clearProperty(ExecModelCompilerOption.PROPERTY_NAME);
    }
}
