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
package org.kie.dmn.core.assembler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.api.io.ResourceWithConfiguration;
import org.kie.dmn.model.api.Definitions;

public class DMNResource {

    private final QName modelID;
    private final ResourceWithConfiguration resAndConfig;
    private final Definitions definitions;
    private final List<QName> dependencies = new ArrayList<>();

    public DMNResource(Definitions definitions, ResourceWithConfiguration resAndConfig) {
        this.modelID = new QName(definitions.getNamespace(), definitions.getName());
        this.resAndConfig = resAndConfig;
        this.definitions = definitions;
    }

    /**
     * @deprecated Use {@link #DMNResource(Definitions, ResourceWithConfiguration)} instead.
     */
    @Deprecated
    public DMNResource(QName modelID, ResourceWithConfiguration resAndConfig, Definitions definitions) {
        this.modelID = modelID;
        this.resAndConfig = resAndConfig;
        this.definitions = definitions;
    }

    public QName getModelID() {
        return modelID;
    }

    public ResourceWithConfiguration getResAndConfig() {
        return resAndConfig;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void addDependency(QName dep) {
        this.dependencies.add(dep);
    }

    public void addDependencies(List<QName> deps) {
        this.dependencies.addAll(deps);
    }

    public List<QName> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "DMNResource [modelID=" + modelID + ", resource=" + resAndConfig.getResource().getSourcePath() + "]";
    }

}