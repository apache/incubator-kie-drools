package org.drools.command.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DisconnectedFactHandle;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

@XmlAccessorType(XmlAccessType.NONE)
public class RetractCommand
implements
GenericCommand<Object> {

	private FactHandle handle;

	public RetractCommand() {
	}

	public RetractCommand(FactHandle handle) {
		this.handle = handle;
	}

	public Object execute(Context context) {
		StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
		ksession.retract( handle );
		return null;
	}

	public FactHandle getFactHandle() {
		return this.handle;
	}

	@XmlAttribute(name="fact-handle", required=true)
	public void setFactHandleFromString(String factHandleId) {
		handle = new DisconnectedFactHandle(factHandleId);
	}
	
    public String getFactHandleFromString() {
    	return handle.toExternalForm();
	}

	public String toString() {
		return "session.retract( " + handle + " );";
	}
}
