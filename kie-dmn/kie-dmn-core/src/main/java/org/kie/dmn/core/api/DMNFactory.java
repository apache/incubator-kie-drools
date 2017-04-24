/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.api;

import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNContextImpl;

public class DMNFactory {

    public static DMNContext newContext() {
        return new DMNContextImpl();
    }

    public static DMNCompiler newCompiler() { return new DMNCompilerImpl(); }

    public static DMNCompiler newCompiler(DMNCompilerConfiguration dmnCompilerConfig) {
        return new DMNCompilerImpl(dmnCompilerConfig);
    }

    public static DMNCompilerConfiguration newCompilerConfiguration() {
        return new DMNCompilerConfigurationImpl();
    }

}
