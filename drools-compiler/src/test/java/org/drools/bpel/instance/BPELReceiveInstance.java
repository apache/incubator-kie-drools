package org.drools.bpel.instance;

import org.drools.bpel.core.BPELReceive;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.workflow.core.Connection;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELReceiveInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 400L;

    public BPELReceive getBPELReceive() {
        return (BPELReceive) getNode();
    }
    
    public void internalTrigger(NodeInstance from, String type) {
        if (BPELLinkManager.checkActivityEnabled(this)) {
            // TODO look in cache of already receive messages
        }
    }
    
    public void triggerCompleted(String message) {
        String variable = getBPELReceive().getVariable();
        if (variable != null) {
            VariableScopeInstance variableScope = (VariableScopeInstance) resolveContextInstance(VariableScope.VARIABLE_SCOPE, variable);
            if (variableScope == null) {
                throw new IllegalArgumentException(
                    "Variable " + variable + " not found!");
            }
            variableScope.setVariable(variable, message);
        }
        Connection to = getBPELReceive().getTo();
        getNodeInstanceContainer().removeNodeInstance(this);
        getNodeInstanceContainer().getNodeInstance( to.getTo() ).trigger( this, to.getToType());
        BPELLinkManager.activateTargetLinks(this);
    }

}
