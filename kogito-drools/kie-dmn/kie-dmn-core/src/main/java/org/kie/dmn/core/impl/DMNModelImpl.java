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

package org.kie.dmn.core.impl;

import org.kie.dmn.core.api.DMNModel;
import org.kie.dmn.core.ast.DecisionNode;
import org.kie.dmn.core.ast.InputDataNode;
import org.kie.dmn.feel.model.v1_1.Definitions;

import java.util.*;
import java.util.stream.Collectors;

public class DMNModelImpl
        implements DMNModel {

    private Definitions definitions;
    private Map<String, InputDataNode> inputs    = new HashMap<>();
    private Map<String, DecisionNode>  decisions = new HashMap<>();

    public DMNModelImpl() {
    }

    public DMNModelImpl(Definitions definitions) {
        this.definitions = definitions;
    }

    @Override
    public String getNamespace() {
        return definitions != null ? definitions.getNamespace() : null;
    }

    public String getName() {
        return definitions != null ? definitions.getName() : null;
    }

    @Override
    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }

    public void addInput(InputDataNode idn) {
        inputs.put( idn.getId(), idn );
    }

    @Override
    public InputDataNode getInputById(String id) {
        return this.inputs.get( id );
    }

    @Override
    public InputDataNode getInputByName(String name) {
        if( name == null ) {
            return null;
        }
        for( InputDataNode in : this.inputs.values() ) {
            if( in.getName() != null && name.equals( in.getName() ) ) {
                return in;
            }
        }
        return null;
    }

    @Override
    public Set<InputDataNode> getInputs() {
        return this.inputs.values().stream().collect( Collectors.toSet());
    }

    public void addDecision(DecisionNode dn) {
        decisions.put( dn.getId(), dn );

    }

    @Override
    public DecisionNode getDecisionById(String id) {
        return this.decisions.get( id );
    }

    @Override
    public DecisionNode getDecisionByName(String name) {
        if( name == null ) {
            return null;
        }
        for( DecisionNode dn : this.decisions.values() ) {
            if( dn.getName() != null && name.equals( dn.getName() ) ) {
                return dn;
            }
        }
        return null;
    }

    @Override
    public Set<DecisionNode> getDecisions() {
        return this.decisions.values().stream().collect( Collectors.toSet());
    }

    @Override
    public Set<InputDataNode> getRequiredInputsForDecisionName(String decisionName) {
        DecisionNode decision = getDecisionByName( decisionName );
        Set<InputDataNode> inputs = new HashSet<>(  );
        if( decision != null ) {
            collectInputsForDecision( decision, inputs );
        }
        return inputs;
    }

    @Override
    public Set<InputDataNode> getRequiredInputsForDecisionId(String decisionId) {
        DecisionNode decision = getDecisionById( decisionId );
        Set<InputDataNode> inputs = new HashSet<>(  );
        if( decision != null ) {
            collectInputsForDecision( decision, inputs );
        }
        return inputs;
    }

    private void collectInputsForDecision(DecisionNode decision, Set<InputDataNode> inputs) {
        decision.getDependencies().values().forEach( dep -> {
            if( dep instanceof InputDataNode ) {
                inputs.add( (InputDataNode) dep );
            } else if( dep instanceof DecisionNode ) {
                collectInputsForDecision( (DecisionNode) dep, inputs );
            }
        } );
    }
}
