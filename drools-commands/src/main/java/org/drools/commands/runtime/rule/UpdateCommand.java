package org.drools.commands.runtime.rule;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.common.DisconnectedFactHandle;
import org.drools.commands.jaxb.JaxbUnknownAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlRootElement(name="update-command")
@XmlAccessorType(XmlAccessType.NONE)
public class UpdateCommand implements ExecutableCommand<Void> {

    private static final long serialVersionUID = 3255044102543531497L;

    @XmlElement(name="fact-handle", required=true)
    private DisconnectedFactHandle handle;

    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    @XmlElement
    private Object object;

    @XmlElement
    @XmlSchemaType(name="string")
    private String entryPoint = "DEFAULT";

    @XmlElement
    private String[] modifiedProperties;

    public UpdateCommand() {
    }

    public UpdateCommand(FactHandle handle,
                         Object object) {
        this.handle = DisconnectedFactHandle.newFrom( handle );
        this.object = object;
    }

    public UpdateCommand(FactHandle handle,
                         Object object,
                         String[] modifiedProperties) {
        this( handle, object );
        this.modifiedProperties = modifiedProperties;
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

    public Object getObject() {
        return object;
    }

    public DisconnectedFactHandle getHandle() {
        return this.handle;
    }

    public void setFactHandleFromString(String factHandleId) {
        handle = new DisconnectedFactHandle(factHandleId);
    }

    public String getFactHandleFromString() {
        return handle.toExternalForm();
    }

    public String[] getModifiedProperties() {
        return modifiedProperties;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint( handle.getEntryPointName() );
        if (modifiedProperties != null) {
            ep.update( handle, object, modifiedProperties );
        } else {
            ep.update( handle, object );
        }
        return null;
    }

    public String toString() {
        return "session.update( " + handle + ", " + object +
               (modifiedProperties != null ? ", " + Arrays.toString( modifiedProperties ) : "") + " );";
    }
}
