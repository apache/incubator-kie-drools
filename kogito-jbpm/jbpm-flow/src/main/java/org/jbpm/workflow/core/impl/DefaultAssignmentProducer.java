/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.core.impl;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.core.datatype.impl.type.UndefinedDataType;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.AssignmentProducer;
import org.jbpm.process.instance.impl.util.TypeTransformer;
import org.jbpm.workflow.instance.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAssignmentProducer implements AssignmentProducer {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultAssignmentProducer.class);

    private TypeTransformer typeTransformer;

    private NodeInstance nodeInstance;

    public DefaultAssignmentProducer(NodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
        this.typeTransformer = new TypeTransformer(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public void accept(String target, Object value) {
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) nodeInstance.resolveContextInstance(VariableScope.VARIABLE_SCOPE, target);
        if (variableScopeInstance == null && nodeInstance != null) {
            nodeInstance.setVariable(target, value);
            return;
        }

        // proper information about the type
        Variable varDef = variableScopeInstance.getVariableScope().findVariable(target);
        DataType dataType = varDef.getType();

        // undefined or null we don't need to compute anything
        if (value == null || dataType instanceof UndefinedDataType) {
            variableScopeInstance.setVariable(nodeInstance, target, value);
            return;
        }

        // we try to convert with the converter
        // only if there is a TypeConverter registered for the data type
        if (value instanceof String) {
            value = dataType.readValue((String) value);
        }

        // the dataType is already the type
        if (dataType.verifyDataType(value)) {
            variableScopeInstance.setVariable(nodeInstance, target, value);
            return;
        }

        // if we use some strict variable this should not be needed but test require this.
        // this is some heuristics to try to transform stuff into the target type
        if (value != null && !(value instanceof Throwable)) {
            try {
                if (!dataType.getStringType().endsWith("java.lang.Object") && dataType instanceof ObjectDataType) {
                    value = typeTransformer.transform(value, ((ObjectDataType) dataType).getObjectClass());

                } else if (!(dataType instanceof StringDataType) && !(dataType instanceof ObjectDataType)) {
                    value = typeTransformer.transform(value, dataType.getStringType());
                }
            } catch (Exception e) {
                logger.debug("error trying to transform value {}", value, e);
            }
        }

        if (value != null && !dataType.verifyDataType(value)) {
            if (dataType instanceof StringDataType) {
                // last chance to put proper value
                value = value.toString();
            } else {
                throw new IllegalArgumentException("value " + value + " does not match " + dataType.getStringType());
            }
        }

        variableScopeInstance.setVariable(nodeInstance, target, value);
    }

}
