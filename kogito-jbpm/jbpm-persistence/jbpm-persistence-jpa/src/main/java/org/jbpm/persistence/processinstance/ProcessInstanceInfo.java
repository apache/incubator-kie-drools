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

package org.jbpm.persistence.processinstance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProcessMarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.drools.persistence.api.Transformable;
import org.jbpm.marshalling.impl.JBPMMessages;
import org.jbpm.marshalling.impl.ProcessInstanceMarshaller;
import org.jbpm.marshalling.impl.ProcessMarshallerRegistry;
import org.jbpm.marshalling.impl.ProtobufRuleFlowProcessInstanceMarshaller;
import org.jbpm.persistence.api.PersistentProcessInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.process.ProcessInstance;

@Entity
@SequenceGenerator(name="processInstanceInfoIdSeq", sequenceName="PROCESS_INSTANCE_INFO_ID_SEQ")
public class ProcessInstanceInfo implements PersistentProcessInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="processInstanceInfoIdSeq")
    @Column(name = "InstanceId")
    private Long                              processInstanceId;

    @Version
    @Column(name = "OPTLOCK")
    private int                               version;

    private String                            processId;
    private Date                              startDate;
    private Date                              lastReadDate;
    private Date                              lastModificationDate;
    private int                               state;
    
    @Lob
    @Column(length=2147483647)
    byte[]                                    processInstanceByteArray;

    @ElementCollection
    @CollectionTable(name="EventTypes", joinColumns=@JoinColumn(name="InstanceId"))
    @Column(name="element")
    private Set<String>                       eventTypes         = new HashSet<String>();
    
    @Transient
    ProcessInstance                           processInstance;
    
    @Transient
    Environment                               env;

    protected ProcessInstanceInfo() {
    }

    public ProcessInstanceInfo(ProcessInstance processInstance) {
        this.processInstance = processInstance;
        this.processId = processInstance.getProcessId();
        startDate = new Date();
    }

    public ProcessInstanceInfo(ProcessInstance processInstance,
                               Environment env) {
        this(processInstance);
        this.env = env;
    }

    /**
     * Added in order to satisfy Hibernate AND the JBPMorm.xml:<ul>
     * <li> Hibernate needs getter/setters for a the field that's mapped.
     *   <ul><li>(field access is inefficient/dangerous, and not necessary)</li></ul></li>
     * <li>The JBPMorm.xml queries reference .processInstanceId as well.</li>
     * </ul>
     * If we mapped the field using 'name="id"', the queries would thus fail.
     * </p>
     * So instead of that, we just add the getters and use 'name="processInstanceId"'.
     * @return The processInstanceId field value. 
     */
    public Long getProcessInstanceId() { 
        return processInstanceId;
    }
    
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getId() {
        return processInstanceId;
    }
    
    public void setId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public Date getLastReadDate() {
        return lastReadDate;
    }

    public void updateLastReadDate() {
    	Date updateTo = new Date();
    	if (lastReadDate == null || lastReadDate.compareTo(updateTo) < 0) {
    		lastReadDate = updateTo;
    	} else {
    		lastReadDate = new Date(lastReadDate.getTime() + 1);
    	}
    }

    public int getState() {
        return state;
    }

    public ProcessInstance getProcessInstance(InternalKnowledgeRuntime kruntime,
                                              Environment env) {
    	return getProcessInstance(kruntime, env, false);
    }
    
    public ProcessInstance getProcessInstance(InternalKnowledgeRuntime kruntime,
                                              Environment env,
                                              boolean readOnly) {
        this.env = env;
        if ( processInstance == null ) {        	
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( processInstanceByteArray );
                MarshallerReaderContext context = new MarshallerReaderContext( bais,
                                                                               (InternalKnowledgeBase) kruntime.getKieBase(),
                                                                               null,
                                                                               null,
                                                                               ProtobufMarshaller.TIMER_READERS,
                                                                               this.env
                                                                              );
                ProcessInstanceMarshaller marshaller = getMarshallerFromContext( context );
            	context.wm = ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory();
                processInstance = marshaller.readProcessInstance(context);
                ((WorkflowProcessInstanceImpl) processInstance).setPersisted(false);
                if (readOnly) {
                    ((WorkflowProcessInstanceImpl) processInstance).disconnect();
                }
                context.close();
            } catch ( IOException e ) {
                e.printStackTrace();
                throw new IllegalArgumentException( "IOException while loading process instance: " + e.getMessage(),
                                                    e );
            }
        }
        ((WorkflowProcessInstanceImpl) processInstance).internalSetStartDate(this.startDate);
        return processInstance;
    }
   
    private ProcessInstanceMarshaller getMarshallerFromContext(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        String processInstanceType = stream.readUTF();
        return ProcessMarshallerRegistry.INSTANCE.getMarshaller( processInstanceType );
    }

    private void saveProcessInstanceType(MarshallerWriteContext context,
                                         ProcessInstance processInstance,
                                         String processInstanceType) throws IOException {
        ObjectOutputStream stream = context.stream;
        // saves the processInstance type first
        stream.writeUTF( processInstanceType );
    }

    public void transform() {
//    	if (processInstance == null) {
//    		return;
//    	}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean variablesChanged = false;
        try {
            ProcessMarshallerWriteContext context = new ProcessMarshallerWriteContext( baos,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         this.env );
            context.setProcessInstanceId(processInstance.getId());
            context.setState(processInstance.getState() == ProcessInstance.STATE_ACTIVE ? 
                    ProcessMarshallerWriteContext.STATE_ACTIVE:ProcessMarshallerWriteContext.STATE_COMPLETED);
            
            String processType = ((ProcessInstanceImpl) processInstance).getProcess().getType();
            saveProcessInstanceType( context,
                                     processInstance,
                                     processType );
            ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller( processType );
            
            Object result = marshaller.writeProcessInstance( context,
                                                             processInstance);
            if( marshaller instanceof ProtobufRuleFlowProcessInstanceMarshaller && result != null ) {
                JBPMMessages.ProcessInstance _instance = (JBPMMessages.ProcessInstance)result;
                PersisterHelper.writeToStreamWithHeader( context, 
                                                         _instance );
            }
            context.close();
        } catch ( IOException e ) {
            throw new IllegalArgumentException( "IOException while storing process instance "
        		+ processInstance.getId() + ": " + e.getMessage(), e );
        }
        byte[] newByteArray = baos.toByteArray();
        if ( variablesChanged || !Arrays.equals( newByteArray,
                                                 processInstanceByteArray ) ) {
            this.state = processInstance.getState();
            this.lastModificationDate = new Date();
            this.processInstanceByteArray = newByteArray;
            this.eventTypes.clear();
            for ( String type : processInstance.getEventTypes() ) {
                eventTypes.add( type );
            }
        }
        if (!processInstance.getProcessId().equals(this.processId)) {
    		this.processId = processInstance.getProcessId();
    	}
        ((WorkflowProcessInstanceImpl) processInstance).setPersisted(true);
    }


    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final ProcessInstanceInfo other = (ProcessInstanceInfo) obj;
        if ( this.processInstanceId != other.processInstanceId && (this.processInstanceId == null || !this.processInstanceId.equals( other.processInstanceId )) ) {
            return false;
        }
        if ( this.version != other.version ) {
            return false;
        }
        if ( (this.processId == null) ? (other.processId != null) : !this.processId.equals( other.processId ) ) {
            return false;
        }
        if ( this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals( other.startDate )) ) {
            return false;
        }
        if ( this.lastReadDate != other.lastReadDate && (this.lastReadDate == null || !this.lastReadDate.equals( other.lastReadDate )) ) {
            return false;
        }
        if ( this.lastModificationDate != other.lastModificationDate && (this.lastModificationDate == null || !this.lastModificationDate.equals( other.lastModificationDate )) ) {
            return false;
        }
        if ( this.state != other.state ) {
            return false;
        }
        if ( !Arrays.equals( this.processInstanceByteArray,
                             other.processInstanceByteArray ) ) {
            return false;
        }
        if ( this.eventTypes != other.eventTypes && (this.eventTypes == null || !this.eventTypes.equals( other.eventTypes )) ) {
            return false;
        }
        if ( this.processInstance != other.processInstance && (this.processInstance == null || !this.processInstance.equals( other.processInstance )) ) {
            return false;
        }
        if ( this.env != other.env && (this.env == null || !this.env.equals( other.env )) ) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.processInstanceId != null ? this.processInstanceId.hashCode() : 0);
        hash = 61 * hash + this.version;
        hash = 61 * hash + (this.processId != null ? this.processId.hashCode() : 0);
        hash = 61 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 61 * hash + (this.lastReadDate != null ? this.lastReadDate.hashCode() : 0);
        hash = 61 * hash + (this.lastModificationDate != null ? this.lastModificationDate.hashCode() : 0);
        hash = 61 * hash + this.state;
        hash = 61 * hash + Arrays.hashCode( this.processInstanceByteArray );
        hash = 61 * hash + (this.eventTypes != null ? this.eventTypes.hashCode() : 0);
        hash = 61 * hash + (this.processInstance != null ? this.processInstance.hashCode() : 0);
        hash = 61 * hash + (this.env != null ? this.env.hashCode() : 0);
        return hash;
    }

    public int getVersion() {
        return version;
    }
    
    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public byte [] getProcessInstanceByteArray() { 
        return processInstanceByteArray;
    }
    
    public void clearProcessInstance(){
        processInstance = null;
    }
    
    public Environment getEnv() { 
        return env;
    }
    
    public void setEnv(Environment env) { 
        this.env = env;
    }
}
