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

package org.jbpm.casemgmt.cmmn.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.casemgmt.cmmn.core.FileItemDefinition;
import org.jbpm.casemgmt.cmmn.core.Role;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.BooleanDataType;
import org.jbpm.process.core.datatype.impl.type.FloatDataType;
import org.jbpm.process.core.datatype.impl.type.IntegerDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.core.datatype.impl.type.UndefinedDataType;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FileItemHandler extends BaseAbstractHandler implements Handler {

    public FileItemHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet<>();
            this.validParents.add(RuleFlowProcess.class);
            this.validPeers = new HashSet<>();
            this.validPeers.add(null);
            this.validPeers.add(Variable.class);
            this.validPeers.add(Role.class);
            this.allowNesting = false;
        }
    }

    @SuppressWarnings("unchecked")
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);

        String id = attrs.getValue("id");
        String name = attrs.getValue("name");
        String definitionRef = attrs.getValue("definitionRef");

        ProcessBuildData buildData = (ProcessBuildData) parser.getData();

        Map<String, FileItemDefinition> itemDefinitions = (Map<String, FileItemDefinition>) buildData.getMetaData("FileItemDefinitions");

        FileItemDefinition definition = itemDefinitions.get(definitionRef);
        if (name == null) {
            definition.getName();
        }
        String structureRef = definition.getStructureRef();

        Map<String, String> fileItems = (Map<String, String>) buildData.getMetaData("FileItems");
        if (fileItems == null) {
            fileItems = new HashMap<String, String>();
            buildData.setMetaData("FileItems", fileItems);
        }
        fileItems.put(id, name);

        Variable variable = new Variable();
        variable.setName(VariableScope.CASE_FILE_PREFIX + name);

        Object parent = parser.getParent();
        if (parent instanceof ContextContainer) {
            ContextContainer contextContainer = (ContextContainer) parent;
            VariableScope variableScope = (VariableScope) contextContainer.getDefaultContext(VariableScope.VARIABLE_SCOPE);
            List<Variable> variables = variableScope.getVariables();

            // if there any prefix in the structureRef e.g. feel: then remove it
            if (structureRef.contains(":")) {
                structureRef = structureRef.split(":")[1];
            }

            variables.add(variable);

            if (UndefinedDataType.getInstance().equals(variable.getType()) && structureRef != null) {
                DataType dataType = new ObjectDataType();

                if ("java.lang.Boolean".equals(structureRef) || "Boolean".equalsIgnoreCase(structureRef)) {
                    dataType = new BooleanDataType();

                } else if ("java.lang.Integer".equals(structureRef) || "Integer".equalsIgnoreCase(structureRef) 
                        || "java.lang.Number".equals(structureRef) || "Number".equalsIgnoreCase(structureRef)) {
                    dataType = new IntegerDataType();

                } else if ("java.lang.Float".equals(structureRef) || "Float".equalsIgnoreCase(structureRef)) {
                    dataType = new FloatDataType();

                } else if ("java.lang.String".equals(structureRef) || "String".equalsIgnoreCase(structureRef)) {
                    dataType = new StringDataType();

                } else if ("java.lang.Object".equals(structureRef) || "Object".equalsIgnoreCase(structureRef)) {
                    // use FQCN of Object
                    dataType = new ObjectDataType("java.lang.Object");

                } else {
                    dataType = new ObjectDataType(structureRef, parser.getClassLoader());
                }

                variable.setType(dataType);
            }

            ((ProcessBuildData) parser.getData()).setMetaData("Variable", variable);
            return variable;
        }

        return variable;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return Variable.class;
    }

}
