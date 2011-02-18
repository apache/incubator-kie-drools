/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.compiled;

import org.drools.base.ClassFieldReader;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.spi.PropagationContext;

/**
 * todo: document
 *
 */
class AssertHandler extends AbstractCompilerHandler {
    private static final String LOCAL_FACT_VAR_NAME = "fact";

    private static final String FACT_HANDLE_PARAM_TYPE = InternalFactHandle.class.getName();
    private static final String PROP_CONTEXT_PARAM_TYPE = PropagationContext.class.getName();
    private static final String WORKING_MEMORY_PARAM_TYPE = InternalWorkingMemory.class.getName();

    private static final String FACT_HANDLE_PARAM_NAME = "handle";
    private static final String PROP_CONTEXT_PARAM_NAME = "context";
    private static final String WORKING_MEMORY_PARAM_NAME = "wm";

    private static final String ASSERT_METHOD_SIGNATURE = "public final void assertObject("
            + FACT_HANDLE_PARAM_TYPE + " " + FACT_HANDLE_PARAM_NAME + ","
            + PROP_CONTEXT_PARAM_TYPE + " " + PROP_CONTEXT_PARAM_NAME + ","
            + WORKING_MEMORY_PARAM_TYPE + " " + WORKING_MEMORY_PARAM_NAME + "){";

    /**
     * This flag is used to instruct the AssertHandler to tell it to generate a local varible
     * in the {@link org.drools.reteoo.compiled.CompiledNetwork#assertObject} for holding the value returned
     * from the {@link org.drools.common.InternalFactHandle#getObject()}.
     *
     * This is only needed if there is at least 1 set of hashed alpha nodes in the network
     */
    private final boolean alphaNetContainsHashedField;

    private final StringBuilder builder;
    private final String factClassName;

    AssertHandler(StringBuilder builder, String factClassName) {
        this(builder, factClassName, false);
    }

    AssertHandler(StringBuilder builder, String factClassName, boolean alphaNetContainsHashedField) {
        this.builder = builder;
        this.factClassName = factClassName;
        this.alphaNetContainsHashedField = alphaNetContainsHashedField;
    }

    @Override
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {
        builder.append(ASSERT_METHOD_SIGNATURE).append(NEWLINE);

        // we only need to create a reference to the object, not handle, if there is a hashed alpha in the network
        if (alphaNetContainsHashedField) {
            // example of what this will look like
            // ExampleFact fact = (ExampleFact) handle.getObject();
            builder.append(factClassName).append(" ").append(LOCAL_FACT_VAR_NAME).
                    append(" = (").append(factClassName).append(")").
                    append(FACT_HANDLE_PARAM_NAME).append(".getObject();").
                    append(NEWLINE);
        }
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
        builder.append(getVariableName(betaNode)).append(".assertObject(").
                append(FACT_HANDLE_PARAM_NAME).append(",").
                append(PROP_CONTEXT_PARAM_NAME).append(",").
                append(WORKING_MEMORY_PARAM_NAME).append(");").append(NEWLINE);
    }

    @Override
    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
        builder.append(getVariableName(leftInputAdapterNode)).append(".assertObject(").
                append(FACT_HANDLE_PARAM_NAME).append(",").
                append(PROP_CONTEXT_PARAM_NAME).append(",").
                append(WORKING_MEMORY_PARAM_NAME).append(");").append(NEWLINE);
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        builder.append("if ( ").append(getVariableName(alphaNode)).
                append(".isAllowed(").append(FACT_HANDLE_PARAM_NAME).append(",").
                append(WORKING_MEMORY_PARAM_NAME).append(", ").
                append(getContextVariableName(alphaNode)).append(") ) {").append(NEWLINE);

    }

    @Override
    public void endNonHashedAlphaNode(AlphaNode alphaNode) {
        // close if statement
        builder.append("}").append(NEWLINE);
    }

    @Override
    public void startHashedAlphaNodes(ClassFieldReader hashedFieldReader) {
        String attributeName = hashedFieldReader.getFieldName();
        String localVariableName = attributeName + "NodeId";

        // todo get accessor smarter because of booleans. Note that right now booleans wouldn't be hashed
        String attributeGetterName = "get" + Character.toTitleCase(attributeName.charAt(0)) + attributeName.substring(1);

        // get the attribute from the fact that we are switching over
        builder.append("Integer ").append(localVariableName);
        // todo we are casting to Integer because generics aren't supported
        builder.append(" = (Integer)").append(getVariableName(hashedFieldReader)).append(".get(").
                append(LOCAL_FACT_VAR_NAME).append(".").append(attributeGetterName)
                .append("());").append(NEWLINE);

        // ensure that the value is present in the node map
        builder.append("if(").append(localVariableName).append(" != null) {").append(NEWLINE);
        // todo we had the .intValue() because JANINO has a problem with it
        builder.append("switch(").append(localVariableName).append(".intValue()) {").append(NEWLINE);
    }

    @Override
    public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        builder.append("case ").append(hashedAlpha.getId()).append(" : ").append(NEWLINE);
    }

    @Override
    public void endHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        builder.append("break;").append(NEWLINE);
    }

    @Override
    public void endHashedAlphaNodes(ClassFieldReader hashedFieldReader) {
        // close switch statement
        builder.append("}").append(NEWLINE);
        // and if statement for ensuring non-null
        builder.append("}").append(NEWLINE);
    }

    @Override
    public void endObjectTypeNode(ObjectTypeNode objectTypeNode) {
        // close the assertObject method
        builder.append("}").append(NEWLINE);
    }
}
