/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.bpmn2.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.core.BaseAbstractHandler;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataTypeResolver;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.kie.api.definition.process.Process;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GlobalHandler extends BaseAbstractHandler implements Handler {

    public GlobalHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet();
            this.validParents.add(Process.class);

            this.validPeers = new HashSet();
            this.validPeers.add(null);

            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
            final String localName,
            final Attributes attrs,
            final Parser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        WorkflowProcessImpl process = (WorkflowProcessImpl) parser.getParent();

        final String identifier = attrs.getValue("identifier");
        final String type = attrs.getValue("type");
        process.addImports(Collections.singleton(type));
        emptyAttributeCheck(localName, "identifier", identifier, parser);
        emptyAttributeCheck(localName, "type", type, parser);

        Map<String, String> map = process.getGlobals();
        if (map == null) {
            map = new HashMap<>();
            process.setGlobals(map);
        }
        map.put(identifier, type);

        VariableScope variableScope = (VariableScope) process.getDefaultContext(VariableScope.VARIABLE_SCOPE);
        List<Variable> variables = variableScope.getVariables();
        Variable variable = new Variable();
        variable.setId(identifier);
        variable.setType(DataTypeResolver.fromType(type, parser.getClassLoader()));
        // if name is given use it as variable name instead of id
        if (identifier != null && identifier.length() > 0) {
            variable.setName(identifier);
            variable.setMetaData(identifier, variable.getName());
        } else {
            variable.setName(identifier);
        }
        variable.setMetaData(identifier, variable.getName());
        variables.add(variable);

        return null;
    }

    public Object end(final String uri,
            final String localName,
            final Parser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return null;
    }

}
