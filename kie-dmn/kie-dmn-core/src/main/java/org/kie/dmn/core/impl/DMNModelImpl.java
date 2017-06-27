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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.core.util.DefaultDMNMessagesManager;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.Definitions;

public class DMNModelImpl
        implements DMNModel, DMNMessageManager, Externalizable {
    
    private static enum SerializationFormat {
        // To ensure backward compatibility, append only:
        DMN_XML
    }
    private SerializationFormat serializedAs = SerializationFormat.DMN_XML;
    private Resource resource;
    private Definitions definitions;
    
    private Map<String, InputDataNode>              inputs       = new HashMap<>();
    private Map<String, DecisionNode>               decisions    = new HashMap<>();
    private Map<String, BusinessKnowledgeModelNode> bkms         = new HashMap<>();
    private Map<String, ItemDefNode>                itemDefs     = new HashMap<>();

    // these are messages created at loading/compilation time
    private DMNMessageManager messages = new DefaultDMNMessagesManager();

    private DMNTypeRegistry types = new DMNTypeRegistry();

    public DMNModelImpl() {
    }

    public DMNModelImpl(Definitions definitions) {
        this.definitions = definitions;
    }
    
    public DMNTypeRegistry getTypeRegistry() {
        return this.types;
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
        return messages.getMessages();
    }

    @Override
    public List<DMNMessage> getMessages(DMNMessage.Severity... sevs) {
        return messages.getMessages( sevs );
    }

    @Override
    public boolean hasErrors() {
        return messages.hasErrors();
    }

    @Override
    public void addAll(List<DMNMessage> messages) {
        this.messages.addAll( messages );
    }

    @Override
    public DMNMessage addMessage(DMNMessage msg) {
        return messages.addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source) {
        return messages.addMessage( severity, message, messageType, source );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception) {
        return messages.addMessage( severity, message, messageType, source, exception );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent) {
        return messages.addMessage( severity, message, messageType, source, feelEvent );
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Resource getResource() {
        return resource;
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(serializedAs);
        out.writeObject(resource);

        if ( !(out instanceof DroolsObjectOutputStream) ) {
            throw new UnsupportedOperationException();
            // TODO assume some defaults
        }
        
        DroolsObjectOutputStream os = (DroolsObjectOutputStream) out;
        DMNCompilerImpl compiler = (DMNCompilerImpl) os.getCustomExtensions().get(DMNAssemblerService.DMN_COMPILER_CACHE_KEY);
        List<DMNExtensionRegister> dmnRegisteredExtensions = compiler.getRegisteredExtensions();
        
        String output = DMNMarshallerFactory.newMarshallerWithExtensions(dmnRegisteredExtensions).marshal(this.definitions);

        out.writeObject(output);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.serializedAs = (SerializationFormat) in.readObject();
        this.resource = (Resource) in.readObject();
        String xml = (String) in.readObject();
        
        if ( !(in instanceof DroolsObjectInputStream) ) {
            throw new UnsupportedOperationException();
            // TODO assume some defaults
        }
        
        DroolsObjectInputStream is = (DroolsObjectInputStream) in;
        DMNCompilerImpl compiler = (DMNCompilerImpl) is.getCustomExtensions().get(DMNAssemblerService.DMN_COMPILER_CACHE_KEY);
        List<DMNExtensionRegister> dmnRegisteredExtensions = compiler.getRegisteredExtensions();
        
        Definitions definitions = DMNMarshallerFactory.newMarshallerWithExtensions(dmnRegisteredExtensions).unmarshal(xml);
        
        this.definitions = definitions;
        
        DMNModelImpl compiledModel = (DMNModelImpl) compiler.compile(definitions);
        this.inputs    = compiledModel.inputs    ;
        this.decisions = compiledModel.decisions ;
        this.bkms      = compiledModel.bkms      ;
        this.itemDefs  = compiledModel.itemDefs  ;
        this.messages  = compiledModel.messages  ;
        this.types     = compiledModel.types     ;
    }

}
