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

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.ast.*;
import org.kie.dmn.feel.model.v1_1.BusinessKnowledgeModel;
import org.kie.dmn.feel.model.v1_1.Definitions;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.stream.Collectors;

public class DMNModelImpl
        implements DMNModel {

    private Definitions definitions;
    private Map<String, InputDataNode>              inputs       = new HashMap<>();
    private Map<String, DecisionNode>               decisions    = new HashMap<>();
    private Map<String, BusinessKnowledgeModelNode> bkms         = new HashMap<>();
    private Map<String, ItemDefNode>                itemDefs     = new HashMap<>();

    // these are messages created at loading/compilation time
    private List<DMNMessage> messages = new ArrayList<>();

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
        if ( name == null ) {
            return null;
        }
        for ( InputDataNode in : this.inputs.values() ) {
            if ( in.getName() != null && name.equals( in.getName() ) ) {
                return in;
            }
        }
        return null;
    }

    @Override
    public Set<InputDataNode> getInputs() {
        return this.inputs.values().stream().collect( Collectors.toSet() );
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
        if ( name == null ) {
            return null;
        }
        for ( DecisionNode dn : this.decisions.values() ) {
            if ( dn.getName() != null && name.equals( dn.getName() ) ) {
                return dn;
            }
        }
        return null;
    }

    @Override
    public Set<DecisionNode> getDecisions() {
        return this.decisions.values().stream().collect( Collectors.toSet() );
    }

    @Override
    public Set<InputDataNode> getRequiredInputsForDecisionName(String decisionName) {
        DecisionNodeImpl decision = (DecisionNodeImpl) getDecisionByName( decisionName );
        Set<InputDataNode> inputs = new HashSet<>();
        if ( decision != null ) {
            collectRequiredInputs( decision.getDependencies().values(), inputs );
        }
        return inputs;
    }

    @Override
    public Set<InputDataNode> getRequiredInputsForDecisionId(String decisionId) {
        DecisionNodeImpl decision = (DecisionNodeImpl) getDecisionById( decisionId );
        Set<InputDataNode> inputs = new HashSet<>();
        if ( decision != null ) {
            collectRequiredInputs( decision.getDependencies().values(), inputs );
        }
        return inputs;
    }

    public void addBusinessKnowledgeModel(BusinessKnowledgeModelNode bkm) {
        bkms.put( bkm.getId(), bkm );
    }

    @Override
    public BusinessKnowledgeModelNode getBusinessKnowledgeModelById(String id) {
        return this.bkms.get( id );
    }

    @Override
    public BusinessKnowledgeModelNode getBusinessKnowledgeModelByName(String name) {
        if ( name == null ) {
            return null;
        }
        for ( BusinessKnowledgeModelNode bkm : this.bkms.values() ) {
            if ( bkm.getName() != null && name.equals( bkm.getName() ) ) {
                return bkm;
            }
        }
        return null;
    }

    @Override
    public Set<BusinessKnowledgeModelNode> getBusinessKnowledgeModels() {
        return this.bkms.values().stream().collect( Collectors.toSet() );
    }

    @Override
    public Set<InputDataNode> getRequiredInputsForBusinessKnowledgeModelName(String bkmName) {
        BusinessKnowledgeModelNodeImpl bkm = (BusinessKnowledgeModelNodeImpl) getBusinessKnowledgeModelByName( bkmName );
        Set<InputDataNode> inputs = new HashSet<>();
        if ( bkm != null ) {
            collectRequiredInputs( bkm.getDependencies().values(), inputs );
        }
        return inputs;
    }

    @Override
    public Set<InputDataNode> getRequiredInputsForBusinessKnowledgeModelId(String bkmId) {
        BusinessKnowledgeModelNodeImpl bkm = (BusinessKnowledgeModelNodeImpl) getBusinessKnowledgeModelById( bkmId );
        Set<InputDataNode> inputs = new HashSet<>();
        if ( bkm != null ) {
            collectRequiredInputs( bkm.getDependencies().values(), inputs );
        }
        return inputs;
    }

    private void collectRequiredInputs(Collection<DMNNode> deps, Set<InputDataNode> inputs) {
        deps.forEach( dep -> {
            if ( dep instanceof InputDataNode ) {
                inputs.add( (InputDataNode) dep );
            } else if ( dep instanceof DecisionNode ) {
                collectRequiredInputs( ((DecisionNodeImpl) dep).getDependencies().values(), inputs );
            } else if ( dep instanceof BusinessKnowledgeModelNode ) {
                collectRequiredInputs( ((BusinessKnowledgeModelNodeImpl) dep).getDependencies().values(), inputs );
            }
        } );
    }

    public void addItemDefinition(ItemDefNode idn) {
        // if ID is null, generate an ID for it
        this.itemDefs.put( idn.getId() != null ? idn.getId() : "_"+this.itemDefs.size(), idn );
    }

    @Override
    public ItemDefNode getItemDefinitionById(String id) {
        return this.itemDefs.get( id );
    }

    @Override
    public ItemDefNode getItemDefinitionByName(String name) {
        if ( name == null ) {
            return null;
        }
        for ( ItemDefNode in : this.itemDefs.values() ) {
            if ( in.getName() != null && name.equals( in.getName() ) ) {
                return in;
            }
        }
        return null;
    }

    @Override
    public Set<ItemDefNode> getItemDefinitions() {
        return this.itemDefs.values().stream().collect( Collectors.toSet() );
    }

    @Override
    public List<DMNMessage> getMessages() {
        return messages;
    }

    @Override
    public List<DMNMessage> getMessages(DMNMessage.Severity... sevs) {
        List<DMNMessage.Severity> severities = Arrays.asList( sevs );
        return messages.stream().filter( m -> severities.contains( m.getSeverity() ) ).collect( Collectors.toList() );
    }

    @Override
    public boolean hasErrors() {
        return messages.stream().anyMatch( m -> DMNMessage.Severity.ERROR.equals( m.getSeverity() ) );
    }

    public void addMessage(DMNMessage msg) {
        this.messages.add( msg );
    }

    public DMNMessage addMessage(DMNMessage.Severity severity, String message, String sourceId) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, sourceId );
        this.messages.add( msg );
        return msg;
    }

    public void addMessage(DMNMessage.Severity severity, String message, String sourceId, Throwable exception) {
        this.messages.add( new DMNMessageImpl( severity, message, sourceId, exception ) );
    }

    public void addMessage(DMNMessage.Severity severity, String message, String sourceId, FEELEvent feelEvent) {
        this.messages.add( new DMNMessageImpl( severity, message, sourceId, feelEvent ) );
    }

}
