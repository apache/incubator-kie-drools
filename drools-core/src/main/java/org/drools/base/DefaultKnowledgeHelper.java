package org.drools.base;

/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleFlowGroup;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.runtime.Channel;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.ProcessContext;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.common.LogicalDependency;
import org.drools.core.util.LinkedList;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;

public class DefaultKnowledgeHelper
    implements
    KnowledgeHelper,
    Externalizable {

    private static final long                   serialVersionUID = 400L;

    private Rule                                rule;
    private GroupElement                        subrule;
    private Activation                          activation;
    private Tuple                               tuple;
    private InternalWorkingMemoryActions        workingMemory;

    private IdentityHashMap<Object, FactHandle> identityMap;

    private LinkedList                          previousJustified;

    public DefaultKnowledgeHelper() {

    }

    public DefaultKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;

        this.identityMap = new IdentityHashMap<Object, FactHandle>();

    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        rule = (Rule) in.readObject();
        subrule = (GroupElement) in.readObject();
        activation = (Activation) in.readObject();
        tuple = (Tuple) in.readObject();
        workingMemory = (InternalWorkingMemoryActions) in.readObject();
        identityMap = (IdentityHashMap<Object, FactHandle>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( rule );
        out.writeObject( subrule );
        out.writeObject( activation );
        out.writeObject( tuple );
        out.writeObject( workingMemory );
        out.writeObject( identityMap );
    }

    public void setActivation(final Activation agendaItem) {
        this.rule = agendaItem.getRule();
        this.subrule = agendaItem.getSubRule();
        this.activation = agendaItem;
        // -- JBRULES-2558: logical inserts must be properly preserved
        this.previousJustified = agendaItem.getLogicalDependencies();
        agendaItem.setLogicalDependencies( null );
        // -- JBRULES-2558: end
        this.tuple = agendaItem.getTuple();
    }

    public void reset() {
        this.rule = null;
        this.subrule = null;
        this.activation = null;
        this.tuple = null;
        this.identityMap.clear();
        this.previousJustified = null;
    }

    public void insert(final Object object) throws FactException {
        insert( object,
                false );
    }

    public void insert(final Object object,
                       final boolean dynamic) throws FactException {
        FactHandle handle = this.workingMemory.insert( object,
                                                       dynamic,
                                                       false,
                                                       this.rule,
                                                       this.activation );
        this.getIdentityMap().put( object,
                                   handle );
    }

    public void insertLogical(final Object object) throws FactException {
        insertLogical( object,
                       false );
    }

    public void insertLogical(final Object object,
                              final boolean dynamic) throws FactException {
        // iterate to find previous equal logical insertion
        LogicalDependency dep = null;
        if ( this.previousJustified != null ) {
            for ( dep = (LogicalDependency) this.previousJustified.getFirst(); dep != null; dep = (LogicalDependency) dep.getNext() ) {
                if ( object.equals( ((InternalFactHandle) dep.getFactHandle()).getObject() ) ) {
                    this.previousJustified.remove( dep );
                    break;
                }
            }
        }

        if ( dep != null ) {
            // Add the previous matching logical dependency back into the list
            this.activation.addLogicalDependency( dep );
        } else {
            // no previous matching logical dependency, so create a new one
            FactHandle handle = this.workingMemory.insert( object,
                                                           dynamic,
                                                           true,
                                                           this.rule,
                                                           this.activation );

            this.getIdentityMap().put( object,
                                       handle );
        }
    }
    
    public void cancelRemainingPreviousLogicalDependencies() {
        if ( this.previousJustified != null ) {
            for ( LogicalDependency dep = (LogicalDependency) this.previousJustified.getFirst(); dep != null; dep = (LogicalDependency) dep.getNext() ) {
                this.workingMemory.getTruthMaintenanceSystem().removeLogicalDependency( activation, dep, activation.getPropagationContext() );
            }
        }
    }

    public void update(final FactHandle handle,
                       final Object newObject) throws FactException {
        // only update if this fact exists in the wm

        ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).update( handle,
                                                                                                  newObject,
                                                                                                  this.rule,
                                                                                                  this.activation );
        this.getIdentityMap().put( newObject,
                                   handle );
    }

    public void update(final Object object) throws FactException {
        FactHandle handle = getFactHandle( object );
        if ( handle == null ) {
            throw new FactException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        update( handle,
                object );
    }

    public void retract(final FactHandle handle) throws FactException {
        ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).retract( handle,
                                                                                                   true,
                                                                                                   true,
                                                                                                   this.rule,
                                                                                                   this.activation );
        this.getIdentityMap().remove( ((InternalFactHandle) handle).getObject() );
    }

    public void retract(final Object object) throws FactException {
        FactHandle handle = getFactHandle( object );
        if ( handle == null ) {
            throw new FactException( "Retract error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        retract( handle );
    }

    public Rule getRule() {
        return this.rule;
    }

    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public KnowledgeRuntime getKnowledgeRuntime() {
        return new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) this.workingMemory );
    }

    public Activation getActivation() {
        return this.activation;
    }

    public void setFocus(final String focus) {
        this.workingMemory.setFocus( focus );
    }

    public Object get(final Declaration declaration) {
        InternalWorkingMemoryEntryPoint wmTmp = ((InternalWorkingMemoryEntryPoint) (this.tuple.get( declaration )).getEntryPoint());

        if ( wmTmp != null ) {
            Object object = declaration.getValue( wmTmp.getInternalWorkingMemory(),
                                                  this.tuple.get( declaration ).getObject() );

            getIdentityMap().put( object,
                                  wmTmp.getFactHandleByIdentity( object ) );
            return object;
        }
        return null;
    }

    public Declaration getDeclaration(final String identifier) {
        return (Declaration) this.subrule.getOuterDeclarations().get( identifier );
    }

    public void halt() {
        this.workingMemory.halt();
    }

    public WorkingMemoryEntryPoint getEntryPoint(String id) {
        return this.workingMemory.getEntryPoints().get( id );
    }

    /**
     * @deprecated use {@link #getChannel(String)} instead
     */
    @Deprecated
    public ExitPoint getExitPoint(String id) {
        return this.workingMemory.getExitPoints().get( id );
    }
    
    public Channel getChannel(String id) {
        return this.workingMemory.getChannels().get( id );
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getEntryPoints() );
    }

    /**
     * @deprecated use {@link #getChannels()} instead
     */
    @Deprecated
    public Map<String, ExitPoint> getExitPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getExitPoints() );
    }
    
    public Map<String, Channel> getChannels() {
        return Collections.unmodifiableMap( this.workingMemory.getChannels() );
    }

    /**
     * @return the identityMap
     */
    public IdentityHashMap<Object, FactHandle> getIdentityMap() {
        return identityMap;
    }

    /**
     * @param identityMap the identityMap to set
     */
    public void setIdentityMap(IdentityHashMap<Object, FactHandle> identityMap) {
        this.identityMap = identityMap;
    }

    private FactHandle getFactHandle(final Object object) {
        FactHandle handle = identityMap.get( object );
        // entry point null means it is a generated fact, not a regular inserted fact
        // NOTE: it would probably be a good idea to create a specific attribute for that
        if ( handle == null || ((InternalFactHandle) handle).getEntryPoint() == null ) {
            for ( WorkingMemoryEntryPoint ep : workingMemory.getEntryPoints().values() ) {
                handle = (FactHandle) ep.getFactHandle( object );
                if ( handle != null ) {
                    identityMap.put( object,
                                     handle );
                    break;
                }
            }
        }
        return handle;
    }
    
    @SuppressWarnings("unchecked")
	public <T> T getContext(Class<T> contextClass) {
    	if (ProcessContext.class.equals(contextClass)) {
    		String ruleflowGroupName = getActivation().getRule().getRuleFlowGroup();
    		if (ruleflowGroupName != null) {
    			Map<Long, String> nodeInstances = ((InternalRuleFlowGroup) workingMemory.getAgenda().getRuleFlowGroup(ruleflowGroupName)).getNodeInstances();
    			if (!nodeInstances.isEmpty()) {
    				if (nodeInstances.size() > 1) {
    					// TODO
    					throw new UnsupportedOperationException(
							"Not supporting multiple node instances for the same ruleflow group");
    				}
    				Map.Entry<Long, String> entry = nodeInstances.entrySet().iterator().next();
    				ProcessInstance processInstance = workingMemory.getProcessInstance(entry.getKey());
    				org.drools.spi.ProcessContext context = new org.drools.spi.ProcessContext();
    				context.setProcessInstance((org.drools.process.instance.ProcessInstance) processInstance);
    				String nodeInstance = entry.getValue();
    				String[] nodeInstanceIds = nodeInstance.split(":");
    				NodeInstanceContainer container = (WorkflowProcessInstance) processInstance;
    				for (int i = 0; i < nodeInstanceIds.length; i++) {
    					for (NodeInstance subNodeInstance: container.getNodeInstances()) {
    						if (subNodeInstance.getId() == new Long(nodeInstanceIds[i])) {
    							if (i == nodeInstanceIds.length - 1) {
    								context.setNodeInstance(subNodeInstance);
    								break;
    							} else {
    								container = (NodeInstanceContainer) subNodeInstance;
    							}
    						}
    					}
    				}
    				return (T) context;
    			}
    		}
    	}
    	return null;
    }

}
