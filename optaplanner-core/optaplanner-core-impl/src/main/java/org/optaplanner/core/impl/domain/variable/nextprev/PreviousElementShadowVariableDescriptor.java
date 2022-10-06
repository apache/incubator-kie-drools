package org.optaplanner.core.impl.domain.variable.nextprev;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.domain.variable.PreviousElementShadowVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerWithSources;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public class PreviousElementShadowVariableDescriptor<Solution_>
        extends AbstractNextPrevElementShadowVariableDescriptor<Solution_> {

    public PreviousElementShadowVariableDescriptor(
            EntityDescriptor<Solution_> entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    @Override
    String getSourceVariableName() {
        return variableMemberAccessor.getAnnotation(PreviousElementShadowVariable.class).sourceVariableName();
    }

    @Override
    String getAnnotationName() {
        return PreviousElementShadowVariable.class.getSimpleName();
    }

    @Override
    public Collection<Class<? extends AbstractVariableListener>> getVariableListenerClasses() {
        return Collections.singleton(PreviousElementVariableListener.class);
    }

    @Override
    public Iterable<VariableListenerWithSources<Solution_>> buildVariableListeners(SupplyManager supplyManager) {
        return new VariableListenerWithSources<>(new PreviousElementVariableListener<>(this, sourceVariableDescriptor),
                sourceVariableDescriptor).toCollection();
    }
}
