/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo.compiled;

import org.drools.core.base.ClassFieldReader;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.Sink;
import org.drools.core.rule.ContextEntry;

/**
 * This handler is used as a base class for all {@link org.kie.reteoo.compiled.NetworkHandler}s used for
 * generating a compiled network. It provides methods to return the variable type and names used for storing
 * refernces to different {@link org.kie.common.NetworkNode}s and variable names for {@link ClassFieldReader}s.
 */
abstract class AbstractCompilerHandler extends NetworkHandlerAdaptor {
    protected static final String NEWLINE = "\n";

    private static final String MAP_VARIABLE_NAME_SUFFIX = "ToNodeId";

    protected Class<?> getVariableType(AlphaNode alphaNode) {

        // for alphas, we use the constraint of the alpha for the declaration
        return alphaNode.getConstraint().getClass();
    }

    protected Class<?> getVariableType(Sink sink) {

        return sink.getClass();
    }

    protected String getVariableName(AlphaNode alphaNode) {
        Class<?> variableType = getVariableType(alphaNode);

        return getVariableName(variableType, alphaNode.getId());
    }

    protected String getContextVariableName(AlphaNode alphaNode) {
        Class<?> variableType = ContextEntry.class;

        return getVariableName(variableType, alphaNode.getId());
    }

    protected String getVariableName(Sink sink) {
        Class<?> variableType = getVariableType(sink);

        return getVariableName(variableType, sink.getId());
    }

    protected String getVariableName() {
        return MAP_VARIABLE_NAME_SUFFIX;
    }

    /**
     * Returns a variable name based on the simple name of the specified class appended with the specified
     * nodeId.
     *
     * @param clazz  class whose simple name is lowercased and user as the prefix of the variable name
     * @param nodeId id of {@link org.kie.common.NetworkNode}
     * @return variable name
     * @see Class#getSimpleName()
     */
    private String getVariableName(Class clazz, int nodeId) {
        String type = clazz.getSimpleName();
        return Character.toLowerCase(type.charAt(0)) + type.substring(1) + nodeId;
    }
}
