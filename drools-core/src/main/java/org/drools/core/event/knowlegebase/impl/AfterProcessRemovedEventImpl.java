package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.api.event.kiebase.AfterProcessRemovedEvent;

public class AfterProcessRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterProcessRemovedEvent {
    private Process process;
    
    public AfterProcessRemovedEventImpl(KieBase knowledgeBase, Process process) {
        super( knowledgeBase );
        this.process = process;
    }

    public Process getProcess() {
        return this.process;
    }

    @Override
    public String toString() {
        return "==>[AfterProcessRemovedEventImpl: getProcess()=" + getProcess() + "]";
    }
}
