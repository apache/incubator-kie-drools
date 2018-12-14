/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.fluent;

import java.util.Objects;
import java.util.Optional;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.command.RegistryContext;

public abstract class AbstractDMNModelCommand implements ExecutableCommand<DMNModel> {

    protected String namespace;
    protected String modelName;
    protected String resourcePath;

    public AbstractDMNModelCommand(String namespace, String modelName) {
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
        this.modelName = Objects.requireNonNull(modelName, "modelName cannot be null");
    }

    public AbstractDMNModelCommand(String resourcePath) {
        this.resourcePath = Objects.requireNonNull(resourcePath, "resource cannot be null");
    }

    protected DMNModel getDMNModel(Context context) {
        RegistryContext registryContext = (RegistryContext) context;
        DMNRuntime dmnRuntime = registryContext.lookup(DMNRuntime.class);
        if (dmnRuntime == null) {
            throw new IllegalStateException("There is no DMNRuntime available");
        }

        return retrieveDMNModel(dmnRuntime);
    }

    protected DMNModel retrieveDMNModel(DMNRuntime dmnRuntime) {
        if(namespace != null && modelName != null) {
            return Optional
                    .ofNullable(dmnRuntime.getModel(namespace, modelName))
                    .orElseThrow(() -> new IllegalStateException("Cannot find a DMN model with namespace=" + namespace + " and modelName=" + modelName));
        }
        else if(resourcePath != null) {
            return dmnRuntime.getModels().stream()
                    .filter(model -> Objects.equals(resourcePath, model.getResource().getSourcePath()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Cannot find a DMN model with resource=" + resourcePath));
        }
        throw new IllegalStateException("Both resourcePath and namespace+modelName are not set, this should not happen");
    }
}
