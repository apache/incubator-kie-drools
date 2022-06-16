package org.optaplanner.core.impl.domain.variable.listener.support.violation;

import java.util.Objects;
import java.util.function.Consumer;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;

final class ShadowVariableSnapshot {

    private final ShadowVariableDescriptor<?> shadowVariableDescriptor;
    private final Object entity;
    private final Object originalValue;

    private ShadowVariableSnapshot(ShadowVariableDescriptor<?> shadowVariableDescriptor, Object entity, Object originalValue) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        this.entity = entity;
        this.originalValue = originalValue;
    }

    static ShadowVariableSnapshot of(ShadowVariableDescriptor<?> shadowVariableDescriptor, Object entity) {
        return new ShadowVariableSnapshot(shadowVariableDescriptor, entity, shadowVariableDescriptor.getValue(entity));
    }

    void validate(Consumer<String> violationMessageConsumer) {
        Object newValue = shadowVariableDescriptor.getValue(entity);
        if (!Objects.equals(originalValue, newValue)) {
            violationMessageConsumer.accept("    The entity (" + entity
                    + ")'s shadow variable (" + shadowVariableDescriptor.getSimpleEntityAndVariableName()
                    + ")'s corrupted value (" + originalValue + ") changed to uncorrupted value (" + newValue
                    + ") after all " + VariableListener.class.getSimpleName()
                    + "s were triggered without changes to the genuine variables.\n"
                    + "      Maybe the " + VariableListener.class.getSimpleName() + " class ("
                    + shadowVariableDescriptor.getVariableListenerClass().getSimpleName()
                    + ") for that shadow variable (" + shadowVariableDescriptor.getSimpleEntityAndVariableName()
                    + ") forgot to update it when one of its sources changed.\n");
        }
    }

    ShadowVariableDescriptor<?> getShadowVariableDescriptor() {
        return shadowVariableDescriptor;
    }

    @Override
    public String toString() {
        return entity + "." + shadowVariableDescriptor.getVariableName() + " = " + originalValue;
    }
}
