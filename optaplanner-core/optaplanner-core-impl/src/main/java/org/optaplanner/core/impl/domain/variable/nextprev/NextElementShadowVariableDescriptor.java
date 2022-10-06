package org.optaplanner.core.impl.domain.variable.nextprev;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.domain.variable.NextElementShadowVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerWithSources;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public class NextElementShadowVariableDescriptor<Solution_> extends AbstractNextPrevElementShadowVariableDescriptor<Solution_> {

    public NextElementShadowVariableDescriptor(
            EntityDescriptor<Solution_> entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    @Override
    String getSourceVariableName() {
        return variableMemberAccessor.getAnnotation(NextElementShadowVariable.class).sourceVariableName();
    }

    @Override
    String getAnnotationName() {
        return NextElementShadowVariable.class.getSimpleName();
    }

    @Override
    public Collection<Class<? extends AbstractVariableListener>> getVariableListenerClasses() {
        return Collections.singleton(NextElementVariableListener.class);
    }

    @Override
    public Iterable<VariableListenerWithSources<Solution_>> buildVariableListeners(SupplyManager supplyManager) {
        return new VariableListenerWithSources<>(new NextElementVariableListener<>(this, sourceVariableDescriptor),
                sourceVariableDescriptor).toCollection();
    }
}
