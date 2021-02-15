/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.marshalling.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistry;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProcessMarshaller;
import org.drools.serialization.protobuf.PersisterHelper;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufMessages.Header;
import org.jbpm.marshalling.impl.JBPMMessages.Variable;
import org.jbpm.marshalling.impl.JBPMMessages.VariableContainer;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.internal.process.marshalling.KogitoObjectMarshallingStrategy;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.workitems.KogitoWorkItem;
import org.kie.kogito.process.workitems.KogitoWorkItemManager;
import org.kie.kogito.process.workitems.impl.KogitoWorkItemImpl;

public class ProtobufProcessMarshaller
        implements
        ProcessMarshaller {

	private static boolean persistWorkItemVars = Boolean.parseBoolean(System.getProperty("org.jbpm.wi.variable.persist", "true"));
	// mainly for testability as the setting is global
	public static void setWorkItemVarsPersistence(boolean turnOn) {
		persistWorkItemVars = turnOn;
	}

    public void writeProcessInstances(MarshallerWriteContext context) throws IOException {
        ProtobufMessages.ProcessData.Builder _pdata = (ProtobufMessages.ProcessData.Builder) context.getParameterObject();

        List<org.kie.api.runtime.process.ProcessInstance> processInstances = new ArrayList<>( context.getWorkingMemory().getProcessInstances() );
        Collections.sort( processInstances,
                          new Comparator<org.kie.api.runtime.process.ProcessInstance>() {
                              public int compare(org.kie.api.runtime.process.ProcessInstance o1,
                                                 org.kie.api.runtime.process.ProcessInstance o2) {
                                  return (( KogitoProcessInstance )o1).getStringId().compareTo((( KogitoProcessInstance )o2).getStringId());
                              }
                          } );

        for ( org.kie.api.runtime.process.ProcessInstance processInstance : processInstances ) {
            String processType = processInstance.getProcess().getType();
            JBPMMessages.ProcessInstance _instance = (JBPMMessages.ProcessInstance) ProcessMarshallerRegistry.INSTANCE.getMarshaller( processType )
                    .writeProcessInstance( context,
                                           processInstance );
            _pdata.addExtension( JBPMMessages.processInstance, _instance );
        }
    }

    public void writeWorkItems(MarshallerWriteContext context) throws IOException {
        ProtobufMessages.ProcessData.Builder _pdata = (ProtobufMessages.ProcessData.Builder) context.getParameterObject();

        List<WorkItem> workItems = new ArrayList<WorkItem>( ((KogitoWorkItemManager) context.getWorkingMemory().getWorkItemManager()).getWorkItems() );
        Collections.sort( workItems,
                          new Comparator<WorkItem>() {
                              public int compare(WorkItem o1,
                                                 WorkItem o2) {
                                  return (( KogitoWorkItem )o1).getStringId().compareTo((( KogitoWorkItem )o2).getStringId());
                              }
                          } );
        for ( WorkItem workItem : workItems ) {
            _pdata.addExtension( JBPMMessages.workItem,
                                 writeWorkItem( context,
                                                workItem ) );
        }
    }

    public static JBPMMessages.WorkItem writeWorkItem(MarshallerWriteContext context,
                                                      WorkItem workItem) throws IOException {
        return writeWorkItem( context, workItem, true );
    }

    public List<ProcessInstance> readProcessInstances(MarshallerReaderContext context) throws IOException {
        ProtobufMessages.ProcessData _pdata = (ProtobufMessages.ProcessData) context.getParameterObject();
        List<ProcessInstance> processInstanceList = new ArrayList<ProcessInstance>();
        for ( JBPMMessages.ProcessInstance _instance : _pdata.getExtension( JBPMMessages.processInstance ) ) {
            context.setParameterObject( _instance );
            ProcessInstance processInstance = ProcessMarshallerRegistry.INSTANCE.getMarshaller( _instance.getProcessType() ).readProcessInstance( context );
            ((WorkflowProcessInstanceImpl)processInstance).reconnect();
            processInstanceList.add( processInstance );
        }
        return processInstanceList;
    }

    public void readWorkItems(MarshallerReaderContext context) throws IOException {
        ProtobufMessages.ProcessData _pdata = (ProtobufMessages.ProcessData) context.getParameterObject();
        InternalWorkingMemory wm = context.getWorkingMemory();
        for ( JBPMMessages.WorkItem _workItem : _pdata.getExtension( JBPMMessages.workItem ) ) {
            WorkItem workItem = readWorkItem( context,
                                              _workItem );
            (( KogitoWorkItemManager ) wm.getWorkItemManager()).internalAddWorkItem( ( KogitoWorkItem ) workItem );
        }
    }

    public static JBPMMessages.WorkItem writeWorkItem(MarshallerWriteContext context,
                                                      WorkItem workItem,
                                                      boolean includeVariables) throws IOException {
        JBPMMessages.WorkItem.Builder _workItem = JBPMMessages.WorkItem.newBuilder()
                .setId( (( KogitoWorkItem )workItem).getStringId() )
                .setProcessInstancesId( (( KogitoWorkItem )workItem).getProcessInstanceStringId() )
                .setName( workItem.getName() )
                .setState( workItem.getState() );

        if (workItem instanceof KogitoWorkItem) {
        	if ((( KogitoWorkItem )workItem).getDeploymentId() != null){
        	_workItem.setDeploymentId((( KogitoWorkItem )workItem).getDeploymentId());
        	}
        	_workItem.setNodeId((( KogitoWorkItem )workItem).getNodeId())
        	.setNodeInstanceId((( KogitoWorkItem )workItem).getNodeInstanceStringId());
        }

        if ( includeVariables ) {
            Map<String, Object> parameters = workItem.getParameters();
            for ( Map.Entry<String, Object> entry : parameters.entrySet() ) {
                _workItem.addVariable( marshallVariable( context, entry.getKey(), entry.getValue() ) );
            }
        }
        return _workItem.build();
    }

    public static WorkItem readWorkItem(MarshallerReaderContext context,
                                        JBPMMessages.WorkItem _workItem ) throws IOException {
        return readWorkItem( context,
                             _workItem,
                             true );
    }

    public static WorkItem readWorkItem(MarshallerReaderContext context,
                                        JBPMMessages.WorkItem _workItem,
                                        boolean includeVariables) throws IOException {
        KogitoWorkItemImpl workItem = new KogitoWorkItemImpl();
        workItem.setId( _workItem.getId() );
        workItem.setProcessInstanceId( _workItem.getProcessInstancesId() );
        workItem.setName( _workItem.getName() );
        workItem.setState( _workItem.getState() );
        workItem.setDeploymentId(_workItem.getDeploymentId());
        workItem.setNodeId(_workItem.getNodeId());
        workItem.setNodeInstanceId(_workItem.getNodeInstanceId());

        if ( includeVariables ) {
            for ( JBPMMessages.Variable _variable : _workItem.getVariableList() ) {
                try {
                    Object value = unmarshallVariableValue( context, _variable );
                    workItem.setParameter( _variable.getName(),
                                           value );
                } catch ( ClassNotFoundException e ) {
                    throw new IllegalArgumentException( "Could not reload parameter " + _variable.getName() + " for work item " + _workItem );
                }
            }
        }

        return workItem;
    }

    public static Variable marshallVariable(MarshallerWriteContext context,
                                            String name,
                                            Object value) throws IOException {
        JBPMMessages.Variable.Builder builder = JBPMMessages.Variable.newBuilder().setName( name );
        if(value != null){
            KogitoObjectMarshallingStrategy strategy = (KogitoObjectMarshallingStrategy) context.getObjectMarshallingStrategyStore().getStrategyObject( value );
            Integer index = context.getStrategyIndex( strategy );
            builder.setStrategyIndex( index )
                   .setDataType(strategy.getType(value.getClass()))
                   .setValue( ByteString.copyFrom( strategy.marshal( context.getStrategyContext().get( strategy ),
                                                                     (ObjectOutputStream) context,
                                                                     value ) ) );
        }
        return builder.build();
    }

    public static Variable marshallVariablesMap(MarshallerWriteContext context, Map<String, Object> variables) throws IOException{
        Map<String, Variable> marshalledVariables = new HashMap<String, Variable>();
        for(String key : variables.keySet()){
            JBPMMessages.Variable.Builder builder = JBPMMessages.Variable.newBuilder().setName( key );
            Object variable = variables.get(key);
            if(variable != null){
                KogitoObjectMarshallingStrategy strategy = (KogitoObjectMarshallingStrategy) context.getObjectMarshallingStrategyStore().getStrategyObject( variable );
                Integer index = context.getStrategyIndex( strategy );
                builder.setStrategyIndex( index )
                    .setDataType(strategy.getType(variable.getClass()))
                   .setValue( ByteString.copyFrom( strategy.marshal( context.getStrategyContext().get( strategy ),
                                                                     ( ObjectOutputStream ) context,
                                                                     variable ) ) );

            }



            marshalledVariables.put(key, builder.build());
        }

        return marshallVariable(context, "variablesMap" ,marshalledVariables);
    }

    public static VariableContainer marshallVariablesContainer(MarshallerWriteContext context, Map<String, Object> variables) throws IOException{
    	JBPMMessages.VariableContainer.Builder vcbuilder = JBPMMessages.VariableContainer.newBuilder();
        for(String key : variables.keySet()){
            JBPMMessages.Variable.Builder builder = JBPMMessages.Variable.newBuilder().setName( key );
            if(variables.get(key) != null){
                ObjectMarshallingStrategy strategy = context.getObjectMarshallingStrategyStore().getStrategyObject( variables.get(key) );
                Integer index = context.getStrategyIndex( strategy );
                builder.setStrategyIndex( index )
                   .setValue( ByteString.copyFrom( strategy.marshal( context.getStrategyContext().get( strategy ),
                                                                     (ObjectOutputStream) context,
                                                                     variables.get(key) ) ) );

            }



            vcbuilder.addVariable(builder.build());
        }

        return vcbuilder.build();
    }

    public static Object unmarshallVariableValue(MarshallerReaderContext context,
                                                  JBPMMessages.Variable _variable) throws IOException,
                                                                                  ClassNotFoundException {
        if(_variable.getValue() == null || _variable.getValue().isEmpty()){
            return null;
        }
        KogitoObjectMarshallingStrategy strategy = (KogitoObjectMarshallingStrategy) context.getUsedStrategies().get( _variable.getStrategyIndex() );
        Object value = strategy.unmarshal( _variable.getDataType(), 
                                           context.getStrategyContexts().get( strategy ),
                                           ( ObjectInputStream ) context,
                                           _variable.getValue().toByteArray(),
                                           (context.getKnowledgeBase() == null)?null:context.getKnowledgeBase().getRootClassLoader() );
        return value;
    }

	public static Map<String, Object> unmarshallVariableContainerValue(MarshallerReaderContext context, JBPMMessages.VariableContainer _variableContiner)
			throws IOException, ClassNotFoundException {
		Map<String, Object> variables = new HashMap<String, Object>();
		if (_variableContiner.getVariableCount() == 0) {
			return variables;
		}

		for (Variable _variable : _variableContiner.getVariableList()) {

			Object value = ProtobufProcessMarshaller.unmarshallVariableValue(context, _variable);

			variables.put(_variable.getName(), value);
		}
		return variables;
	}

    public void init(MarshallerReaderContext context) {
        ExtensionRegistry registry = (ExtensionRegistry) context.getParameterObject();
        registry.add( JBPMMessages.processInstance );
        registry.add( JBPMMessages.processTimer );
        registry.add( JBPMMessages.procTimer );
        registry.add( JBPMMessages.workItem );
        registry.add( JBPMMessages.timerId );
    }

    @Override
    public void writeWorkItem(MarshallerWriteContext context, org.drools.core.process.instance.WorkItem workItem) {
        try {
            JBPMMessages.WorkItem _workItem = writeWorkItem(context, workItem, persistWorkItemVars);
            PersisterHelper.writeToStreamWithHeader( context, _workItem );
        } catch (IOException e) {
            throw new IllegalArgumentException( "IOException while storing work item instance "
                    + (( KogitoWorkItem ) workItem).getStringId() + ": " + e.getMessage(), e );
        }
    }

    @Override
    public KogitoWorkItem readWorkItem( MarshallerReaderContext context) {
        try {
            ExtensionRegistry registry = PersisterHelper.buildRegistry(context, null);
            Header _header = PersisterHelper.readFromStreamWithHeaderPreloaded(context, registry);
            JBPMMessages.WorkItem _workItem = JBPMMessages.WorkItem.parseFrom(_header.getPayload(), registry);
            return ( KogitoWorkItem ) readWorkItem(context, _workItem, persistWorkItemVars);
        } catch (IOException e) {
            throw new IllegalArgumentException( "IOException while fetching work item instance : " + e.getMessage(), e );
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException( "ClassNotFoundException while fetching work item instance : " + e.getMessage(), e );
        }
    }

    @Override
    public void writeProcessTimers(MarshallerWriteContext context) throws IOException {

    }

    @Override
    public void readProcessTimers(MarshallerReaderContext context) throws IOException, ClassNotFoundException {

    }
}
