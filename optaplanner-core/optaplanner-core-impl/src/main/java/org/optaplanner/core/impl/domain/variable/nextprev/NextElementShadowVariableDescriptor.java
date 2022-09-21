package org.optaplanner.core.impl.domain.variable.nextprev;

import org.optaplanner.core.api.domain.variable.NextElementShadowVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

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
    public Class<NextElementVariableListener> getVariableListenerClass() {
        return NextElementVariableListener.class;
    }

    @Override
    public NextElementVariableListener<Solution_> buildVariableListener(InnerScoreDirector<Solution_, ?> scoreDirector) {
        return new NextElementVariableListener<>(this, sourceVariableDescriptor);
    }
}
