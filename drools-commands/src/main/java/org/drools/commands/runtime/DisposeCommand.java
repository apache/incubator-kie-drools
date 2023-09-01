package org.drools.commands.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.commands.impl.NotTransactionalCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DisposeCommand
    implements
    NotTransactionalCommand<Void> {

    public Void execute(Context context) {
        KieSession ksession = ( (RegistryContext) context ).lookup( KieSession.class );
        ksession.dispose();
        return null;
    }

    public String toString() {
        return "ksession.dispose();";
    }

    public boolean canRunInTransaction() {
        return false;
    }
}
