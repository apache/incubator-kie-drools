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
import org.drools.WorkingMemory;
import org.drools.common.EventSupport;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemListener;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.WorkflowProcess;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;

/**
 * Default implementation of a RuleFlow process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class WorkflowProcessInstanceImpl extends ProcessInstanceImpl
        implements WorkflowProcessInstance {

    private static final long serialVersionUID = 400L;

    private InternalWorkingMemory workingMemory;
    private final List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>();;
    private long nodeInstanceCounter = 0;
    private List<WorkItemListener> workItemListeners = new CopyOnWriteArrayList<WorkItemListener>();

    public WorkflowProcess getWorkflowProcess() {
        return (WorkflowProcess) getProcess();
    }

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
            ((InternalRuleBase) this.workingMemory.getRuleBase())
                .getConfiguration().getProcessNodeInstanceFactoryRegistry();
        NodeInstanceFactory conf = nodeRegistry.getProcessNodeInstanceFactory(node);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal node type: " + node.getClass());
        }
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) conf.getNodeInstance(node, this, this);
        nodeInstance.setNodeId(node.getId());
        nodeInstance.setNodeInstanceContainer(this);
        nodeInstance.setProcessInstance(this);
        if (nodeInstance == null) {
            throw new IllegalArgumentException("Illegal node type: " + node.getClass());
        }
        return nodeInstance;
    }

    public Agenda getAgenda() {
        if ( this.workingMemory == null ) {
            return null;
        }
        return this.workingMemory.getAgenda();
    }

    public void setWorkingMemory(final InternalWorkingMemory workingMemory) {
        if ( this.workingMemory != null ) {
            throw new IllegalArgumentException( "A working memory can only be set once." );
        }
        this.workingMemory = workingMemory;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }
    
    public void setState(final int state) {
        super.setState( state );
        if ( state == ProcessInstanceImpl.STATE_COMPLETED ) {
            ((EventSupport) this.workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowProcessCompleted( this,
                                                                                                              this.workingMemory );
            // deactivate all node instances of this process instance
            while ( !nodeInstances.isEmpty() ) {
                NodeInstance nodeInstance = (NodeInstance) nodeInstances.get( 0 );
                nodeInstance.cancel();
            }
            workingMemory.removeProcessInstance( this );
            ((EventSupport) this.workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowProcessCompleted( this,
                                                                                                             this.workingMemory );
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
    
}
