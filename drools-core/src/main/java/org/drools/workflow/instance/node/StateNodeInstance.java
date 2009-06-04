package org.drools.workflow.instance.node;

import java.util.Set;
import org.drools.common.InternalAgenda;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.StateNode;

public class StateNodeInstance extends EventBasedNodeInstance {

	private static final long serialVersionUID = 4L;

    protected StateNode getStateNode() {
        return (StateNode) getNode();
    }
	public void internalTrigger(NodeInstance from, String type) {
        super.internalTrigger(from, type);
        StateNode stateNode = getStateNode();
        Set<String> keys = stateNode.getConstraints().keySet();
        int triggeredActivation = 0;
        for(String key:keys){
            String rule = "RuleFlowStateNode-" + getProcessInstance().getProcessId()
                + "-" + getNode().getId() +"-"+ key ;
            
            boolean isActive = ((InternalAgenda) getProcessInstance().getAgenda())
                .isRuleActiveInRuleFlowGroup("DROOLS_SYSTEM", rule, getProcessInstance().getId());
            if (isActive) {
                triggerCompleted(key, true);
                triggeredActivation++;
            }
        }
        if(triggeredActivation == 0){
             addTriggerListener();
        }


	}
	
	public void signalEvent(String type, Object event) {
		if ("signal".equals(type)) {
			String connectionType = NodeImpl.CONNECTION_DEFAULT_TYPE;
			if (event instanceof String) {
				connectionType = (String) event;
			}
			removeEventListeners();
			triggerCompleted(connectionType, true);
		} else {
			super.signalEvent(type, event);
		}
	}
	
	private void addTriggerListener() {
		getProcessInstance().addEventListener("signal", this, false);
	}

    public void addEventListeners() {
        super.addEventListeners();
        addTriggerListener();
    }
    
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("signal", this, false);
    }

}
