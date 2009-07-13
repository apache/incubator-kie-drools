package org.drools.command.impl;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.command.Context;
import org.drools.command.ContextManager;
import org.drools.runtime.StatefulKnowledgeSession;

public class KnowledgeCommandContext
    implements
    Context {
    private Context                  context;
    private KnowledgeBuilder         kbuilder;
    private KnowledgeBase            kbase;
    private StatefulKnowledgeSession statefulKsession;

    public KnowledgeCommandContext(Context context,
                                   KnowledgeBuilder kbuilder,
                                   KnowledgeBase kbase,
                                   StatefulKnowledgeSession statefulKsession) {
        this.context = context;
        this.kbuilder = kbuilder;
        this.kbase = kbase;
        this.statefulKsession = statefulKsession;
    }

    public KnowledgeBuilder getKnowledgeBuilder() {
        return kbuilder;
    }

    public KnowledgeBase getKnowledgeBase() {
        return this.kbase;
    }       

    public StatefulKnowledgeSession getStatefulKnowledgesession() {
        return statefulKsession;
    }

    public ContextManager getContextManager() {
        return context.getContextManager();
    }

    public String getName() {
        return context.getName();
    }

    public Object get(String identifier) {
        return context.get( identifier );
    }

    public void set(String identifier,
                    Object value) {
        context.set( identifier,
                     value );
    }

}
