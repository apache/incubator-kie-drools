package org.drools.commands.runtime.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.commands.IdentifiableResult;
import org.drools.commands.runtime.FlatQueryResults;
import org.drools.core.QueryResultsImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType( XmlAccessType.NONE )
public class QueryCommand implements ExecutableCommand<QueryResults>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlAttribute(name = "out-identifier")
    private String outIdentifier;
    @XmlAttribute(required = true)
    private String name;

    @XmlElement
    private List<Object> arguments;

    public QueryCommand() {
    }

    public QueryCommand(String outIdentifier, String name, Object... arguments) {
        this.outIdentifier = outIdentifier;
        this.name = name;
        if ( arguments != null ) {
            this.arguments = Arrays.asList( arguments );
        } else {
            this.arguments = Collections.EMPTY_LIST;
        }
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Object> getArguments() {
        if (this.arguments == null) {
            this.arguments = Collections.emptyList();
        }
        return this.arguments;
    }
    public void setArguments(List<Object> arguments) {
        if ( arguments == null || arguments.isEmpty() ) {
            this.arguments = Collections.emptyList();
        }
        this.arguments = arguments;
    }

    public QueryResults execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        if ( this.arguments == null || this.arguments.isEmpty() ) {
            this.arguments = Collections.emptyList();
        }

        for (int j = 0; j < arguments.size(); j++) {
            if (arguments.get(j) instanceof Variable) {
                arguments.set(j, Variable.v);
            }
        }

        QueryResults results = ksession.getQueryResults( name, this.arguments.toArray() );

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult( this.outIdentifier, new FlatQueryResults( (QueryResultsImpl) results) );
        }

        return results;
    }

    public String toString() {
        return "QueryCommand{" +
                "outIdentifier='" + outIdentifier + '\'' +
                ", name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
