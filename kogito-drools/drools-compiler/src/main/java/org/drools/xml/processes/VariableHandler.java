package org.drools.xml.processes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.process.core.Process;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class VariableHandler extends BaseAbstractHandler
    implements
    Handler {
    public VariableHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Process.class );

            this.validPeers = new HashSet();         
            this.validPeers.add( null );            

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startConfiguration(localName, attrs);
        WorkflowProcessImpl process = (WorkflowProcessImpl) parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        
        VariableScope variableScope = (VariableScope) 
            process.getDefaultContext(VariableScope.VARIABLE_SCOPE);
        Variable variable = new Variable();
        if (variableScope != null) {
            variable.setName(name);
            List<Variable> variables = variableScope.getVariables();
            if (variables == null) {
                variables = new ArrayList<Variable>();
                variableScope.setVariables(variables);
            }
            variables.add(variable);
        } else {
            throw new SAXParseException(
                "Could not find default variable scope.", parser.getLocator());
        }
        
        return variable;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endConfiguration();
        return null;
    }

    public Class generateNodeFor() {
        return Variable.class;
    }    

}
