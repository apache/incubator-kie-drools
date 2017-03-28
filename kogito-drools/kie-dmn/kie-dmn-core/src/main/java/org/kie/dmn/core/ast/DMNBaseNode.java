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

package org.kie.dmn.core.ast;

import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.model.v1_1.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class DMNBaseNode
        implements DMNNode {

    private NamedElement source;
    // need to retain dependencies order, so need to use LinkedHashMap
    private Map<String, DMNNode> dependencies = new LinkedHashMap<>();

    public DMNBaseNode() {
    }

    public DMNBaseNode(NamedElement source) {
        this.source = source;
    }

    public String getId() {
        return source != null ? source.getId() : null;
    }

    public String getName() {
        return source != null ? source.getName() : null;
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
}
