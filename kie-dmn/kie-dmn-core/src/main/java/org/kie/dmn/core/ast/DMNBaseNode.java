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
package org.kie.dmn.core.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.api.KnowledgeRequirement;
import org.kie.dmn.model.api.NamedElement;

public abstract class DMNBaseNode implements DMNNode {

    private NamedElement source;
    // need to retain dependencies order, so need to use LinkedHashMap
    private Map<String, DMNNode> dependencies = new LinkedHashMap<>();

    private Map<String, QName> importAliases = new HashMap<>();

    public DMNBaseNode() {
    }

    public DMNBaseNode(NamedElement source) {
        this.source = source;
    }

    public abstract DMNType getType();

    public String getId() {
        return source != null ? source.getId() : null;
    }

    public String getName() {
        return source != null ? source.getName() : null;
    }

    private Optional<Definitions> getParentDefinitions() {
        if (source != null) {
            DMNModelInstrumentedBase parent = source.getParent();
            while (!(parent instanceof Definitions)) {
                if (parent == null) {
                    return Optional.empty();
                }
                parent = parent.getParent();
            }
            return Optional.of((Definitions) parent);
        }
        return Optional.empty();
    }

    @Override
    public String getModelNamespace() {
        return getParentDefinitions().map(Definitions::getNamespace).orElse(null);
    }

    @Override
    public String getModelName() {
        return getParentDefinitions().map(Definitions::getName).orElse(null);
    }

    public String getIdentifierString() {
        String identifier = "[unnamed]";
        if( source != null ) {
            identifier = source.getName() != null ? source.getName() : source.getId();
        }
        return identifier;
    }

    public NamedElement getSource() {
        return source;
    }

    public Map<String, DMNNode> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, DMNNode> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(String name, DMNNode dependency) {
        this.dependencies.put( name, dependency );
    }

    public List<InformationRequirement> getInformationRequirement() {
        if ( source instanceof Decision ) {
            return ((Decision) source).getInformationRequirement();
        } else {
            return Collections.emptyList();
        }
    }

    public List<KnowledgeRequirement> getKnowledgeRequirement() {
        if ( source instanceof Decision ) {
            return ((Decision) source).getKnowledgeRequirement();
        } else if( source instanceof BusinessKnowledgeModel ) {
            return ((BusinessKnowledgeModel) source).getKnowledgeRequirement();
        } else {
            return Collections.emptyList();
        }
    }

    public void addModelImportAliases(Map<String, QName> importAliases) {
        this.importAliases.putAll(importAliases);
    }

    @Override
    public Optional<String> getModelImportAliasFor(String ns, String iModelName) {
        QName lookup = new QName(ns, iModelName);
        return this.importAliases.entrySet().stream().filter(kv -> kv.getValue().equals(lookup)).map(kv -> kv.getKey()).findFirst();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DMNBaseNode [getName()=");
        builder.append(getName());
        builder.append(", getModelNamespace()=");
        builder.append(getModelNamespace());
        builder.append("]");
        return builder.toString();
    }

}
