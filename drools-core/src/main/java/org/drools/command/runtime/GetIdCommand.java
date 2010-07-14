package org.drools.command.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;

@XmlAccessorType(XmlAccessType.NONE)
public class GetIdCommand
    implements
    GenericCommand<Integer> {

	private static final long serialVersionUID = 1L;

    
    public GetIdCommand() {
	}

    public Integer execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ((StatefulKnowledgeSessionImpl)ksession).getId();
    }

    public String toString() {
        return "session.getId( );";
    }
}
