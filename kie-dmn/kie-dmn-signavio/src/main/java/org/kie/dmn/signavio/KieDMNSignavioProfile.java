/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.signavio;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.DRGElementCompiler;

public class KieDMNSignavioProfile implements DMNProfile {
    
    protected final static List<DMNExtensionRegister> EXT_REGISTERS = Stream.of(
            new MultiInstanceDecisionLogicRegister()
    ).collect(Collectors.toList());
    
    protected final static List<DRGElementCompiler> COMPILATION_EXT = Stream.of(
            new MultiInstanceDecisionLogic.MultiInstanceDecisionNodeCompiler()
    ).collect(Collectors.toList());

    @Override
    public List<DMNExtensionRegister> getExtensionRegisters() {
        return EXT_REGISTERS;
    }

    @Override
    public List<DRGElementCompiler> getDRGElementCompilers() {
        return COMPILATION_EXT;
    }
}