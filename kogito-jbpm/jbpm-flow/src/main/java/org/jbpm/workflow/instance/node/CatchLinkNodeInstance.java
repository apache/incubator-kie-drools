package org.jbpm.workflow.instance.node;

import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.runtime.process.NodeInstance;

public class CatchLinkNodeInstance extends NodeInstanceImpl {

	private static final long serialVersionUID = 20110505L;

	@Override
	public void internalTrigger(NodeInstance from, String type) {
		this.triggerCompleted();

	}

	public void triggerCompleted() {
		this.triggerCompleted(
				org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
	}

}
