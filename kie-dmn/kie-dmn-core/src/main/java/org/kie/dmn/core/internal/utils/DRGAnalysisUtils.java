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
package org.kie.dmn.core.internal.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DRGAnalysisUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DRGAnalysisUtils.class);
    
    public static Collection<DRGDependency> dependencies(DMNModel model, String nodeName) {
        DMNNode identifiedNode = model.getDecisionByName(nodeName);
        if (identifiedNode != null) {
            return internalDependencies(0, identifiedNode, (DMNBaseNode) identifiedNode);
        }
        identifiedNode = model.getBusinessKnowledgeModelByName(nodeName);
        if (identifiedNode != null) {
            return internalDependencies(0, identifiedNode, (DMNBaseNode) identifiedNode);
        }
        throw new IllegalArgumentException(String.format("No Decision and no BKM found for nodeName: '%s'", nodeName));
    }
    
    public static Collection<DRGDependency> dependencies(DMNModel model, DMNNode node) {
        return internalDependencies(0, node, (DMNBaseNode) node);
    }
    
    static Collection<DRGDependency> internalDependencies(final int degree, final DMNNode superParent, DMNBaseNode node) {
        LOG.trace("internalDependencies {} {} {}", degree, superParent, node);
        List<DRGDependency> results = new ArrayList<>();
        Collection<DMNNode> values = node.getDependencies().values();
        for (DMNNode v : values) { // first we add each node as a direct dependency.
            results.add(new DRGDependency(degree, v, superParent));
        }
        for (DMNNode v : values) { // then we cycle recursively
            if (v instanceof DecisionNodeImpl) {
                results.addAll(internalDependencies(degree+1, superParent, (DecisionNodeImpl) v));
            } else if (v instanceof BusinessKnowledgeModelNodeImpl) {
                results.addAll(internalDependencies(degree+1, superParent, (BusinessKnowledgeModelNodeImpl) v));
            } // anything else, do not need to recurse
        }
        return results;
    }
    
    public static class DRGDependency {
        private final int degree;
        private final DMNNode dependency;
        private final DMNNode parent;
        public DRGDependency(int degree, DMNNode dependency, DMNNode parent) {
            super();
            this.degree = degree;
            this.dependency = dependency;
            this.parent = parent;
        }
        
        public int getDegree() {
            return degree;
        }

        public DMNNode getDependency() {
            return dependency;
        }

        public DMNNode getParent() {
            return parent;
        }

        @Override
        public int hashCode() {
            return Objects.hash(degree, dependency, parent);
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DRGDependency other = (DRGDependency) obj;
            return degree == other.degree && Objects.equals(dependency, other.dependency) &&
                   Objects.equals(parent, other.parent);
        }
        
    }
    
    public static Collection<String> inputDataOfDecision(DMNModel model, String decisionName) {
        DMNNode identifiedNode = model.getDecisionByName(decisionName);
        if (identifiedNode != null) {
            Collection<DRGDependency> dependencies = dependencies(model, decisionName);
            Set<String> inputs = dependencies.stream().filter(d->d.getDependency()instanceof InputDataNode).map(d->d.getDependency().getName()).collect(Collectors.toSet());
            return inputs;
        } else {
            throw new IllegalArgumentException(String.format("Could not find decision '%s'", decisionName));
        }
    }

    private DRGAnalysisUtils() {
        // Constructing instances is not allowed for this class
    }

}
