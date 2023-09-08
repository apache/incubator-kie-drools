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
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.model.api.DecisionService;

public class DecisionServiceNodeImpl extends DMNBaseNode implements DecisionServiceNode {

    private DecisionService ds;
    private DMNExpressionEvaluator evaluator;
    private DMNType type;
    private DMNType resultType;
    private Map<String, DMNNode> inputs = new LinkedHashMap<>(); // need to retain order of input (parameter)s

    public DecisionServiceNodeImpl(DecisionService ds, DMNType type, DMNType resultType) {
        super(ds);
        this.ds = ds;
        this.type = type;
        this.resultType = resultType;
    }

    @Override
    public DecisionService getDecisionService() {
        return ds;
    }

    public DMNExpressionEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(DMNExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public DMNType getResultType() {
        return resultType;
    }

    public void setResultType(DMNType resultType) {
        this.resultType = resultType;
    }

    @Override
    public DMNType getType() {
        return this.type;
    }

    /**
     * Will always return an empty collection, a Decision Service has no dependency.
     */
    @Override
    public Map<String, DMNNode> getDependencies() {
        return Collections.emptyMap();
    }

    @Override
    public void setDependencies(Map<String, DMNNode> dependencies) {
        throw new UnsupportedOperationException("A Decision Service has no dependency");
    }

    @Override
    public void addDependency(String name, DMNNode dependency) {
        throw new UnsupportedOperationException("A Decision Service has no dependency");
    }

    public Map<String, DMNNode> getInputParameters() {
        return inputs;
    }

    public void addInputParameter(String name, DMNNode node) {
        this.inputs.put(name, node);
    }

}
