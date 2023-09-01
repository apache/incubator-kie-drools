package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.util.StringUtils;
import org.drools.commands.jaxb.JaxbUnknownAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class DeleteObjectCommand
        implements ExecutableCommand<Void> {

    @XmlAttribute(name="object")
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private Object object;

    @XmlAttribute(name="entry-point")
    private String entryPoint = "DEFAULT";

    public DeleteObjectCommand() {
    }

    public DeleteObjectCommand( Object object, String entryPoint ) {
        this.object = object;
        if ( ! StringUtils.isEmpty( this.entryPoint ) ) {
            this.entryPoint = entryPoint;
        }
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint( entryPoint );
        if ( ep != null ) {
            FactHandle handle = ksession.getEntryPoint( entryPoint ).getFactHandle( object );
            ksession.delete( handle );
        }
        return null;
    }

    public Object getObject() {
        return this.object;
    }

    public String getEntryPointId() {
        return entryPoint;
    }

    public String toString() {
        return "session.entryPoints(" + ((this.entryPoint == null ) ? "DEFAULT" : this.entryPoint) + ").delete( " + object + " );";
    }

}
