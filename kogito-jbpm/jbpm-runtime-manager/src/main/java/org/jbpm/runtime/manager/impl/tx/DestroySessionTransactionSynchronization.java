package org.jbpm.runtime.manager.impl.tx;

import org.drools.core.command.CommandService;
import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.GenericCommand;
import org.drools.persistence.TransactionSynchronization;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

public class DestroySessionTransactionSynchronization implements
        TransactionSynchronization {

    private KieSession ksession;
    public DestroySessionTransactionSynchronization(KieSession ksession) {
        this.ksession = ksession;
    }

    @Override
    public void beforeCompletion() {
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Void execute(Context context) {
                if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
                    CommandService commandService = ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService();
                    ((SingleSessionCommandService) commandService).destroy();
                 }
                return null;
            }
        });

    }

    @Override
    public void afterCompletion(int status) {

    }

}
