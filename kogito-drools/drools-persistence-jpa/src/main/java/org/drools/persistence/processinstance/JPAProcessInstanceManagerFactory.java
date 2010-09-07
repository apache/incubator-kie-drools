package org.drools.persistence.processinstance;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.ProcessInstanceManagerFactory;

public class JPAProcessInstanceManagerFactory implements ProcessInstanceManagerFactory {

	public ProcessInstanceManager createProcessInstanceManager(InternalKnowledgeRuntime kruntime) {
		JPAProcessInstanceManager result = new JPAProcessInstanceManager();
		result.setKnowledgeRuntime(kruntime);
		return result;
	}

}
