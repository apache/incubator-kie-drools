package org.drools.command.builder;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.io.Resource;

public class NewKnowledgeBuilderCommand
    implements
    GenericCommand<KnowledgeBuilder> {

    private KnowledgeBuilderConfiguration kbuilderConf;

    public NewKnowledgeBuilderCommand(KnowledgeBuilderConfiguration kbuilderConf) {
        this.kbuilderConf = kbuilderConf;
    }

    public KnowledgeBuilder execute(Context context) {
        KnowledgeBuilder kbuilder = null;
        if ( this.kbuilderConf == null ) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();            
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( this.kbuilderConf );
        }
        
        return kbuilder;
    }

}
