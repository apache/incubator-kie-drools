package org.drools.persistence.processinstance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.ProcessInstanceMarshaller;
import org.drools.marshalling.impl.ProcessMarshallerRegistry;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.ContextableInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.hibernate.annotations.CollectionOfElements;

@Entity
public class ProcessInstanceInfo {

    private static final String               VARIABLE_SEPARATOR = ":";

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
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "processInstanceId")
    @MapKey(name = "name")
    private Map<String, VariableInstanceInfo> variables          = new HashMap<String, VariableInstanceInfo>();
    private boolean                           externalVariables  = false;

    protected ProcessInstanceInfo() {
    }

    public ProcessInstanceInfo(ProcessInstance processInstance) {
        this.processInstance = processInstance;
        this.processId = processInstance.getProcessId();
        startDate = new Date();
    }

    public ProcessInstanceInfo(ProcessInstance processInstance,
                               Environment env) {
        this.processInstance = processInstance;
        this.processId = processInstance.getProcessId();
        startDate = new Date();
        this.env = env;
    }

    public long getId() {
        return processInstanceId;
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

    public ProcessInstance getProcessInstance(WorkingMemory workingMemory,
                                              Environment env) {
        this.env = env;
        if ( processInstance == null ) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( processInstanceByteArray );
                MarshallerReaderContext context = new MarshallerReaderContext( bais,
                                                                               (InternalRuleBase) workingMemory.getRuleBase(),
                                                                               null,
                                                                               null );
                context.wm = (InternalWorkingMemory) workingMemory;
                ProcessInstanceMarshaller marshaller = getMarshallerFromContext( context );
                processInstance = marshaller.readProcessInstance( context,
                                                                  !externalVariables );
                if ( externalVariables ) {
                    restoreVariables();
                }
                context.close();
            } catch ( IOException e ) {
                e.printStackTrace();
                throw new IllegalArgumentException( "IOException while loading process instance: " + e.getMessage(),
                                                    e );
            }
        }
        return processInstance;
    }

    private void restoreVariables() {
        for ( Map.Entry<String, VariableInstanceInfo> entry : variables.entrySet() ) {
            String[] variableHierarchy = entry.getKey().split( VARIABLE_SEPARATOR );
            // last one is variable name
            String variableName = variableHierarchy[variableHierarchy.length - 1];
            // other ones are parent ids
            List<Long> parentIds = new ArrayList<Long>();
            for ( int i = 0; i < variableHierarchy.length - 1; i++ ) {
                parentIds.add( Long.valueOf( variableHierarchy[i] ) );
            }
            restoreVariable( entry.getValue(),
                             parentIds,
                             variableName,
                             (WorkflowProcessInstance) processInstance );
        }
    }

    private void restoreVariable(VariableInstanceInfo variableInfo,
                                 List<Long> parentIds,
                                 String variableName,
                                 NodeInstanceContainer nodeInstanceContainer) throws NumberFormatException {
        if ( parentIds.size() == 0 ) {
            if ( !(nodeInstanceContainer instanceof ContextableInstance) ) {
                throw new IllegalArgumentException( "Parent node instance is not a contextable instance: " + nodeInstanceContainer );
            }
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance) ((ContextableInstance) nodeInstanceContainer).getContextInstance( VariableScope.VARIABLE_SCOPE );
            VariablePersistenceStrategy persistenceStrategy = VariablePersistenceStrategyFactory.getVariablePersistenceStrategy();
            Object value = persistenceStrategy.getVariable( variableInfo,
                                                            this.env );
            System.out.println( ">>>>> Restoring variable " + variableName + " = " + value );
            variableScopeInstance.setVariable( variableName,
                                               value );
        } else {
            Long nodeInstanceId = parentIds.get( 0 );
            // find the node and get the variableScopeInstance and insert the
            // variable..
            Collection<NodeInstance> nodeInstances = nodeInstanceContainer.getNodeInstances();
            for ( NodeInstance nodeInstance : nodeInstances ) {
                if ( nodeInstance.getId() == nodeInstanceId ) {
                    parentIds.remove( 0 );
                    if ( !(nodeInstance instanceof NodeInstanceContainer) ) {
                        throw new IllegalArgumentException( "Restoring variable " + variableName + " but node found is not a node instance container:" + nodeInstance );
                    }
                    restoreVariable( variableInfo,
                                     parentIds,
                                     variableName,
                                     (NodeInstanceContainer) nodeInstance );
                }
                return;
            }
            throw new IllegalArgumentException( "Could not find node instance " + nodeInstanceId + " in " + nodeInstanceContainer );
        }
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
                                                                         null );
            String processType = ((ProcessInstanceImpl) processInstance).getProcess().getType();
            saveProcessInstanceType( context,
                                     processInstance,
                                     processType );
            ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller( processType );
            externalVariables = VariablePersistenceStrategyFactory.getVariablePersistenceStrategy().isEnabled();
            marshaller.writeProcessInstance( context,
                                             processInstance,
                                             !externalVariables );
            if ( externalVariables ) {
                variablesChanged = persistVariables();
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

    private boolean persistVariables() {
        // Get Process Variables
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) ((WorkflowProcessInstanceImpl) this.processInstance).getContextInstance( VariableScope.VARIABLE_SCOPE );
        Map<String, Object> processVariables = variableScopeInstance.getVariables();
        Map<String, VariableInstanceInfo> newVariables = new HashMap<String, VariableInstanceInfo>();
        // persist process variables
        persist( processVariables,
                 "",
                 newVariables );
        // persist variables in nested variable scopes
        Collection<NodeInstance> nodeInstances = ((WorkflowProcessInstanceImpl) this.processInstance).getNodeInstances();
        if ( nodeInstances.size() > 0 ) {
            persistNodeVariables( nodeInstances,
                                  "",
                                  newVariables );
        }
        if ( newVariables.size() > 0 || this.variables.size() > 0 ) {
            // clear variables so unnecessary values are removed
            this.variables.clear();
            this.variables.putAll( newVariables );
            // TODO: how can I know that no variables were changed?
            return true;
        } else {
            return false;
        }
    }

    private void persist(Map<String, Object> variables,
                         String prefix,
                         Map<String, VariableInstanceInfo> newVariables) {
        VariablePersistenceStrategy persistenceStrategy = VariablePersistenceStrategyFactory.getVariablePersistenceStrategy();
        for ( Map.Entry<String, Object> entries : variables.entrySet() ) {
            String variableName = prefix + entries.getKey();
            Object value = entries.getValue();
            VariableInstanceInfo oldValue = this.variables.get( variableName );
            VariableInstanceInfo variable = persistenceStrategy.persistVariable( variableName,
                                                                                 value,
                                                                                 oldValue,
                                                                                 this.env );

            if ( variable != null ) {
                System.out.println( "<<<<< Persisting variable " + variableName + " = " + value );
                newVariables.put( variableName,
                                  variable );
            } else {
                System.out.println( "<<<<< Variable " + variableName + " not persisted (value null)" );
            }
        }
    }

    private void persistNodeVariables(Collection<NodeInstance> nodeInstances,
                                      String parentPrefix,
                                      Map<String, VariableInstanceInfo> newVariables) {
        for ( NodeInstance nodeInstance : nodeInstances ) {
            String prefix = parentPrefix + nodeInstance.getId() + VARIABLE_SEPARATOR;
            if ( nodeInstance instanceof ContextInstanceContainer ) {
                List<ContextInstance> variableScopeInstances = ((ContextInstanceContainer) nodeInstance).getContextInstances( VariableScope.VARIABLE_SCOPE );
                if ( variableScopeInstances != null ) {
                    for ( ContextInstance contextInstance : variableScopeInstances ) {
                        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) contextInstance;
                        persist( variableScopeInstance.getVariables(),
                                 prefix,
                                 newVariables );
                    }
                }
            }
            if ( nodeInstance instanceof NodeInstanceContainer ) {
                Collection<NodeInstance> nodeInstancesInsideTheContainer = ((NodeInstanceContainer) nodeInstance).getNodeInstances();
                persistNodeVariables( nodeInstancesInsideTheContainer,
                                      prefix,
                                      newVariables );
            }
        }
    }

    public Map<String, VariableInstanceInfo> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, VariableInstanceInfo> variables) {
        this.variables = variables;
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
        if ( this.variables != other.variables && (this.variables == null || !this.variables.equals( other.variables )) ) {
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
        hash = 61 * hash + (this.variables != null ? this.variables.hashCode() : 0);
        return hash;
    }

}
