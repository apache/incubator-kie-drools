/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.xml.processes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class VariableHandler extends BaseAbstractHandler
    implements
    Handler {
    public VariableHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add( ContextContainer.class );

            this.validPeers = new HashSet<Class<?>>();         
            this.validPeers.add( null );            

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        ContextContainer contextContainer = (ContextContainer) parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        
        VariableScope variableScope = (VariableScope) 
            contextContainer.getDefaultContext(VariableScope.VARIABLE_SCOPE);
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
        parser.endElementBuilder();
        return null;
    }

    public Class<?> generateNodeFor() {
        return Variable.class;
    }    

}
