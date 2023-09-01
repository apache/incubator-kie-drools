package org.drools.commands.runtime.rule;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.drools.core.common.DisconnectedFactHandle;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class UpdateInEntryPointCommand
        implements ExecutableCommand<Void> {

    private static final long serialVersionUID = 3255044102543531497L;

    private DisconnectedFactHandle handle;
    private Object object;
    private String entryPoint;
    private String[] modifiedProperties;

    public UpdateInEntryPointCommand() {
    }

    public UpdateInEntryPointCommand(FactHandle handle,
                                     Object object,
                                     String entryPoint) {
        this.handle = DisconnectedFactHandle.newFrom( handle );
        this.object = object;
        this.entryPoint = entryPoint;
    }

    public UpdateInEntryPointCommand(FactHandle handle,
                                     Object object,
                                     String entryPoint,
                                     String[] modifiedProperties) {
        this(handle, object, entryPoint);
        this.modifiedProperties = modifiedProperties;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint( entryPoint );
        if (modifiedProperties != null) {
            ep.update( handle, object, modifiedProperties );
        } else {
            ep.update( handle, object );
        }
        return null;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
    	if (entryPoint == null) {
    		entryPoint = "DEFAULT";
    	}
        this.entryPoint = entryPoint;
    }

    public String toString() {
        return "session.getEntryPoint( " + entryPoint + " ).update( " + handle + ", " + object +
               (modifiedProperties != null ? ", " + Arrays.toString(modifiedProperties) : "") + " );";
    }

    public Object getObject() { 
        return object;
    }
}
