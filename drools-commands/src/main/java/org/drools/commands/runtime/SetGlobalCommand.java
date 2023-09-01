package org.drools.commands.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.IdentifiableResult;
import org.drools.commands.jaxb.JaxbUnknownAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SetGlobalCommand
    implements
    ExecutableCommand<Object>, IdentifiableResult {

    @XmlAttribute(required=true)
    private String  identifier;

    @XmlElement
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private Object  object;

    @XmlAttribute(name="out-identifier")
    private String  outIdentifier;

    public SetGlobalCommand() {
    }

    public SetGlobalCommand(String identifier,
                            Object object) {
        this.identifier = identifier;
        this.object = object;
    }

    public Object execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult( this.outIdentifier, object );
        }

        ksession.setGlobal( this.identifier, this.object );

        // returning the object is necessary for drools-simulator
        return this.object;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject( Object object ) {
        this.object = object;
    }

    public String getOutIdentifier() {
        return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String toString() {
        return "session.setGlobal(" + this.identifier + ", " + this.object + ");";
    }

}
