package org.drools.commands.runtime.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.commands.IdentifiableResult;
import org.drools.core.ClassObjectSerializationFilter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectsCommand
    implements
    ExecutableCommand<Collection>, IdentifiableResult {

    @XmlElement(name="class-object-filter", required=false)
    private ClassObjectSerializationFilter classObjectFilter = null;

    private transient ObjectFilter filter = null;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public GetObjectsCommand() {
    }

    public GetObjectsCommand(ObjectFilter filter) {
        setFilter(filter);
    }

    public GetObjectsCommand(ObjectFilter filter, String outIdentifier) {
        setFilter(filter);
        this.outIdentifier = outIdentifier;
    }

    public void setFilter(ObjectFilter filter) {
        this.filter = filter;
        if( filter instanceof ClassObjectFilter ) {
            this.classObjectFilter = new ClassObjectSerializationFilter((ClassObjectFilter)filter);
        }
    }

    public ObjectFilter getFilter() {
        if( this.filter == null ) {
           this.filter = this.classObjectFilter;
        }
        return this.filter;
    }

    public Collection execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        Collection col;

        if ( getFilter() != null ) {
            col =  ksession.getObjects( this.filter );
        } else {
            col =  ksession.getObjects( );
        }

        if ( this.outIdentifier != null ) {
            List objects = new ArrayList( col );

            ((RegistryContext) context).lookup(ExecutionResults.class).setResult( this.outIdentifier, objects );
        }

        return col;
    }

    public String toString() {
        if ( getFilter() != null ) {
            return "session.iterateObjects( " + filter + " );";
        } else {
            return "session.iterateObjects();";
        }
    }

}
