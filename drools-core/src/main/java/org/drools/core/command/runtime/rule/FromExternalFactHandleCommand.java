package org.drools.core.command.runtime.rule;

import java.util.Collection;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.InternalFactHandle;
import org.kie.internal.command.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class FromExternalFactHandleCommand implements GenericCommand<FactHandle> {

    private String factHandleExternalForm;
    private boolean disconnected;

    public FromExternalFactHandleCommand(String factHandleExternalForm) {
        this(factHandleExternalForm, false);
    }

    public FromExternalFactHandleCommand(String factHandleExternalForm, boolean disconnected) {
        this.factHandleExternalForm = factHandleExternalForm;
        this.disconnected = disconnected;
    }

    public FactHandle execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        Collection<FactHandle> factHandles = ksession.getFactHandles();
        int fhId = Integer.parseInt(factHandleExternalForm.split(":")[1]);
        for (FactHandle factHandle : factHandles) {
            if (factHandle instanceof InternalFactHandle
                    && ((InternalFactHandle) factHandle).getId() == fhId) {
                InternalFactHandle fhClone = ((InternalFactHandle) factHandle).clone();
                if (disconnected) {
                    fhClone.disconnect();
                }
                return fhClone;
            }
        }
        return null;
    }

    public String toString() {
        return "ksession.getFactHandle( " + factHandleExternalForm + " );";
    }
}
