/*
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

import org.junit.jupiter.api.AfterEach;
import org.kie.dmn.core.compiler.ExecModelCompilerOption;
import org.kie.dmn.core.compiler.RuntimeModeOption;

import static org.kie.dmn.core.compiler.RuntimeModeOption.MODE.LENIENT;
import static org.kie.dmn.core.compiler.RuntimeModeOption.MODE.STRICT;

public abstract class BaseInterpretedVsCompiledTest {

    protected static Object[] params() {
        return new Object[]{false};
    }

    protected static Object[] strictMode() {
        return new Object[]{false, true};
    }

    protected boolean useExecModelCompiler;
    protected boolean useStrictMode;

    protected void init(boolean useExecModelCompiler){
        init(useExecModelCompiler, false);
    }

    protected void init(boolean useExecModelCompiler, boolean useStrictMode) {
        this.useExecModelCompiler = useExecModelCompiler;
        this.useStrictMode = useStrictMode;
        System.setProperty(ExecModelCompilerOption.PROPERTY_NAME, Boolean.toString(useExecModelCompiler));
        String modeToSet = useStrictMode ? STRICT.getMode() : LENIENT.getMode();
        System.setProperty(RuntimeModeOption.PROPERTY_NAME, modeToSet);
    }

    @AfterEach
    public void after() {
        System.clearProperty(ExecModelCompilerOption.PROPERTY_NAME);
        System.clearProperty(RuntimeModeOption.PROPERTY_NAME);
    }
}
