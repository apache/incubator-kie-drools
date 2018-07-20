package org.drools.core.reteoo.compiled;

import java.util.stream.Stream;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.PropagationContext;

abstract public class SwitchCompilerHandler extends AbstractCompilerHandler {

    protected static final String MODIFY_PREVIOUS_TUPLE_NAME = ModifyPreviousTuples.class.getName();
    protected static final String MODIFY_PREVIOUS_TUPLE_PARAM_NAME = "modifyPreviousTuples";
    protected final StringBuilder builder;
    private Class fieldType;

    static final String LOCAL_FACT_VAR_NAME = "fact";
    protected static final String FACT_HANDLE_PARAM_TYPE = InternalFactHandle.class.getName();
    protected static final String PROP_CONTEXT_PARAM_TYPE = PropagationContext.class.getName();
    protected static final String WORKING_MEMORY_PARAM_TYPE = InternalWorkingMemory.class.getName();

    static final String FACT_HANDLE_PARAM_NAME = "handle";
    static final String PROP_CONTEXT_PARAM_NAME = "context";
    static final String WORKING_MEMORY_PARAM_NAME = "wm";
    static final String ASSERT_METHOD_SIGNATURE = "public final void assertObject("
            + FACT_HANDLE_PARAM_TYPE + " " + FACT_HANDLE_PARAM_NAME + ","
            + PROP_CONTEXT_PARAM_TYPE + " " + PROP_CONTEXT_PARAM_NAME + ","
            + WORKING_MEMORY_PARAM_TYPE + " " + WORKING_MEMORY_PARAM_NAME + "){";

    protected SwitchCompilerHandler(StringBuilder builder) {
        this.builder = builder;
    }

    protected void generateSwitch(IndexableConstraint indexableConstraint) {
        final InternalReadAccessor fieldExtractor = indexableConstraint.getFieldExtractor();
        fieldType = fieldExtractor.getExtractToClass();

        if (canInlineValue()) {
            String switchVar = "switchVar";
            builder.append(fieldType.getCanonicalName())
                    .append(" ")
                    .append(switchVar);
            builder.append(" = ")
                    .append("(" + fieldType.getCanonicalName() + ")")
                    .append("readAccessor.getValue(")
                    .append(LOCAL_FACT_VAR_NAME)
                    .append(");").append(NEWLINE);

            if (fieldType.isPrimitive()) {
                builder.append("if(true) {").append(NEWLINE);
            } else {
                builder.append("if(switchVar != null) {").append(NEWLINE);
            }
            builder.append("switch(").append(switchVar).append(")").append("{").append(NEWLINE);
        } else {

            String localVariableName = "NodeId";

            builder.append("Integer ").append(localVariableName);
            // todo we are casting to Integer because generics aren't supported
            builder.append(" = (Integer)").append(getVariableName())
                    .append(".get(")
                    .append("readAccessor.getValue(")
                    .append(LOCAL_FACT_VAR_NAME).append(")")
                    .append(");").append(NEWLINE);

            // ensure that the value is present in the node map
            builder.append("if(").append(localVariableName).append(" != null) {").append(NEWLINE);
            // todo we had the .intValue() because JANINO has a problem with it
            builder.append("switch(").append(localVariableName).append(".intValue()) {").append(NEWLINE);
        }
    }

    protected void generateSwitchCase(AlphaNode hashedAlpha, Object hashedValue) {
        if (canInlineValue()) {

            final Object quotedHashedValue;
            if (hashedValue instanceof String) {
                quotedHashedValue = String.format("\"%s\"", hashedValue);
            } else {
                quotedHashedValue = hashedValue;
            }

            builder.append("case ")
                    .append(quotedHashedValue)
                    .append(" : ").append(NEWLINE);
        } else {
            builder.append("case ").append(hashedAlpha.getId()).append(" : ").append(NEWLINE);
        }
    }

    protected boolean canInlineValue() {
        return Stream.of(String.class, Integer.class, int.class).anyMatch(c -> c.isAssignableFrom(fieldType));
    }

    @Override
    public void nullCaseAlphaNodeStart(AlphaNode hashedAlpha) {
        if (canInlineValue()) {
            builder.append("else { ");
        }
    }

    @Override
    public void nullCaseAlphaNodeEnd(AlphaNode hashedAlpha) {
        if (canInlineValue()) {
            builder.append("}").append(NEWLINE);
        }
    }
}
