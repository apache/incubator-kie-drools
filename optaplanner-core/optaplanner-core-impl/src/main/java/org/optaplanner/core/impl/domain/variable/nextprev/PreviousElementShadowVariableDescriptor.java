package org.optaplanner.core.impl.domain.variable.nextprev;

import org.optaplanner.core.api.domain.variable.PreviousElementShadowVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

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
    public Class<PreviousElementVariableListener> getVariableListenerClass() {
        return PreviousElementVariableListener.class;
    }

    @Override
    public PreviousElementVariableListener<Solution_> buildVariableListener(InnerScoreDirector<Solution_, ?> scoreDirector) {
        return new PreviousElementVariableListener<>(this, sourceVariableDescriptor);
    }
}
