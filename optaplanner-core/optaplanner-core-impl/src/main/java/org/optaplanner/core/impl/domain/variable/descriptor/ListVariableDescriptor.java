package org.optaplanner.core.impl.domain.variable.descriptor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;

public class ListVariableDescriptor<Solution_> extends GenuineVariableDescriptor<Solution_> {

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public ListVariableDescriptor(EntityDescriptor<Solution_> entityDescriptor, MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    protected void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
        PlanningListVariable planningVariableAnnotation = variableMemberAccessor.getAnnotation(PlanningListVariable.class);
        processValueRangeRefs(descriptorPolicy, planningVariableAnnotation.valueRangeProviderRefs());
    }

    @Override
    protected void processValueRangeRefs(DescriptorPolicy descriptorPolicy, String[] valueRangeProviderRefs) {
        List<String> fromEntityValueRangeProviderRefs = Arrays.stream(valueRangeProviderRefs)
                .filter(descriptorPolicy::hasFromEntityValueRangeProvider)
                .collect(Collectors.toList());
        if (!fromEntityValueRangeProviderRefs.isEmpty()) {
            throw new IllegalArgumentException("@" + ValueRangeProvider.class.getSimpleName()
                    + " on a @" + PlanningEntity.class.getSimpleName()
                    + " is not supported with a list variable (" + this + ").\n"
                    + "Maybe move the valueRangeProvider" + (fromEntityValueRangeProviderRefs.size() > 1 ? "s" : "")
                    + " (" + fromEntityValueRangeProviderRefs
                    + ") from the entity class to the @" + PlanningSolution.class.getSimpleName() + " class.");
        }
        super.processValueRangeRefs(descriptorPolicy, valueRangeProviderRefs);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isGenuineListVariable() {
        return true;
    }

    @Override
    public boolean isListVariable() {
        return true;
    }

    @Override
    public boolean isChained() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public boolean acceptsValueType(Class<?> valueType) {
        Class<?> variableTypeArgument = ConfigUtils.extractCollectionGenericTypeParameterStrictly(
                "entityClass", entityDescriptor.getEntityClass(),
                variableMemberAccessor.getType(), variableMemberAccessor.getGenericType(),
                PlanningListVariable.class, variableMemberAccessor.getName());
        return variableTypeArgument.isAssignableFrom(valueType);
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    @Override
    public boolean isInitialized(Object entity) {
        return true;
    }

    public List<Object> getListVariable(Object entity) {
        return (List<Object>) getValue(entity);
    }

    public Object removeElement(Object entity, int index) {
        return getListVariable(entity).remove(index);
    }

    public void addElement(Object entity, int index, Object element) {
        getListVariable(entity).add(index, element);
    }

    public Object getElement(Object entity, int index) {
        return getListVariable(entity).get(index);
    }

    public Object setElement(Object entity, int index, Object element) {
        return getListVariable(entity).set(index, element);
    }

    public int getListSize(Object entity) {
        return getListVariable(entity).size();
    }
}
