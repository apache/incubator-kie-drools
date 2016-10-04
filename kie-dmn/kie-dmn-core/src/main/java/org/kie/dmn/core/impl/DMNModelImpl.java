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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DMNModelImpl
        implements DMNModel {

    private Definitions                definitions;
    private Map<String, InputDataNode> inputs = new HashMap<>(  );
    private Map<String, DecisionNode> decisions = new HashMap<>(  );

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

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }

    public void addInput(InputDataNode idn) {
        inputs.put( idn.getId(), idn );
    }

    public InputDataNode getInput( String id ) {
        return this.inputs.get( id );
    }

    public Collection<InputDataNode> getInputs() {
        return this.inputs.values();
    }

    public void addDecision(DecisionNode dn) {
        decisions.put( dn.getId(), dn );

    }

    public DecisionNode getDecision( String id ) {
        return this.decisions.get( id );
    }

    public Collection<DecisionNode> getDecisions() {
        return this.decisions.values();
    }


}
