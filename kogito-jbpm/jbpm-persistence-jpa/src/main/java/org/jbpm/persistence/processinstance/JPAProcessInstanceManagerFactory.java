package org.jbpm.persistence.processinstance;

import org.drools.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.process.instance.ProcessInstanceManagerFactory;

public class JPAProcessInstanceManagerFactory implements ProcessInstanceManagerFactory {

	public ProcessInstanceManager createProcessInstanceManager(InternalKnowledgeRuntime kruntime) {
		JPAProcessInstanceManager result = new JPAProcessInstanceManager();
		result.setKnowledgeRuntime(kruntime);
		return result;
	}

}
