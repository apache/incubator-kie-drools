package org.drools.workflow.instance.impl;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.Agenda;
import org.drools.common.EventSupport;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.core.timer.Timer;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemListener;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.process.instance.timer.TimerListener;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.WorkflowProcess;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.node.EventNodeInstance;

/**
 * Default implementation of a RuleFlow process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class WorkflowProcessInstanceImpl extends ProcessInstanceImpl
        implements WorkflowProcessInstance {

    private static final long serialVersionUID = 400L;

    private final List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>();;
    private long nodeInstanceCounter = 0;
    private List<WorkItemListener> workItemListeners = new CopyOnWriteArrayList<WorkItemListener>();
    private List<TimerListener> timerListeners = new CopyOnWriteArrayList<TimerListener>();

    public NodeContainer getNodeContainer() {
        return getWorkflowProcess();
    }

    public void addNodeInstance(final NodeInstance nodeInstance) {
        ((NodeInstanceImpl) nodeInstance).setId(nodeInstanceCounter++);
        this.nodeInstances.add(nodeInstance);
    }

    public void removeNodeInstance(final NodeInstance nodeInstance) {
        this.nodeInstances.remove(nodeInstance);
    }

    public Collection<NodeInstance> getNodeInstances() {
        return getNodeInstances(false);
    }
    
    public Collection<NodeInstance> getNodeInstances(boolean recursive) {
        Collection<NodeInstance> result = nodeInstances;
        if (recursive) {
            result = new ArrayList<NodeInstance>(result);
            for (Iterator<NodeInstance> iterator = nodeInstances.iterator(); iterator.hasNext(); ) {
                NodeInstance nodeInstance = iterator.next();
                if (nodeInstance instanceof NodeInstanceContainer) {
                    result.addAll(((NodeInstanceContainer) nodeInstance).getNodeInstances(true));
                }
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    public NodeInstance getFirstNodeInstance(final long nodeId) {
        for ( final Iterator<NodeInstance> iterator = this.nodeInstances.iterator(); iterator.hasNext(); ) {
            final NodeInstance nodeInstance = iterator.next();
            if ( nodeInstance.getNodeId() == nodeId ) {
                return nodeInstance;
            }
        }
        return null;
    }

    public NodeInstance getNodeInstance(final Node node) {
        NodeInstanceFactoryRegistry nodeRegistry =
            ((InternalRuleBase) getWorkingMemory().getRuleBase())
                .getConfiguration().getProcessNodeInstanceFactoryRegistry();
        NodeInstanceFactory conf = nodeRegistry.getProcessNodeInstanceFactory(node);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal node type: " + node.getClass());
        }
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) conf.getNodeInstance(node, this, this);
        if (nodeInstance == null) {
            throw new IllegalArgumentException("Illegal node type: " + node.getClass());
        }
        return nodeInstance;
    }

    public Agenda getAgenda() {
        if ( getWorkingMemory() == null ) {
            return null;
        }
        return getWorkingMemory().getAgenda();
    }

    public WorkflowProcess getWorkflowProcess() {
        return (WorkflowProcess) getProcess();
    }

    public void setState(final int state) {
        super.setState( state );
        // TODO move most of this to ProcessInstanceImpl
        if ( state == ProcessInstanceImpl.STATE_COMPLETED ) {
            InternalWorkingMemory workingMemory = (InternalWorkingMemory) getWorkingMemory();
            ((EventSupport) getWorkingMemory()).getRuleFlowEventSupport()
                .fireBeforeRuleFlowProcessCompleted( this, workingMemory );
            // deactivate all node instances of this process instance
            while ( !nodeInstances.isEmpty() ) {
                NodeInstance nodeInstance = (NodeInstance) nodeInstances.get( 0 );
                nodeInstance.cancel();
            }
            workingMemory.removeProcessInstance( this );
            ((EventSupport) workingMemory).getRuleFlowEventSupport()
                .fireAfterRuleFlowProcessCompleted( this, workingMemory );
        }
    }

    public void disconnect() {
        for (NodeInstance nodeInstance: nodeInstances) {
            if (nodeInstance instanceof EventNodeInstance) {
                ((EventNodeInstance) nodeInstance).removeEventListeners();
            }
        }
        super.disconnect();
    }
    
    public void reconnect() {
        super.reconnect();
        for (NodeInstance nodeInstance: nodeInstances) {
            if (nodeInstance instanceof EventNodeInstance) {
                ((EventNodeInstance) nodeInstance).addEventListeners();
            }
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer( "WorkflowProcessInstance" );
        sb.append( getId() );
        sb.append( " [processId=" );
        sb.append( getProcess().getId() );
        sb.append( ",state=" );
        sb.append( getState() );
        sb.append( "]" );
        return sb.toString();
    }

    public void workItemCompleted(WorkItem workItem) {
        for (WorkItemListener listener: workItemListeners) {
            listener.workItemCompleted(workItem);
        }
    }

    public void workItemAborted(WorkItem workItem) {
        for (WorkItemListener listener: workItemListeners) {
            listener.workItemCompleted(workItem);
        }
    }
    
    public void addWorkItemListener(WorkItemListener listener) {
        workItemListeners.add(listener);
    }
    
    public void removeWorkItemListener(WorkItemListener listener) {
        workItemListeners.remove(listener);
    }
    
    public void timerTriggered(Timer timer) {
        for (TimerListener listener: timerListeners) {
            listener.timerTriggered(timer);
        }
    }

    public void addTimerListener(TimerListener listener) {
        timerListeners.add(listener);
    }
    
    public void removeTimerListener(TimerListener listener) {
        timerListeners.remove(listener);
    }
    
}
