package org.optaplanner.core.impl.domain.variable.descriptor;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class VariableDescriptor<Solution_> {

    protected final EntityDescriptor<Solution_> entityDescriptor;

    protected final MemberAccessor variableMemberAccessor;
    protected final String variableName;

    protected List<ShadowVariableDescriptor<Solution_>> sinkVariableDescriptorList = new ArrayList<>(4);

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public VariableDescriptor(EntityDescriptor<Solution_> entityDescriptor, MemberAccessor variableMemberAccessor) {
        this.entityDescriptor = entityDescriptor;
        this.variableMemberAccessor = variableMemberAccessor;
        variableName = variableMemberAccessor.getName();
        if (variableMemberAccessor.getType().isPrimitive()) {
            throw new IllegalStateException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + PlanningVariable.class.getSimpleName()
                    + " annotated member (" + variableMemberAccessor
                    + ") that returns a primitive type (" + variableMemberAccessor.getType()
                    + "). This means it cannot represent an uninitialized variable as null"
                    + " and the Construction Heuristics think it's already initialized.\n"
                    + "Maybe let the member (" + getSimpleEntityAndVariableName()
                    + ") return its primitive wrapper type instead.");
        }
    }

    public EntityDescriptor<Solution_> getEntityDescriptor() {
        return entityDescriptor;
    }

    public String getVariableName() {
        return variableName;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public String getSimpleEntityAndVariableName() {
        return entityDescriptor.getEntityClass().getSimpleName() + "." + variableName;
    }

    public Class<?> getVariablePropertyType() {
        return variableMemberAccessor.getType();
    }

    public abstract void linkVariableDescriptors(DescriptorPolicy descriptorPolicy);

    public boolean isGenuineListVariable() {
        return false;
    }

    // ************************************************************************
    // Shadows
    // ************************************************************************

    public void registerSinkVariableDescriptor(ShadowVariableDescriptor<Solution_> shadowVariableDescriptor) {
        sinkVariableDescriptorList.add(shadowVariableDescriptor);
    }

    /**
     * Inverse of {@link ShadowVariableDescriptor#getSourceVariableDescriptorList()}.
     *
     * @return never null, only direct shadow variables that are affected by this variable
     */
    public List<ShadowVariableDescriptor<Solution_>> getSinkVariableDescriptorList() {
        return sinkVariableDescriptorList;
    }

    /**
     * @param value never null
     * @return true if it might be an anchor, false if it is definitely not an anchor
     */
    public boolean isValuePotentialAnchor(Object value) {
        return !entityDescriptor.getEntityClass().isAssignableFrom(value.getClass());
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public Object getValue(Object entity) {
        return variableMemberAccessor.executeGetter(entity);
    }

    public void setValue(Object entity, Object value) {
        variableMemberAccessor.executeSetter(entity, value);
    }

    public String getMemberAccessorSpeedNote() {
        return variableMemberAccessor.getSpeedNote();
    }

    public abstract boolean isGenuineAndUninitialized(Object entity);
}
