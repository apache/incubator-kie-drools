package org.drools.command.builder;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.io.Resource;

public class KnowledgeBuilderAddCommand
    implements
    GenericCommand<Void> {

    private Resource              resource;
    private ResourceType          resourceType;
    private ResourceConfiguration resourceConfiguration;

    public KnowledgeBuilderAddCommand(Resource resource,
                                      ResourceType resourceType,
                                      ResourceConfiguration resourceConfiguration) {
        this.resource = resource;
        this.resourceType = resourceType;
        this.resourceConfiguration = resourceConfiguration;
    }
    

    public Void execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        if ( resourceConfiguration == null ) {
            kbuilder.add( resource,
                          resourceType );
        } else {
            kbuilder.add( resource,
                          resourceType,
                          resourceConfiguration );
        }
        return null;
    }

}
