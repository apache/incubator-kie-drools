package org.drools.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;

@XmlRootElement(name="get-variable-command")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetVariableCommand implements ExecutableCommand<Object> {
   
    @XmlElement
    @XmlSchemaType(name="string")
    private String identifier;
   
    @XmlElement
    @XmlSchemaType(name="string")
    private String contextName;
    
    public GetVariableCommand() {
        // no-arg constructor neccessary for serialization
    }    

    public GetVariableCommand(String identifier) {
        this.identifier = identifier;
    }    

    public GetVariableCommand(String identifier,
                              String contextName) {
        this.identifier = identifier;
        this.contextName = contextName;
    }

    public Object execute(Context ctx) {        
        Context targetCtx;
        if ( this.contextName == null ) {
            targetCtx = ctx;
        } else {
            targetCtx = ( (RegistryContext) ctx ).getContextManager().getContext( this.contextName );
        }
        
        return targetCtx.get( identifier);        
    }

}
