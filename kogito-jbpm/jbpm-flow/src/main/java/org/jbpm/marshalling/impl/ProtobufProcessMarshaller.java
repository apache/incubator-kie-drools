/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.marshalling.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProcessMarshaller;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.Header;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.marshalling.impl.JBPMMessages.ProcessTimer.TimerInstance.Builder;
import org.jbpm.marshalling.impl.JBPMMessages.Variable;
import org.jbpm.marshalling.impl.JBPMMessages.VariableContainer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;
import org.jbpm.process.instance.timer.TimerManager.StartProcessJobContext;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;

import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistry;

public class ProtobufProcessMarshaller
        implements
        ProcessMarshaller {
	
	private static boolean persistWorkItemVars = Boolean.parseBoolean(System.getProperty("org.jbpm.wi.variable.persist", "true"));
	// mainly for testability as the setting is global
	public static void setWorkItemVarsPersistence(boolean turnOn) {
		persistWorkItemVars = turnOn;
	}

    public void writeProcessInstances(MarshallerWriteContext context) throws IOException {
        ProtobufMessages.ProcessData.Builder _pdata = (ProtobufMessages.ProcessData.Builder) context.parameterObject;
                                                  
        List<org.kie.api.runtime.process.ProcessInstance> processInstances = new ArrayList<org.kie.api.runtime.process.ProcessInstance>( context.wm.getProcessInstances() );
        Collections.sort( processInstances,
                          new Comparator<org.kie.api.runtime.process.ProcessInstance>() {
                              public int compare(org.kie.api.runtime.process.ProcessInstance o1,
                                                 org.kie.api.runtime.process.ProcessInstance o2) {
                                  return (int) (o1.getId() - o2.getId());
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

    public void writeProcessTimers(MarshallerWriteContext outCtx) throws IOException {
        outCtx.writersByClass.put( ProcessJobContext.class, new TimerManager.ProcessTimerOutputMarshaller() );
        outCtx.writersByClass.put( StartProcessJobContext.class, new TimerManager.ProcessTimerOutputMarshaller() );
        ProtobufMessages.ProcessData.Builder _pdata = (ProtobufMessages.ProcessData.Builder) outCtx.parameterObject;

        TimerManager timerManager = ((InternalProcessRuntime) ((InternalWorkingMemory) outCtx.wm).getProcessRuntime()).getTimerManager();
        long timerId = timerManager.internalGetTimerId();

        _pdata.setExtension( JBPMMessages.timerId, timerId );
    }

    public void writeWorkItems(MarshallerWriteContext context) throws IOException {
        ProtobufMessages.ProcessData.Builder _pdata = (ProtobufMessages.ProcessData.Builder) context.parameterObject;
        
        List<WorkItem> workItems = new ArrayList<WorkItem>( ((WorkItemManager) context.wm.getWorkItemManager()).getWorkItems() );
        Collections.sort( workItems,
                          new Comparator<WorkItem>() {
                              public int compare(WorkItem o1,
                                                 WorkItem o2) {
                                  return (int) (o2.getId() - o1.getId());
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
        ProtobufMessages.ProcessData _pdata = (ProtobufMessages.ProcessData) context.parameterObject;
        List<ProcessInstance> processInstanceList = new ArrayList<ProcessInstance>();
        for ( JBPMMessages.ProcessInstance _instance : _pdata.getExtension( JBPMMessages.processInstance ) ) {
            context.parameterObject = _instance;
            ProcessInstance processInstance = ProcessMarshallerRegistry.INSTANCE.getMarshaller( _instance.getProcessType() ).readProcessInstance( context );
            processInstanceList.add( processInstance );
        }
        return processInstanceList;
    }

    public void readWorkItems(MarshallerReaderContext context) throws IOException {
        ProtobufMessages.ProcessData _pdata = (ProtobufMessages.ProcessData) context.parameterObject;
        InternalWorkingMemory wm = context.wm;
        for ( JBPMMessages.WorkItem _workItem : _pdata.getExtension( JBPMMessages.workItem ) ) {
            WorkItem workItem = readWorkItem( context,
                                              _workItem );
            ((WorkItemManager) wm.getWorkItemManager()).internalAddWorkItem( (org.drools.core.process.instance.WorkItem) workItem );
        }
    }

    public void readProcessTimers(MarshallerReaderContext inCtx) throws IOException,
                                                                ClassNotFoundException {
        inCtx.readersByInt.put( ProtobufMessages.Timers.TimerType.PROCESS_VALUE, new TimerManager.ProcessTimerInputMarshaller() );
        ProtobufMessages.ProcessData _pdata = (ProtobufMessages.ProcessData) inCtx.parameterObject;

        TimerManager timerManager = ((InternalProcessRuntime) ((InternalWorkingMemory) inCtx.wm).getProcessRuntime()).getTimerManager();
        timerManager.internalSetTimerId( _pdata.getExtension( JBPMMessages.timerId ) );
//
//        int token;
//        while ( (token = inCtx.readShort()) != PersisterEnums.END ) {
//            switch ( token ) {
//                case PersisterEnums.TIMER : {
//                    TimerInstance timer = readTimer( inCtx );
//                    timerManager.internalAddTimer( timer );
//                    break;
//                }
//                case PersisterEnums.DEFAULT_TIMER : {
//                    InputMarshaller.readTimer( inCtx );
//                    break;
//                }
//            }
//        }
    }

    public static JBPMMessages.ProcessTimer.TimerInstance writeTimer(MarshallerWriteContext context,
                                                                     TimerInstance timer) {
        Builder _timer = JBPMMessages.ProcessTimer.TimerInstance.newBuilder()
                .setId( timer.getId() )
                .setTimerId( timer.getTimerId() )
                .setSessionId( timer.getSessionId() )
                .setDelay( timer.getDelay() )
                .setPeriod( timer.getPeriod() )
                .setProcessInstanceId( timer.getProcessInstanceId() )
                .setActivatedTime( timer.getActivated().getTime() )
                .setRepeatLimit(timer.getRepeatLimit());
        Date lastTriggered = timer.getLastTriggered();
        if ( lastTriggered != null ) {
            _timer.setLastTriggered( lastTriggered.getTime() );
        }
        return _timer.build();
    }

    public static TimerInstance readTimer(MarshallerReaderContext context, 
                                          JBPMMessages.ProcessTimer.TimerInstance _timer) {
        TimerInstance timer = new TimerInstance();
        timer.setId( _timer.getId());
        timer.setTimerId( _timer.getTimerId() );
        timer.setDelay( _timer.getDelay() );
        timer.setPeriod( _timer.getPeriod() );
        timer.setProcessInstanceId( _timer.getProcessInstanceId() );
        if (_timer.hasDEPRECATEDSessionId()) {
        	timer.setSessionId( _timer.getDEPRECATEDSessionId() );
        } else {
        	timer.setSessionId( _timer.getSessionId() );
        }
        timer.setActivated( new Date( _timer.getActivatedTime() ) );
        if ( _timer.hasLastTriggered() ) {
            timer.setLastTriggered( new Date( _timer.getLastTriggered() ) );
        }
        timer.setRepeatLimit(_timer.getRepeatLimit());
        return timer;
    }

    public static JBPMMessages.WorkItem writeWorkItem(MarshallerWriteContext context,
                                                      WorkItem workItem,
                                                      boolean includeVariables) throws IOException {
        JBPMMessages.WorkItem.Builder _workItem = JBPMMessages.WorkItem.newBuilder()
                .setId( workItem.getId() )
                .setProcessInstancesId( workItem.getProcessInstanceId() )
                .setName( workItem.getName() )
                .setState( workItem.getState() );
        
        if (workItem instanceof org.drools.core.process.instance.WorkItem) {
        	if (((org.drools.core.process.instance.WorkItem)workItem).getDeploymentId() != null){
        	_workItem.setDeploymentId(((org.drools.core.process.instance.WorkItem)workItem).getDeploymentId());
        	}
        	_workItem.setNodeId(((org.drools.core.process.instance.WorkItem)workItem).getNodeId())
        	.setNodeInstanceId(((org.drools.core.process.instance.WorkItem)workItem).getNodeInstanceId());
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
        WorkItemImpl workItem = new WorkItemImpl();
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
            ObjectMarshallingStrategy strategy = context.objectMarshallingStrategyStore.getStrategyObject( value );
            Integer index = context.getStrategyIndex( strategy );
            builder.setStrategyIndex( index )
                   .setValue( ByteString.copyFrom( strategy.marshal( context.strategyContext.get( strategy ),
                                                                     context,
                                                                     value ) ) );
        }
        return builder.build();
    }
    
    public static Variable marshallVariablesMap(MarshallerWriteContext context, Map<String, Object> variables) throws IOException{
        Map<String, Variable> marshalledVariables = new HashMap<String, Variable>();
        for(String key : variables.keySet()){
            JBPMMessages.Variable.Builder builder = JBPMMessages.Variable.newBuilder().setName( key );
            if(variables.get(key) != null){
                ObjectMarshallingStrategy strategy = context.objectMarshallingStrategyStore.getStrategyObject( variables.get(key) );
                Integer index = context.getStrategyIndex( strategy );
                builder.setStrategyIndex( index )
                   .setValue( ByteString.copyFrom( strategy.marshal( context.strategyContext.get( strategy ),
                                                                     context,
                                                                     variables.get(key) ) ) );
                
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
                ObjectMarshallingStrategy strategy = context.objectMarshallingStrategyStore.getStrategyObject( variables.get(key) );
                Integer index = context.getStrategyIndex( strategy );
                builder.setStrategyIndex( index )
                   .setValue( ByteString.copyFrom( strategy.marshal( context.strategyContext.get( strategy ),
                                                                     context,
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
        ObjectMarshallingStrategy strategy = context.usedStrategies.get( _variable.getStrategyIndex() );
        Object value = strategy.unmarshal( context.strategyContexts.get( strategy ),
                                           context,
                                           _variable.getValue().toByteArray(), 
                                           (context.kBase == null)?null:context.kBase.getRootClassLoader() );
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
        ExtensionRegistry registry = (ExtensionRegistry) context.parameterObject;
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
                    + workItem.getId() + ": " + e.getMessage(), e );
        }
    }

    @Override
    public org.drools.core.process.instance.WorkItem readWorkItem(MarshallerReaderContext context) {
        try {
            ExtensionRegistry registry = PersisterHelper.buildRegistry(context, null);
            Header _header = PersisterHelper.readFromStreamWithHeaderPreloaded(context, registry);
            JBPMMessages.WorkItem _workItem = JBPMMessages.WorkItem.parseFrom(_header.getPayload(), registry); 
            return (org.drools.core.process.instance.WorkItem) readWorkItem(context, _workItem, persistWorkItemVars);
        } catch (IOException e) {
            throw new IllegalArgumentException( "IOException while fetching work item instance : " + e.getMessage(), e );
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException( "ClassNotFoundException while fetching work item instance : " + e.getMessage(), e );
        }
    }

}
