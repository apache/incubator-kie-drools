package org.drools.persistence.processinstance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.ProcessMarshallerImpl;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.process.instance.WorkItem;
import org.drools.runtime.Environment;

@Entity
public class WorkItemInfo {

    private static final String               VARIABLE_SEPARATOR = ":";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long   workItemId;

    @Version
    @Column(name = "OPTLOCK")
    private int    version;

    private String name;
    private Date   creationDate;
    private long   processInstanceId;
    private long   state;
    private @Lob
    byte[]         workItemByteArray;
    private @Transient
    WorkItem       workItem;

    private @Transient
    Environment                               env;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "workItemId")
    @MapKey(name = "name")
    private Map<String, VariableInstanceInfo> variables          = new HashMap<String, VariableInstanceInfo>();
    private boolean                           externalVariables  = false;



    protected WorkItemInfo() {
    }

    public WorkItemInfo(WorkItem workItem, Environment env) {
        this.workItem = workItem;
        this.name = workItem.getName();
        this.creationDate = new Date();
        this.processInstanceId = workItem.getProcessInstanceId();
        this.env = env;
    }

    public long getId() {
        return workItemId;
    }
    
    public int getVersion() {
        return this.version;
    }    

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public long getState() {
        return state;
    }

    public WorkItem getWorkItem(Environment env) {
        this.env = env;
        if ( workItem == null ) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( workItemByteArray );
                MarshallerReaderContext context = new MarshallerReaderContext( bais,
                                                                               null,
                                                                               null,
                                                                               null );
                workItem = ProcessMarshallerImpl.readWorkItem( context, !externalVariables );
                 if ( externalVariables ) {
                    restoreVariables();
                }
                context.close();
            } catch ( IOException e ) {
                e.printStackTrace();
                throw new IllegalArgumentException( "IOException while loading process instance: " + e.getMessage() );
            }
        }
        return workItem;
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
                             workItem );
        }
    }

    private void restoreVariable(VariableInstanceInfo variableInfo,
                                 List<Long> parentIds,
                                 String variableName,
                                 WorkItem workItem) throws NumberFormatException {

            VariablePersistenceStrategy persistenceStrategy = VariablePersistenceStrategyFactory.getVariablePersistenceStrategy();
            Object value = persistenceStrategy.getVariable( variableInfo,
                                                            this.env );
            System.out.println( ">>>>> Restoring variable inside workitem " + variableName + " = " + value );
            workItem.setParameter( variableName, value );

    }


    @PreUpdate
    public void update() {
        this.state = workItem.getState();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean variablesChanged = false;
        try {
            MarshallerWriteContext context = new MarshallerWriteContext( baos,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         null );
            externalVariables = VariablePersistenceStrategyFactory.getVariablePersistenceStrategy().isEnabled();
            ProcessMarshallerImpl.writeWorkItem( context,
                                                 workItem, !externalVariables );


            if ( externalVariables ) {
                variablesChanged = persistVariables();
            }
            context.close();
            this.workItemByteArray = baos.toByteArray();
        } catch ( IOException e ) {
            throw new IllegalArgumentException( "IOException while storing workItem " + workItem.getId() + ": " + e.getMessage() );
        }
    }

    private boolean persistVariables() {
        // Get Process Variables

        Map<String, Object> processVariables = workItem.getParameters();
        Map<String, VariableInstanceInfo> newVariables = new HashMap<String, VariableInstanceInfo>();
        // persist process variables
        persist( processVariables,
                 "workItem.",
                 newVariables );
        // persist variables in nested variable scopes

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
                System.out.println( "<<<<< Persisting variable inside workitem " + variableName + " = " + value );
                newVariables.put( variableName,
                                  variable );
            } else {
                System.out.println( "<<<<< Variable inside workitem " + variableName + " not persisted (value null)" );
            }
        }
    }


}
