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

import javax.persistence.PreUpdate;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalRuleBase;
import org.drools.event.ProcessEventSupport;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.PersisterHelper;
import org.drools.marshalling.impl.ProtobufMarshaller;
import org.kie.runtime.Environment;
import org.kie.runtime.process.ProcessInstance;
import org.jbpm.marshalling.impl.JBPMMessages;
import org.jbpm.marshalling.impl.ProcessInstanceMarshaller;
import org.jbpm.marshalling.impl.ProcessMarshallerRegistry;
import org.jbpm.marshalling.impl.ProtobufRuleFlowProcessInstanceMarshaller;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;

/**
 * This is the object that contains the 
 * marshalled byte stream of information representing the 
 * ProcessInstance class. 
 * 
 * Because of Hibernate 3.3.x/3.4.x <-> 4.x  compatibility issues,
 * the mapping for this class has been moved to 
 */
public class ProcessInstanceInfo{

    private Long                              processInstanceId;

    private int                               version;

    private String                            processId;
    private Date                              startDate;
    private Date                              lastReadDate;
    private Date                              lastModificationDate;
    private int                               state;

    byte[]                                    processInstanceByteArray;

    private Set<String>                       eventTypes         = new HashSet<String>();
    
    ProcessInstance                           processInstance;
    
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
                                                                               ProtobufMarshaller.TIMER_READERS,
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

    /**
     * Adding @PrePersist breaks things, because: <ul>
     * <li>We retrieve/generate the marshaller (see below).</li> 
     * <li>..and the marshaller retrieves the context instance</li>
     * <li>..which actually (re)sets all variables in {@link VariableScopeInstance}.setContextInstanceContainer(...)</li>
     * <li>This of course causes {@link ProcessEventSupport}.fireBeforeVariableChanged(...) to fire.</li>
     * <li>Then the {@link org.jbpm.process.audit.JPAWorkingMemoryDbLogger} ends up logging a variable change 
	 * -- but the associated process instance hasn't been persisted yet.</li> 
     * <li>So the variable instance change is associated with process instance "0"</li>
     * <li>...and can never be retrieved, because "0" is not a valid id.</li>
     * </ul>
     * </p>
     * Normally, the variable change is logged after the following method has completed. 
     */
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
            
            Object result = marshaller.writeProcessInstance( context,
                                                             processInstance);
            if( marshaller instanceof ProtobufRuleFlowProcessInstanceMarshaller && result != null ) {
                JBPMMessages.ProcessInstance _instance = (JBPMMessages.ProcessInstance)result;
                PersisterHelper.writeToStreamWithHeader( context, 
                                                         _instance );
            }
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
