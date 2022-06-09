package org.optaplanner.core.impl.domain.variable.index;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.variable.IndexShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class IndexShadowVariableDescriptor<Solution_> extends ShadowVariableDescriptor<Solution_> {

    protected ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    public IndexShadowVariableDescriptor(
            EntityDescriptor<Solution_> entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
        if (!variableMemberAccessor.getType().equals(Integer.class) && !variableMemberAccessor.getType().equals(Long.class)) {
            throw new IllegalStateException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has an @" + IndexShadowVariable.class.getSimpleName()
                    + " annotated member (" + variableMemberAccessor
                    + ") of type (" + variableMemberAccessor.getType()
                    + ") which cannot represent an index in a list.\n"
                    + "The @" + IndexShadowVariable.class.getSimpleName() + " annotated member type must be "
                    + Integer.class + " or " + Long.class + ".");
        }
    }

    @Override
    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        // Do nothing
    }

    @Override
    public void linkVariableDescriptors(DescriptorPolicy descriptorPolicy) {
        linkShadowSources(descriptorPolicy);
    }

    private void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        String sourceVariableName = variableMemberAccessor.getAnnotation(IndexShadowVariable.class).sourceVariableName();
        List<EntityDescriptor<Solution_>> entitiesWithSourceVariable =
                entityDescriptor.getSolutionDescriptor().getEntityDescriptors().stream()
                        .filter(entityDescriptor -> entityDescriptor.hasVariableDescriptor(sourceVariableName))
                        .collect(Collectors.toList());
        if (entitiesWithSourceVariable.isEmpty()) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has an @" + IndexShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a valid planning variable on any of the entity classes ("
                    + entityDescriptor.getSolutionDescriptor().getEntityDescriptors() + ").");
        }
        if (entitiesWithSourceVariable.size() > 1) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has an @" + IndexShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a unique planning variable."
                    + " A planning variable with the name (" + sourceVariableName + ") exists on multiple entity classes ("
                    + entitiesWithSourceVariable + ").");
        }
        VariableDescriptor<Solution_> variableDescriptor =
                entitiesWithSourceVariable.get(0).getVariableDescriptor(sourceVariableName);
        if (variableDescriptor == null) {
            throw new IllegalStateException(
                    "Impossible state: variableDescriptor (" + variableDescriptor + ") is null"
                            + " but previous checks indicate that the entityClass (" + entitiesWithSourceVariable.get(0)
                            + ") has a planning variable with sourceVariableName (" + sourceVariableName + ").");
        }
        if (!(variableDescriptor instanceof ListVariableDescriptor)) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has an @" + IndexShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a @" + PlanningListVariable.class.getSimpleName() + ".");
        }
        sourceVariableDescriptor = (ListVariableDescriptor<Solution_>) variableDescriptor;
        sourceVariableDescriptor.registerSinkVariableDescriptor(this);
    }

    @Override
    public List<VariableDescriptor<Solution_>> getSourceVariableDescriptorList() {
        return Collections.singletonList(sourceVariableDescriptor);
    }

    @Override
    public Class<IndexVariableListener> getVariableListenerClass() {
        return IndexVariableListener.class;
    }

    @Override
    public IndexVariableDemand<Solution_> getProvidedDemand() {
        return new IndexVariableDemand<>(sourceVariableDescriptor);
    }

    @Override
    public IndexVariableListener<Solution_> buildVariableListener(InnerScoreDirector<Solution_, ?> scoreDirector) {
        return new IndexVariableListener<>(this, sourceVariableDescriptor);
    }

    @Override
    public Integer getValue(Object entity) {
        return (Integer) super.getValue(entity);
    }
}
