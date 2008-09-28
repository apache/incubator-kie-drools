package org.drools.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.StatefulKnowledgeSession;
import org.drools.common.InternalFactHandle;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItemManager;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.time.SessionClock;

public class StatefulKnowledgeSessionImpl implements StatefulKnowledgeSession {
	public ReteooStatefulSession session;
	
	public StatefulKnowledgeSessionImpl(ReteooStatefulSession session) {
		this.session = session;
	}
	
	public void fireAllRules() {
		this.session.fireAllRules();
	}

	public SessionClock getSessionClock() {
		return this.session.getSessionClock();
	}

	public void halt() {
		this.session.halt();
	}

	public FactHandle insertObject(Object object) {
		return this.session.insert( object );
	}

	public void retractObject(FactHandle factHandle) {
		this.session.retract( factHandle );
		
	}

	public void updateObject(FactHandle factHandle) {
		this.session.update( factHandle, ((InternalFactHandle) factHandle).getObject() );
	}

	public void updateObject(FactHandle factHandle, Object object) {
		this.session.update( factHandle, object);
		
	}

	public ProcessInstance getProcessInstance(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<ProcessInstance> getProcessInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	public WorkItemManager getWorkItemManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public ProcessInstance startProcess(String processId) {
		// TODO Auto-generated method stub
		return null;
	}

	public ProcessInstance startProcess(String processId,
			Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
