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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalRuleBase;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.runtime.Environment;
import org.drools.runtime.process.ProcessInstance;
import org.hibernate.annotations.CollectionOfElements;
import org.jbpm.marshalling.impl.ProcessInstanceMarshaller;
import org.jbpm.marshalling.impl.ProcessMarshallerRegistry;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;

@Entity
public class ProcessInstanceInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    // TODO How do I mark a process instance info as dirty when the process
    // instance has changed (so that byte array is regenerated and saved) ?
    private @Lob
    byte[]                                    processInstanceByteArray;
    
//  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//  @JoinColumn(name = "processInstanceId")
//  private Set<EventType>                    eventTypes         = new HashSet<EventType>();    
    private @CollectionOfElements

    @JoinTable(name = "EventTypes", joinColumns = @JoinColumn(name = "InstanceId"))
    Set<String>                               eventTypes         = new HashSet<String>();
    private @Transient
    ProcessInstance                           processInstance;
    private @Transient
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
        lastReadDate = new Date();
    }

    public int getState() {
        return state;
    }

    public ProcessInstance getProcessInstance(InternalKnowledgeRuntime kruntime,
                                              Environment env) {
        this.env = env;
        if ( processInstance == null ) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( processInstanceByteArray );
                MarshallerReaderContext context = new MarshallerReaderContext( bais,
                                                                               (InternalRuleBase) ((InternalKnowledgeBase) kruntime.getKnowledgeBase()).getRuleBase(),
                                                                               null,
                                                                               null,
                                                                               this.env
                                                                              );
                ProcessInstanceMarshaller marshaller = getMarshallerFromContext( context );
                context.wm = ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory();
                processInstance = marshaller.readProcessInstance(context);
                context.close();
            } catch ( IOException e ) {
                e.printStackTrace();
                throw new IllegalArgumentException( "IOException while loading process instance: " + e.getMessage(),
                                                    e );
            }
        }
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

    @PreUpdate
    public void update() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean variablesChanged = false;
        try {
            MarshallerWriteContext context = new MarshallerWriteContext( baos,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         this.env );
            String processType = ((ProcessInstanceImpl) processInstance).getProcess().getType();
            saveProcessInstanceType( context,
                                     processInstance,
                                     processType );
            ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller( processType );
            
                        marshaller.writeProcessInstance( context,
                                                processInstance);
            context.close();
        } catch ( IOException e ) {
            throw new IllegalArgumentException( "IOException while storing process instance " + processInstance.getId() + ": " + e.getMessage() );
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
    
    public void clearProcessInstance(){
        processInstance = null;
    }
}
