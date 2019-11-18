/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.compiler;

import org.kie.dmn.core.assembler.DMNAssemblerService;

public class ExecModelCompilerOption implements DMNOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = DMNAssemblerService.ORG_KIE_DMN_PREFIX + ".compiler.execmodel";

    /**
     * The default value for this option
     */
    public static final boolean DEFAULT_VALUE = false;

    private final boolean useExecModelCompiler;

    public ExecModelCompilerOption(boolean value) {
        this.useExecModelCompiler = value;
    }

    public ExecModelCompilerOption(String value) {
        this.useExecModelCompiler = value == null ? DEFAULT_VALUE : Boolean.valueOf(value);
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isUseExecModelCompiler() {
        return useExecModelCompiler;
    }

}
