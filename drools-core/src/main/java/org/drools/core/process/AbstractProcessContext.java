package org.drools.core.process;

import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProcessContext implements org.kie.api.runtime.process.ProcessContext {

    private static Logger logger = LoggerFactory.getLogger( ProcessContext.class );

    private KieRuntime kruntime;
    private ProcessInstance processInstance;
    private NodeInstance nodeInstance;

    public AbstractProcessContext( KieRuntime kruntime ) {
        this.kruntime = kruntime;
    }

    public ProcessInstance getProcessInstance() {
        if ( processInstance != null ) {
            return processInstance;
        }
        if ( nodeInstance != null ) {
            return nodeInstance.getProcessInstance();
        }
        return null;
    }

    public void setProcessInstance( ProcessInstance processInstance ) {
        this.processInstance = processInstance;
    }

    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    public void setNodeInstance( NodeInstance nodeInstance ) {
        this.nodeInstance = nodeInstance;
    }

    public Object getVariable( String variableName ) {
        if ( nodeInstance != null ) {
            return nodeInstance.getVariable( variableName );
        } else {
            return (( WorkflowProcessInstance ) getProcessInstance()).getVariable( variableName );
        }
    }

    public void setVariable( String variableName, Object value ) {
        if ( nodeInstance != null ) {
            nodeInstance.setVariable( variableName, value );
        } else {
            (( WorkflowProcessInstance ) getProcessInstance()).setVariable( variableName, value );
        }
    }

    public KieRuntime getKieRuntime() {
        return kruntime;
    }

    public KieRuntime getKnowledgeRuntime() {
        return kruntime;
    }

    public Logger getLogger() {
        return logger;
    }
}
