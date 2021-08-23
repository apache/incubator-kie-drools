/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.trisotech;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.DRGElementCompiler;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.trisotech.backend.marshalling.v1_3.xstream.TrisotechBoxedExtensionRegister;

public class TrisotechDMNProfile implements DMNProfile {

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Collections.emptyList();
    }

    @Override
    public List<DMNExtensionRegister> getExtensionRegisters() {
        return Arrays.asList(new TrisotechBoxedExtensionRegister());
    }

    @Override
    public List<DRGElementCompiler> getDRGElementCompilers() {
        return Collections.emptyList();
    }

}
