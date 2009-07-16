package org.drools.command;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;

public class NewKnowledgeBaseCommand
    implements
    GenericCommand<KnowledgeBase> {

    private KnowledgeBaseConfiguration kbaseConf;

    public NewKnowledgeBaseCommand(KnowledgeBaseConfiguration kbaseConf) {
        this.kbaseConf = kbaseConf;
    }

    public KnowledgeBase execute(Context context) {
        KnowledgeBase kbase = null;
        if ( this.kbaseConf == null ) {
            kbase = KnowledgeBaseFactory.newKnowledgeBase();
        } else {
            kbase = KnowledgeBaseFactory.newKnowledgeBase( this.kbaseConf );
        }

        return kbase;
    }

}
