package org.drools.bpel.core;

import java.util.List;

import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.EndNode;


/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELProcess extends WorkflowProcessImpl implements BPELFaultHandlerContainer {

    private static final long serialVersionUID = 400L;
    
    public static final String BPEL_TYPE = "BPEL";
    
    private BPELActivity activity;
    
    public BPELProcess() {
        setType(BPEL_TYPE);
        VariableScope variableScope = new VariableScope();
        addContext(variableScope);
        setDefaultContext(variableScope);
    }
    
    public VariableScope getVariableScope() {
        return (VariableScope) getDefaultContext(VariableScope.VARIABLE_SCOPE);
    }
    
    public void setActivity(BPELActivity activity) {
        if (activity == null) {
            throw new IllegalArgumentException(
                "The activity of a BPEL process may not be null!");
        }
        if (this.activity != null) {
            throw new IllegalArgumentException(
                "The activity of this BPEL process has already been set!");
        }
        this.activity = activity;
        addNode(activity);
        EndNode end = new EndNode();
        addNode(end);
        new ConnectionImpl(
            activity, Node.CONNECTION_DEFAULT_TYPE,
            end, Node.CONNECTION_DEFAULT_TYPE);
    }
    
    public BPELActivity getActivity() {
        return activity;
    }
    
    public void setFaultHandlers(List<BPELFaultHandler> faultHandlers) {
        ExceptionScope exceptionScope = new ExceptionScope();
        addContext(exceptionScope);
        setDefaultContext(exceptionScope);
        for (BPELFaultHandler faultHandler: faultHandlers) {
            addNode(faultHandler.getActivity());
            exceptionScope.setExceptionHandler(faultHandler.getFaultName(), faultHandler);
        }
        // TODO: process should end once fault handler has been executed
    }
    
}
