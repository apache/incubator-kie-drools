package org.drools.command;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public class NewStatefulKnowledgeSessionCommand
    implements
    GenericCommand<StatefulKnowledgeSession> {

    private KnowledgeSessionConfiguration ksessionConf;

    public NewStatefulKnowledgeSessionCommand(KnowledgeSessionConfiguration ksessionConf) {
        this.ksessionConf = ksessionConf;
    }

    public StatefulKnowledgeSession execute(Context context) {
        KnowledgeBase kbase = ((KnowledgeCommandContext) context).getKnowledgeBase();
        StatefulKnowledgeSession ksession;
        
        if ( this.ksessionConf == null ) {
            ksession = kbase.newStatefulKnowledgeSession();
        } else {
            ksession = kbase.newStatefulKnowledgeSession( this.ksessionConf, null );
        }

        return ksession;
    }

}
