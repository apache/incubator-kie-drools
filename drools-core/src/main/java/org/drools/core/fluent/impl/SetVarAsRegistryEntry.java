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

package org.drools.core.fluent.impl;

import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.runtime.Context;

import java.util.Map;

public class SetVarAsRegistryEntry<Void> implements ExecutableCommand<Void> {
    private String registryName;
    private String varName;

    public SetVarAsRegistryEntry(String registryName, String varName) {
        this.registryName = registryName;
        this.varName = varName;
    }

    @Override
    public Void execute(Context context) {
        Object o = context.get(varName);

        ((Map<String, Object>)context.get(ContextImpl.REGISTRY)).put(registryName, o);
        return null;
    }

    @Override
    public String toString() {
        return "SetVarAsRegistryEntry{" +
               "registryName='" + registryName + '\'' +
               ", varName='" + varName + '\'' +
               '}';
    }
}
