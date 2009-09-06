package org.drools.command;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.impl.ExecutionResultImpl;

public class KnowledgeContextResolveFromContextCommand
    implements
    GenericCommand {

    private String  kbaseIdentifier;
    private String  kbuilderIdentifier;
    private String  statefulKsessionName;
    private String  kresults;
    private Command command;

    public KnowledgeContextResolveFromContextCommand(Command command,
                                                     String kbuilderIdentifier,
                                                     String kbaseIdentifier,
                                                     String statefulKsessionName,
                                                     String kresults) {
        this.command = command;
        this.kbuilderIdentifier = kbuilderIdentifier;
        this.kbaseIdentifier = kbaseIdentifier;
        this.statefulKsessionName = statefulKsessionName;
        this.kresults = kresults;
    }

    public Object execute(Context context) {
        KnowledgeCommandContext kcContext = new KnowledgeCommandContext( context,
                                                                         (KnowledgeBuilder) context.get( this.kbuilderIdentifier ),
                                                                         (KnowledgeBase) context.get( this.kbaseIdentifier ),
                                                                         (StatefulKnowledgeSession) context.get( this.statefulKsessionName ),
                                                                         (ExecutionResultImpl) context.get( this.kresults ) );
        return ((GenericCommand) command).execute( kcContext );
    }

    public Command getCommand() {
        return this.command;
    }

}
