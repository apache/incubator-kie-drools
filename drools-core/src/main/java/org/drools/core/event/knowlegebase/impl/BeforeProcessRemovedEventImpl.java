package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.api.event.kiebase.BeforeProcessRemovedEvent;

public class BeforeProcessRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeProcessRemovedEvent {
    private Process process;
    
    public BeforeProcessRemovedEventImpl(KieBase knowledgeBase, Process process) {
        super( knowledgeBase );
        this.process = process;
    }

    public Process getProcess() {
        return this.process;
    }

    @Override
    public String toString() {
        return "==>[BeforeProcessRemovedEventImpl: getProcess()=" + getProcess() + "]";
    }

}
