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

import com.google.protobuf.ExtensionRegistry;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufMessages.Header;
import org.jbpm.marshalling.impl.JBPMMessages.ProcessInstance.NodeInstanceContent;
import org.jbpm.marshalling.impl.JBPMMessages.ProcessInstance.NodeInstanceContent.RuleSetNode.TextMapEntry;
import org.jbpm.marshalling.impl.JBPMMessages.ProcessInstance.NodeInstanceType;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.context.exclusive.ExclusiveGroup;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.context.exclusive.ExclusiveGroupInstance;
import org.jbpm.process.instance.context.swimlane.SwimlaneContextInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.AsyncEventNodeInstance;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.instance.node.EventSubProcessNodeInstance;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.jbpm.workflow.instance.node.JoinInstance;
import org.jbpm.workflow.instance.node.MilestoneNodeInstance;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.jbpm.workflow.instance.node.StateNodeInstance;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of a process instance marshaller.
 * 
 */
public abstract class AbstractProtobufProcessInstanceMarshaller
        implements
        ProcessInstanceMarshaller {

    // Output methods
    public JBPMMessages.ProcessInstance writeProcessInstance(MarshallerWriteContext context,
                                                             ProcessInstance processInstance) throws IOException {
        WorkflowProcessInstanceImpl workFlow = (WorkflowProcessInstanceImpl) processInstance;
        
        JBPMMessages.ProcessInstance.Builder _instance = JBPMMessages.ProcessInstance.newBuilder()
                .setId( workFlow.getId() )
                .setProcessId( workFlow.getProcessId() )
                .setState( workFlow.getState() )
                .setNodeInstanceCounter( workFlow.getNodeInstanceCounter() )
                .setProcessType( workFlow.getProcess().getType() )
                .setParentProcessInstanceId(workFlow.getParentProcessInstanceId())
                .setSignalCompletion(workFlow.isSignalCompletion())
                .setSlaCompliance(workFlow.getSlaCompliance());
        if (workFlow.getProcessXml() != null) {
            _instance.setProcessXml( workFlow.getProcessXml());
        }
        if (workFlow.getDescription() != null) {
            _instance.setDescription(workFlow.getDescription());
        }
        if (workFlow.getDeploymentId() != null) {
            _instance.setDeploymentId(workFlow.getDeploymentId());
        }
        _instance.addAllCompletedNodeIds(workFlow.getCompletedNodeIds());
        if (workFlow.getCorrelationKey() != null) {
            _instance.setCorrelationKey(workFlow.getCorrelationKey());
        }
        if (workFlow.getSlaDueDate() != null) {
            _instance.setSlaDueDate(workFlow.getSlaDueDate().getTime());
        }
        if (workFlow.getSlaTimerId() != null) {
            _instance.setSlaTimerId(workFlow.getSlaTimerId());
        }

        SwimlaneContextInstance swimlaneContextInstance = (SwimlaneContextInstance) workFlow.getContextInstance( SwimlaneContext.SWIMLANE_SCOPE );
        if ( swimlaneContextInstance != null ) {
            Map<String, String> swimlaneActors = swimlaneContextInstance.getSwimlaneActors();
            for ( Map.Entry<String, String> entry : swimlaneActors.entrySet() ) {
                _instance.addSwimlaneContext( JBPMMessages.ProcessInstance.SwimlaneContextInstance.newBuilder()
                        .setSwimlane( entry.getKey() )
                        .setActorId( entry.getValue() )
                        .build() );
            }
        }

        List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>( workFlow.getNodeInstances() );
        Collections.sort( nodeInstances,
                          new Comparator<NodeInstance>() {

                              public int compare(NodeInstance o1,
                                                 NodeInstance o2) {
                                  return (int) (o1.getId() - o2.getId());
                              }
                          } );
        for ( NodeInstance nodeInstance : nodeInstances ) {
            _instance.addNodeInstance( writeNodeInstance( context,
                                                          nodeInstance ) );
        }

        List<ContextInstance> exclusiveGroupInstances =
                workFlow.getContextInstances( ExclusiveGroup.EXCLUSIVE_GROUP );
        if ( exclusiveGroupInstances != null ) {
            for ( ContextInstance contextInstance : exclusiveGroupInstances ) {
                JBPMMessages.ProcessInstance.ExclusiveGroupInstance.Builder _exclusive = JBPMMessages.ProcessInstance.ExclusiveGroupInstance.newBuilder();
                ExclusiveGroupInstance exclusiveGroupInstance = (ExclusiveGroupInstance) contextInstance;
                Collection<NodeInstance> groupNodeInstances = exclusiveGroupInstance.getNodeInstances();
                for ( NodeInstance nodeInstance : groupNodeInstances ) {
                    _exclusive.addGroupNodeInstanceId( nodeInstance.getId() );
                }
                _instance.addExclusiveGroup( _exclusive.build() );
            }
        }

        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) workFlow.getContextInstance( VariableScope.VARIABLE_SCOPE );
        List<Map.Entry<String, Object>> variables = new ArrayList<Map.Entry<String, Object>>( variableScopeInstance.getVariables().entrySet() );
        Collections.sort( variables,
                          new Comparator<Map.Entry<String, Object>>() {
                              public int compare(Map.Entry<String, Object> o1,
                                                 Map.Entry<String, Object> o2) {
                                  return o1.getKey().compareTo( o2.getKey() );
                              }
                          } );

        for ( Map.Entry<String, Object> variable : variables ) {
            if ( variable.getValue() != null ) {
                _instance.addVariable( ProtobufProcessMarshaller.marshallVariable( context, variable.getKey(), variable.getValue() ) );
            }
        }
        
        List<Map.Entry<String, Integer>> iterationlevels = new ArrayList<Map.Entry<String, Integer>>( workFlow.getIterationLevels().entrySet() );
        Collections.sort( iterationlevels,
                          new Comparator<Map.Entry<String, Integer>>() {
                              public int compare(Map.Entry<String, Integer> o1,
                                                 Map.Entry<String, Integer> o2) {
                                  return o1.getKey().compareTo( o2.getKey() );
                              }
                          } );

        for ( Map.Entry<String, Integer> level : iterationlevels ) {
            if ( level.getValue() != null ) {
                _instance.addIterationLevels( 
                        JBPMMessages.IterationLevel.newBuilder()
                        .setId(level.getKey())
                        .setLevel(level.getValue()) );
            }
        }
        
        return _instance.build();
    }

    public JBPMMessages.ProcessInstance.NodeInstance writeNodeInstance(MarshallerWriteContext context,
                                                                       NodeInstance nodeInstance) throws IOException {
        JBPMMessages.ProcessInstance.NodeInstance.Builder _node = JBPMMessages.ProcessInstance.NodeInstance.newBuilder()
                .setId( nodeInstance.getId() )
                .setNodeId( nodeInstance.getNodeId())
                .setLevel(((org.jbpm.workflow.instance.NodeInstance)nodeInstance).getLevel())
                .setSlaCompliance(((org.jbpm.workflow.instance.NodeInstance)nodeInstance).getSlaCompliance());
                        
        if (((org.jbpm.workflow.instance.NodeInstance)nodeInstance).getSlaDueDate() != null) {
            _node.setSlaDueDate(((org.jbpm.workflow.instance.NodeInstance)nodeInstance).getSlaDueDate().getTime());
        }
        if (((org.jbpm.workflow.instance.NodeInstance)nodeInstance).getSlaTimerId() != null) {
            _node.setSlaTimerId(((org.jbpm.workflow.instance.NodeInstance)nodeInstance).getSlaTimerId());
        }
        
        _node.setContent( writeNodeInstanceContent( _node, 
                                                    nodeInstance, 
                                                    context ) );
        return _node.build();
    }

    protected JBPMMessages.ProcessInstance.NodeInstanceContent writeNodeInstanceContent(JBPMMessages.ProcessInstance.NodeInstance.Builder _node,
                                                                                        NodeInstance nodeInstance,
                                                                                        MarshallerWriteContext context) throws IOException {
        JBPMMessages.ProcessInstance.NodeInstanceContent.Builder _content = null;
        if ( nodeInstance instanceof RuleSetNodeInstance ) {
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.RULE_SET_NODE );
            List<Long> timerInstances =
                    ((RuleSetNodeInstance) nodeInstance).getTimerInstances();
            JBPMMessages.ProcessInstance.NodeInstanceContent.RuleSetNode.Builder _ruleSet = JBPMMessages.ProcessInstance.NodeInstanceContent.RuleSetNode.newBuilder();
            _ruleSet.setRuleFlowGroup(((RuleSetNodeInstance) nodeInstance).getRuleFlowGroup());
            if ( timerInstances != null ) {
                
                for ( Long id : timerInstances ) {
                    _ruleSet.addTimerInstanceId( id );
                }
            }
            
           Map<String, FactHandle> facts = ((RuleSetNodeInstance) nodeInstance).getFactHandles();
           if (facts != null && facts.size() > 0) {
               for (Map.Entry<String, FactHandle> entry : facts.entrySet()) {
                   JBPMMessages.ProcessInstance.NodeInstanceContent.RuleSetNode.TextMapEntry.Builder _textMapEntry = JBPMMessages.ProcessInstance.NodeInstanceContent.RuleSetNode.TextMapEntry.newBuilder();
                   _textMapEntry.setName(entry.getKey());
                   _textMapEntry.setValue(entry.getValue().toExternalForm());
                   
                   _ruleSet.addMapEntry(_textMapEntry.build());
               }
           }
           _content.setRuleSet( _ruleSet.build() );
           
        } else if ( nodeInstance instanceof HumanTaskNodeInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.HumanTaskNode.Builder _task = JBPMMessages.ProcessInstance.NodeInstanceContent.HumanTaskNode.newBuilder()
                    .setWorkItemId( ((HumanTaskNodeInstance) nodeInstance).getWorkItemId() );
            List<Long> timerInstances =
                    ((HumanTaskNodeInstance) nodeInstance).getTimerInstances();
            if ( timerInstances != null ) {
                for ( Long id : timerInstances ) {
                    _task.addTimerInstanceId( id );
                }
            }
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.HUMAN_TASK_NODE )
                    .setHumanTask( _task.build() );
        } else if ( nodeInstance instanceof WorkItemNodeInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.WorkItemNode.Builder _wi = JBPMMessages.ProcessInstance.NodeInstanceContent.WorkItemNode.newBuilder()
                    .setWorkItemId( ((WorkItemNodeInstance) nodeInstance).getWorkItemId() );
            
            List<Long> timerInstances =
                    ((WorkItemNodeInstance) nodeInstance).getTimerInstances();
            if ( timerInstances != null ) {
                for ( Long id : timerInstances ) {
                    _wi.addTimerInstanceId( id );
                }
            }
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.WORK_ITEM_NODE )
                    .setWorkItem( _wi.build() );
        } else if ( nodeInstance instanceof SubProcessNodeInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.SubProcessNode.Builder _sp = JBPMMessages.ProcessInstance.NodeInstanceContent.SubProcessNode.newBuilder()
                    .setProcessInstanceId( ((SubProcessNodeInstance) nodeInstance).getProcessInstanceId() );
            List<Long> timerInstances =
                    ((SubProcessNodeInstance) nodeInstance).getTimerInstances();
            if ( timerInstances != null ) {
                for ( Long id : timerInstances ) {
                    _sp.addTimerInstanceId( id );
                }
            }
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.SUBPROCESS_NODE )
                    .setSubProcess( _sp.build() );
        } else if ( nodeInstance instanceof MilestoneNodeInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.MilestoneNode.Builder _ms = JBPMMessages.ProcessInstance.NodeInstanceContent.MilestoneNode.newBuilder();
            List<Long> timerInstances =
                    ((MilestoneNodeInstance) nodeInstance).getTimerInstances();
            if ( timerInstances != null ) {
                for ( Long id : timerInstances ) {
                    _ms.addTimerInstanceId( id );
                }
            }
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.MILESTONE_NODE )
                    .setMilestone( _ms.build() );
        } else if ( nodeInstance instanceof AsyncEventNodeInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.AsyncEventNode.Builder _asyncEvent = JBPMMessages.ProcessInstance.NodeInstanceContent.AsyncEventNode.newBuilder();
            _asyncEvent.setEventType(((AsyncEventNodeInstance) nodeInstance).getEventType());
                    
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.ASYNC_EVENT_NODE )
                    .setAsyncEvent(_asyncEvent.build());
            
        } else if ( nodeInstance instanceof EventNodeInstance ) {
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.EVENT_NODE );
        } else if ( nodeInstance instanceof TimerNodeInstance ) {
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.TIMER_NODE )
                    .setTimer( JBPMMessages.ProcessInstance.NodeInstanceContent.TimerNode.newBuilder()
                               .setTimerId( ((TimerNodeInstance) nodeInstance).getTimerId() )
                               .build() );
        } else if ( nodeInstance instanceof JoinInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.JoinNode.Builder _join = JBPMMessages.ProcessInstance.NodeInstanceContent.JoinNode.newBuilder();
            Map<Long, Integer> triggers = ((JoinInstance) nodeInstance).getTriggers();
            List<Long> keys = new ArrayList<Long>( triggers.keySet() );
            Collections.sort( keys,
                              new Comparator<Long>() {
                                  public int compare(Long o1,
                                                     Long o2) {
                                      return o1.compareTo( o2 );
                                  }
                              } );
            for ( Long key : keys ) {
                _join.addTrigger( JBPMMessages.ProcessInstance.NodeInstanceContent.JoinNode.JoinTrigger.newBuilder()
                                  .setNodeId( key )
                                  .setCounter( triggers.get( key ) )
                                  .build() );
            }
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.JOIN_NODE )
                    .setJoin( _join.build() );
        } else if ( nodeInstance instanceof StateNodeInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.StateNode.Builder _state = JBPMMessages.ProcessInstance.NodeInstanceContent.StateNode.newBuilder();
            List<Long> timerInstances =
                    ((StateNodeInstance) nodeInstance).getTimerInstances();
            if ( timerInstances != null ) {
                for ( Long id : timerInstances ) {
                    _state.addTimerInstanceId( id );
                }
            }
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.STATE_NODE )
                    .setState( _state.build() );
        } else if ( nodeInstance instanceof ForEachNodeInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.ForEachNode.Builder _foreach = JBPMMessages.ProcessInstance.NodeInstanceContent.ForEachNode.newBuilder();
            ForEachNodeInstance forEachNodeInstance = (ForEachNodeInstance) nodeInstance;
            List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>( forEachNodeInstance.getNodeInstances() );
            Collections.sort( nodeInstances,
                              new Comparator<NodeInstance>() {
                                  public int compare(NodeInstance o1,
                                                     NodeInstance o2) {
                                      return (int) (o1.getId() - o2.getId());
                                  }
                              } );
            for ( NodeInstance subNodeInstance : nodeInstances ) {
                if ( subNodeInstance instanceof CompositeContextNodeInstance ) {
                    _foreach.addNodeInstance( writeNodeInstance( context,
                                                                 subNodeInstance ) );
                }
            }
            
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance) forEachNodeInstance.getContextInstance( VariableScope.VARIABLE_SCOPE);
            if ( variableScopeInstance != null ) {
                List<Map.Entry<String, Object>> variables = new ArrayList<Map.Entry<String, Object>>( variableScopeInstance.getVariables().entrySet() );
                Collections.sort( variables,
                                  new Comparator<Map.Entry<String, Object>>() {
                                      public int compare(Map.Entry<String, Object> o1,
                                                         Map.Entry<String, Object> o2) {
                                          return o1.getKey().compareTo( o2.getKey() );
                                      }
                                  } );
                for ( Map.Entry<String, Object> variable : variables ) {
                    
                    _foreach.addVariable( ProtobufProcessMarshaller.marshallVariable( context, variable.getKey(), variable.getValue() ) );
                }
            }
            
            List<Map.Entry<String, Integer>> iterationlevels = new ArrayList<Map.Entry<String, Integer>>( forEachNodeInstance.getIterationLevels().entrySet() );
            Collections.sort( iterationlevels,
                              new Comparator<Map.Entry<String, Integer>>() {
                                  public int compare(Map.Entry<String, Integer> o1,
                                                     Map.Entry<String, Integer> o2) {
                                      return o1.getKey().compareTo( o2.getKey() );
                                  }
                              } );

            for ( Map.Entry<String, Integer> level : iterationlevels ) {
                if ( level.getKey() != null && level.getValue() != null ) {
                    _foreach.addIterationLevels( 
                            JBPMMessages.IterationLevel.newBuilder()
                            .setId(level.getKey())
                            .setLevel(level.getValue()) );
                }
            }
            
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( NodeInstanceType.FOR_EACH_NODE )
                    .setForEach( _foreach.build() );
        } else if ( nodeInstance instanceof CompositeContextNodeInstance ) {
            JBPMMessages.ProcessInstance.NodeInstanceContent.CompositeContextNode.Builder _composite = JBPMMessages.ProcessInstance.NodeInstanceContent.CompositeContextNode.newBuilder();
            JBPMMessages.ProcessInstance.NodeInstanceType _type = null;
            if (nodeInstance instanceof DynamicNodeInstance) {
                _type = JBPMMessages.ProcessInstance.NodeInstanceType.DYNAMIC_NODE;
            } else if (nodeInstance instanceof EventSubProcessNodeInstance) {
                _type = JBPMMessages.ProcessInstance.NodeInstanceType.EVENT_SUBPROCESS_NODE;
            } else {
                _type = JBPMMessages.ProcessInstance.NodeInstanceType.COMPOSITE_CONTEXT_NODE;
            }

            CompositeContextNodeInstance compositeNodeInstance = (CompositeContextNodeInstance) nodeInstance;
            List<Long> timerInstances =
                    ((CompositeContextNodeInstance) nodeInstance).getTimerInstances();
            if ( timerInstances != null ) {
                for ( Long id : timerInstances ) {
                    _composite.addTimerInstanceId( id );
                }
            }
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance) compositeNodeInstance.getContextInstance( VariableScope.VARIABLE_SCOPE );
            if ( variableScopeInstance != null ) {
                List<Map.Entry<String, Object>> variables = new ArrayList<Map.Entry<String, Object>>( variableScopeInstance.getVariables().entrySet() );
                Collections.sort( variables,
                                  new Comparator<Map.Entry<String, Object>>() {
                                      public int compare(Map.Entry<String, Object> o1,
                                                         Map.Entry<String, Object> o2) {
                                          return o1.getKey().compareTo( o2.getKey() );
                                      }
                                  } );
                for ( Map.Entry<String, Object> variable : variables ) {
                    
                    _composite.addVariable( ProtobufProcessMarshaller.marshallVariable( context, variable.getKey(), variable.getValue() ) );
                }
            }
            
            List<Map.Entry<String, Integer>> iterationlevels = new ArrayList<Map.Entry<String, Integer>>( compositeNodeInstance.getIterationLevels().entrySet() );
            Collections.sort( iterationlevels,
                              new Comparator<Map.Entry<String, Integer>>() {
                                  public int compare(Map.Entry<String, Integer> o1,
                                                     Map.Entry<String, Integer> o2) {
                                      return o1.getKey().compareTo( o2.getKey() );
                                  }
                              } );

            for ( Map.Entry<String, Integer> level : iterationlevels ) {
                if (level.getKey() != null && level.getValue() != null ) {
                    _composite.addIterationLevels( 
                            JBPMMessages.IterationLevel.newBuilder()
                            .setId(level.getKey())
                            .setLevel(level.getValue()) );
                }
            }
            
            List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>( compositeNodeInstance.getNodeInstances() );
            Collections.sort( nodeInstances,
                              new Comparator<NodeInstance>() {
                                  public int compare(NodeInstance o1,
                                                     NodeInstance o2) {
                                      return (int) (o1.getId() - o2.getId());
                                  }
                              } );
            for ( NodeInstance subNodeInstance : nodeInstances ) {
                _composite.addNodeInstance( writeNodeInstance( context,
                                                               subNodeInstance ) );
            }
            List<ContextInstance> exclusiveGroupInstances =
                    compositeNodeInstance.getContextInstances( ExclusiveGroup.EXCLUSIVE_GROUP );
            if ( exclusiveGroupInstances != null ) {
                for ( ContextInstance contextInstance : exclusiveGroupInstances ) {
                    JBPMMessages.ProcessInstance.ExclusiveGroupInstance.Builder _excl = JBPMMessages.ProcessInstance.ExclusiveGroupInstance.newBuilder();
                    ExclusiveGroupInstance exclusiveGroupInstance = (ExclusiveGroupInstance) contextInstance;
                    Collection<NodeInstance> groupNodeInstances = exclusiveGroupInstance.getNodeInstances();
                    for ( NodeInstance groupNodeInstance : groupNodeInstances ) {
                        _excl.addGroupNodeInstanceId( groupNodeInstance.getId() );
                    }
                    _composite.addExclusiveGroup( _excl.build() );
                }
            }
            _content = JBPMMessages.ProcessInstance.NodeInstanceContent.newBuilder()
                    .setType( _type )
                    .setComposite( _composite.build() );
        } else {
            throw new IllegalArgumentException( "Unknown node instance type: " + nodeInstance );
        }
        return _content.build();
    }

    // Input methods
    public ProcessInstance readProcessInstance(MarshallerReaderContext context) throws IOException {
        InternalKnowledgeBase ruleBase = context.kBase;
        InternalWorkingMemory wm = context.wm;
        
        JBPMMessages.ProcessInstance _instance = (org.jbpm.marshalling.impl.JBPMMessages.ProcessInstance) context.parameterObject;
        if( _instance == null ) {
            // try to parse from the stream
            ExtensionRegistry registry = PersisterHelper.buildRegistry( context, null ); 
            Header _header;
            try {

                _header = PersisterHelper.readFromStreamWithHeaderPreloaded(context, registry);
            } catch ( ClassNotFoundException e ) {
                // Java 5 does not accept [new IOException(String, Throwable)]
                IOException ioe =  new IOException( "Error deserializing process instance." );
                ioe.initCause(e);
                throw ioe;
            }
            _instance = JBPMMessages.ProcessInstance.parseFrom( _header.getPayload(), registry );
        }

        WorkflowProcessInstanceImpl processInstance = createProcessInstance();
        processInstance.setId( _instance.getId() );
        String processId = _instance.getProcessId();
        processInstance.setProcessId( processId );
        String processXml = _instance.getProcessXml();
        Process process = null;
        if (processXml != null && processXml.trim().length() > 0) {
        	processInstance.setProcessXml( processXml );
        	process = processInstance.getProcess();
        } else {
            process = ruleBase.getProcess( processId );
            if (process == null) {
            	throw new RuntimeException("Could not find process " + processId + " when restoring process instance " + processInstance.getId());
            }
            processInstance.setProcess( process );
        }
        processInstance.setDescription(_instance.getDescription());
        processInstance.setState( _instance.getState() );
        processInstance.setParentProcessInstanceId(_instance.getParentProcessInstanceId());
        processInstance.setSignalCompletion(_instance.getSignalCompletion());
        processInstance.setDeploymentId(_instance.getDeploymentId());
        processInstance.setCorrelationKey(_instance.getCorrelationKey());
        processInstance.internalSetSlaCompliance(_instance.getSlaCompliance());
        if (_instance.getSlaDueDate() > 0) {
            processInstance.internalSetSlaDueDate(new Date(_instance.getSlaDueDate()));
        }
        processInstance.internalSetSlaTimerId(_instance.getSlaTimerId());
        
        long nodeInstanceCounter = _instance.getNodeInstanceCounter();
        processInstance.setKnowledgeRuntime( wm.getKnowledgeRuntime() );
        processInstance.internalSetNodeInstanceCounter( nodeInstanceCounter );
        for( String completedNodeId : _instance.getCompletedNodeIdsList() ) { 
            processInstance.addCompletedNodeId(completedNodeId);
        }

        if ( _instance.getSwimlaneContextCount() > 0 ) {
            Context swimlaneContext = ((org.jbpm.process.core.Process) process).getDefaultContext( SwimlaneContext.SWIMLANE_SCOPE );
            SwimlaneContextInstance swimlaneContextInstance = (SwimlaneContextInstance) processInstance.getContextInstance( swimlaneContext );
            for ( JBPMMessages.ProcessInstance.SwimlaneContextInstance _swimlane : _instance.getSwimlaneContextList() ) {
                swimlaneContextInstance.setActorId( _swimlane.getSwimlane(), _swimlane.getActorId() );
            }
        }

        for ( JBPMMessages.ProcessInstance.NodeInstance _node : _instance.getNodeInstanceList() ) {
            context.parameterObject = _node;
            readNodeInstance( context, 
                              processInstance, 
                              processInstance );
        }

        for ( JBPMMessages.ProcessInstance.ExclusiveGroupInstance _excl : _instance.getExclusiveGroupList() ) {
            ExclusiveGroupInstance exclusiveGroupInstance = new ExclusiveGroupInstance();
            processInstance.addContextInstance( ExclusiveGroup.EXCLUSIVE_GROUP, exclusiveGroupInstance );
            for ( Long nodeInstanceId : _excl.getGroupNodeInstanceIdList() ) {
                NodeInstance nodeInstance = ((org.jbpm.workflow.instance.NodeInstanceContainer)processInstance).getNodeInstance( nodeInstanceId, true );
                if ( nodeInstance == null ) {
                    throw new IllegalArgumentException( "Could not find node instance when deserializing exclusive group instance: " + nodeInstanceId );
                }
                exclusiveGroupInstance.addNodeInstance( nodeInstance );
            }
        }

        if ( _instance.getVariableCount() > 0 ) {
            Context variableScope = ((org.jbpm.process.core.Process) process)
                    .getDefaultContext( VariableScope.VARIABLE_SCOPE );
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance) processInstance
                    .getContextInstance( variableScope );
            for ( JBPMMessages.Variable _variable : _instance.getVariableList() ) {
                try {
                    Object _value = ProtobufProcessMarshaller.unmarshallVariableValue( context, _variable );
                    variableScopeInstance.internalSetVariable( _variable.getName(), 
                                                               _value );
                } catch ( ClassNotFoundException e ) {
                    throw new IllegalArgumentException( "Could not reload variable " + _variable.getName() );
                }
            }
        }
        
        if ( _instance.getIterationLevelsCount() > 0 ) {
            
            for ( JBPMMessages.IterationLevel _level : _instance.getIterationLevelsList()) {
                processInstance.getIterationLevels().put(_level.getId(), _level.getLevel());
            }
        }        
    	processInstance.reconnect();
        return processInstance;
    }

    protected abstract WorkflowProcessInstanceImpl createProcessInstance();

    public NodeInstance readNodeInstance(MarshallerReaderContext context,
                                         NodeInstanceContainer nodeInstanceContainer,
                                         WorkflowProcessInstance processInstance) throws IOException {
        JBPMMessages.ProcessInstance.NodeInstance _node = (JBPMMessages.ProcessInstance.NodeInstance) context.parameterObject;
        
        NodeInstanceImpl nodeInstance = readNodeInstanceContent( _node,
                                                                 context, 
                                                                 processInstance);

        nodeInstance.setNodeId( _node.getNodeId() );                
        nodeInstance.setId( _node.getId() );
        nodeInstance.setNodeInstanceContainer( nodeInstanceContainer );
        nodeInstance.setProcessInstance( (org.jbpm.workflow.instance.WorkflowProcessInstance) processInstance );
        nodeInstance.setLevel(_node.getLevel()==0?1:_node.getLevel());
        nodeInstance.internalSetSlaCompliance(_node.getSlaCompliance());
        if (_node.getSlaDueDate() > 0) {
            nodeInstance.internalSetSlaDueDate(new Date(_node.getSlaDueDate()));
        }
        nodeInstance.internalSetSlaTimerId(_node.getSlaTimerId());

        switch ( _node.getContent().getType() ) {
            case COMPOSITE_CONTEXT_NODE :
            	
            case DYNAMIC_NODE :
                if ( _node.getContent().getComposite().getVariableCount() > 0 ) {
                    Context variableScope = ((org.jbpm.process.core.Process) ((org.jbpm.process.instance.ProcessInstance)
                            processInstance).getProcess()).getDefaultContext( VariableScope.VARIABLE_SCOPE );
                    VariableScopeInstance variableScopeInstance = (VariableScopeInstance) ((CompositeContextNodeInstance) nodeInstance).getContextInstance( variableScope );
                    for ( JBPMMessages.Variable _variable : _node.getContent().getComposite().getVariableList() ) {
                        try {
                            Object _value = ProtobufProcessMarshaller.unmarshallVariableValue( context, _variable );
                            variableScopeInstance.internalSetVariable( _variable.getName(), _value );
                        } catch ( ClassNotFoundException e ) {
                            throw new IllegalArgumentException( "Could not reload variable " + _variable.getName() );
                        }
                    }
                }
                if ( _node.getContent().getComposite().getIterationLevelsCount() > 0 ) {
                    
                    for ( JBPMMessages.IterationLevel _level : _node.getContent().getComposite().getIterationLevelsList()) {
                        ((CompositeContextNodeInstance) nodeInstance).getIterationLevels().put(_level.getId(), _level.getLevel());
                    }
                }
                for ( JBPMMessages.ProcessInstance.NodeInstance _instance : _node.getContent().getComposite().getNodeInstanceList() ) {
                    context.parameterObject = _instance;
                    readNodeInstance( context,
                                      (CompositeContextNodeInstance) nodeInstance,
                                      processInstance );
                }

                for ( JBPMMessages.ProcessInstance.ExclusiveGroupInstance _excl : _node.getContent().getComposite().getExclusiveGroupList() ) {
                    ExclusiveGroupInstance exclusiveGroupInstance = new ExclusiveGroupInstance();
                    ((CompositeContextNodeInstance) nodeInstance).addContextInstance( ExclusiveGroup.EXCLUSIVE_GROUP, exclusiveGroupInstance );
                    for ( Long nodeInstanceId : _excl.getGroupNodeInstanceIdList() ) {
                        NodeInstance groupNodeInstance = ((org.jbpm.workflow.instance.NodeInstanceContainer)processInstance).getNodeInstance( nodeInstanceId, true );
                        if ( groupNodeInstance == null ) {
                            throw new IllegalArgumentException( "Could not find node instance when deserializing exclusive group instance: " + nodeInstanceId );
                        }
                        exclusiveGroupInstance.addNodeInstance( groupNodeInstance );
                    }
                }
                break;
            case FOR_EACH_NODE :
                for ( JBPMMessages.ProcessInstance.NodeInstance _instance : _node.getContent().getForEach().getNodeInstanceList() ) {
                    context.parameterObject = _instance;
                    readNodeInstance( context,
                                      (ForEachNodeInstance) nodeInstance,
                                      processInstance );
                    VariableScopeInstance variableScopeInstance = (VariableScopeInstance) ((ForEachNodeInstance) nodeInstance).getContextInstance( VariableScope.VARIABLE_SCOPE );
                    for ( JBPMMessages.Variable _variable : _node.getContent().getForEach().getVariableList() ) {
                        try {
                            Object _value = ProtobufProcessMarshaller.unmarshallVariableValue( context, _variable );
                            variableScopeInstance.internalSetVariable( _variable.getName(), _value );
                        } catch ( ClassNotFoundException e ) {
                            throw new IllegalArgumentException( "Could not reload variable " + _variable.getName() );
                        }
                    }
                    if ( _node.getContent().getForEach().getIterationLevelsCount() > 0 ) {
                        
                        for ( JBPMMessages.IterationLevel _level : _node.getContent().getForEach().getIterationLevelsList()) {
                            ((ForEachNodeInstance) nodeInstance).getIterationLevels().put(_level.getId(), _level.getLevel());
                        }
                    }
                }
                break;
            case EVENT_SUBPROCESS_NODE :
                for ( JBPMMessages.ProcessInstance.NodeInstance _instance : _node.getContent().getComposite().getNodeInstanceList() ) {
                    context.parameterObject = _instance;
                    readNodeInstance( context,
                                      (EventSubProcessNodeInstance) nodeInstance,
                                      processInstance );
                    VariableScopeInstance variableScopeInstance = (VariableScopeInstance) ((EventSubProcessNodeInstance) nodeInstance).getContextInstance( VariableScope.VARIABLE_SCOPE );
                    for ( JBPMMessages.Variable _variable : _node.getContent().getComposite().getVariableList() ) {
                        try {
                            Object _value = ProtobufProcessMarshaller.unmarshallVariableValue( context, _variable );
                            variableScopeInstance.internalSetVariable( _variable.getName(), _value );
                        } catch ( ClassNotFoundException e ) {
                            throw new IllegalArgumentException( "Could not reload variable " + _variable.getName() );
                        }
                    }
                }
                break;
            default :
                // do nothing
        }

        return nodeInstance;
    }

    protected NodeInstanceImpl readNodeInstanceContent(JBPMMessages.ProcessInstance.NodeInstance _node,
                                                       MarshallerReaderContext context,
                                                       WorkflowProcessInstance processInstance) throws IOException {
        NodeInstanceImpl nodeInstance = null;
        NodeInstanceContent _content = _node.getContent();
        switch ( _content.getType() ) {
            case  RULE_SET_NODE:
                nodeInstance = new RuleSetNodeInstance();
                ((RuleSetNodeInstance) nodeInstance).setRuleFlowGroup(_content.getRuleSet().getRuleFlowGroup());
                if ( _content.getRuleSet().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getRuleSet().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((RuleSetNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }
                
                if (_content.getRuleSet().getMapEntryCount() > 0) {
                    Map<String, FactHandle> factInfo = new HashMap<String, FactHandle>();
                    
                    for (TextMapEntry entry : _content.getRuleSet().getMapEntryList()) {
                        factInfo.put(entry.getName(), DefaultFactHandle.createFromExternalFormat(entry.getValue()));
                    }
                    
                    ((RuleSetNodeInstance) nodeInstance).setFactHandles(factInfo);
                }
                break;
            case HUMAN_TASK_NODE :
                nodeInstance = new HumanTaskNodeInstance();
                ((HumanTaskNodeInstance) nodeInstance).internalSetWorkItemId( _content.getHumanTask().getWorkItemId() );
                if ( _content.getHumanTask().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getHumanTask().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((HumanTaskNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }
                break;
            case WORK_ITEM_NODE :
                nodeInstance = new WorkItemNodeInstance();
                ((WorkItemNodeInstance) nodeInstance).internalSetWorkItemId( _content.getWorkItem().getWorkItemId() );
                if ( _content.getWorkItem().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getWorkItem().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((WorkItemNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }
                break;
            case SUBPROCESS_NODE :
                nodeInstance = new SubProcessNodeInstance();
                ((SubProcessNodeInstance) nodeInstance).internalSetProcessInstanceId( _content.getSubProcess().getProcessInstanceId() );
                if ( _content.getSubProcess().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getSubProcess().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((SubProcessNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }
                break;
            case MILESTONE_NODE :
                nodeInstance = new MilestoneNodeInstance();
                if ( _content.getMilestone().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getMilestone().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((MilestoneNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }
                break;
            case TIMER_NODE :
                nodeInstance = new TimerNodeInstance();
                ((TimerNodeInstance) nodeInstance).internalSetTimerId( _content.getTimer().getTimerId() );
                break;
            case ASYNC_EVENT_NODE :
                nodeInstance = new AsyncEventNodeInstance();
                ((AsyncEventNodeInstance) nodeInstance).setEventType(_content.getAsyncEvent().getEventType());
                break;
            case EVENT_NODE :
                nodeInstance = new EventNodeInstance();
                break;
            case JOIN_NODE :
                nodeInstance = new JoinInstance();
                if ( _content.getJoin().getTriggerCount() > 0 ) {
                    Map<Long, Integer> triggers = new HashMap<Long, Integer>();
                    for ( JBPMMessages.ProcessInstance.NodeInstanceContent.JoinNode.JoinTrigger _join : _content.getJoin().getTriggerList() ) {
                        triggers.put( _join.getNodeId(),
                                      _join.getCounter() );
                    }
                    ((JoinInstance) nodeInstance).internalSetTriggers( triggers );
                }
                break;
            case FOR_EACH_NODE :
                nodeInstance = new ForEachNodeInstance();
                break;
            case COMPOSITE_CONTEXT_NODE :
                nodeInstance = new CompositeContextNodeInstance();
                
                if ( _content.getComposite().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getComposite().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((CompositeContextNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }                
                break;
            case DYNAMIC_NODE :
                nodeInstance = new DynamicNodeInstance();
                if ( _content.getComposite().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getComposite().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((CompositeContextNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }
                break;
            case STATE_NODE :
                nodeInstance = new StateNodeInstance();
                if ( _content.getState().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getState().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((CompositeContextNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }
                break;
            case EVENT_SUBPROCESS_NODE :
                nodeInstance = new EventSubProcessNodeInstance();
                
                if ( _content.getComposite().getTimerInstanceIdCount() > 0 ) {
                    List<Long> timerInstances = new ArrayList<Long>();
                    for ( Long _timerId : _content.getComposite().getTimerInstanceIdList() ) {
                        timerInstances.add( _timerId );
                    }
                    ((CompositeContextNodeInstance) nodeInstance).internalSetTimerInstances( timerInstances );
                }
                break;
            default :
                throw new IllegalArgumentException( "Unknown node type: " + _content.getType() );
        }
        return nodeInstance;

    }
}
