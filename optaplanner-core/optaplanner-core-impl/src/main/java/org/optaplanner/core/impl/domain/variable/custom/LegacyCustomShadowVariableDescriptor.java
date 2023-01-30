package org.optaplanner.core.impl.domain.variable.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerWithSources;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class LegacyCustomShadowVariableDescriptor<Solution_> extends ShadowVariableDescriptor<Solution_> {

    protected LegacyCustomShadowVariableDescriptor<Solution_> refVariableDescriptor;

    protected Class<? extends VariableListener> variableListenerClass;
    protected List<VariableDescriptor<Solution_>> sourceVariableDescriptorList;

    public LegacyCustomShadowVariableDescriptor(EntityDescriptor<Solution_> entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    @Override
    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processPropertyAnnotations(descriptorPolicy);
    }

    private void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
        CustomShadowVariable shadowVariableAnnotation = variableMemberAccessor
                .getAnnotation(CustomShadowVariable.class);
        PlanningVariableReference variableListenerRef = shadowVariableAnnotation.variableListenerRef();
        if (variableListenerRef.variableName().equals("")) {
            variableListenerRef = null;
        }
        variableListenerClass = shadowVariableAnnotation.variableListenerClass();
        if (variableListenerClass == CustomShadowVariable.NullVariableListener.class) {
            variableListenerClass = null;
        }
        PlanningVariableReference[] sources = shadowVariableAnnotation.sources();
        if (variableListenerRef != null) {
            if (variableListenerClass != null || sources.length > 0) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a @" + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with a non-null variableListenerRef (" + variableListenerRef
                        + "), so it cannot have a variableListenerClass (" + variableListenerClass
                        + ") nor any sources (" + Arrays.toString(sources) + ").");
            }
        } else {
            if (variableListenerClass == null) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a @" + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") which lacks a variableListenerClass (" + variableListenerClass + ").");
            }
            if (sources.length < 1) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a @" + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with sources (" + Arrays.toString(sources)
                        + ") which is empty.");
            }
        }
    }

    public boolean isRef() {
        // refVariableDescriptor might not be initialized yet, but variableListenerClass will
        return variableListenerClass == null;
    }

    @Override
    public void linkVariableDescriptors(DescriptorPolicy descriptorPolicy) {
        linkShadowSources(descriptorPolicy);
    }

    private void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        CustomShadowVariable shadowVariableAnnotation = variableMemberAccessor
                .getAnnotation(CustomShadowVariable.class);
        PlanningVariableReference variableListenerRef = shadowVariableAnnotation.variableListenerRef();
        if (variableListenerRef.variableName().equals("")) {
            variableListenerRef = null;
        }
        if (variableListenerRef != null) {
            EntityDescriptor<Solution_> refEntityDescriptor;
            Class<?> refEntityClass = variableListenerRef.entityClass();
            if (refEntityClass.equals(PlanningVariableReference.NullEntityClass.class)) {
                refEntityDescriptor = entityDescriptor;
            } else {
                refEntityDescriptor = entityDescriptor.getSolutionDescriptor().findEntityDescriptor(refEntityClass);
                if (refEntityDescriptor == null) {
                    throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                            + ") has a @" + CustomShadowVariable.class.getSimpleName()
                            + " annotated property (" + variableMemberAccessor.getName()
                            + ") with a refEntityClass (" + refEntityClass
                            + ") which is not a valid planning entity."
                            + "\nMaybe check the annotations of the class (" + refEntityClass + ")."
                            + "\nMaybe add the class (" + refEntityClass
                            + ") among planning entities in the solver configuration.");
                }
            }
            String refVariableName = variableListenerRef.variableName();
            VariableDescriptor<Solution_> uncastRefVariableDescriptor = refEntityDescriptor
                    .getVariableDescriptor(refVariableName);
            if (uncastRefVariableDescriptor == null) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a @" + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with refVariableName (" + refVariableName
                        + ") which is not a valid planning variable on entityClass ("
                        + refEntityDescriptor.getEntityClass() + ").\n"
                        + refEntityDescriptor.buildInvalidVariableNameExceptionMessage(refVariableName));
            }
            if (!(uncastRefVariableDescriptor instanceof LegacyCustomShadowVariableDescriptor)) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a @" + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with refVariable (" + uncastRefVariableDescriptor.getSimpleEntityAndVariableName()
                        + ") that lacks a @" + CustomShadowVariable.class.getSimpleName() + " annotation.");
            }
            refVariableDescriptor = (LegacyCustomShadowVariableDescriptor<Solution_>) uncastRefVariableDescriptor;
            if (refVariableDescriptor.isRef()) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a @" + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with refVariable (" + refVariableDescriptor + ") that is a reference itself too.");
            }
            refVariableDescriptor.registerSinkVariableDescriptor(this);
        } else {
            PlanningVariableReference[] sources = shadowVariableAnnotation.sources();
            sourceVariableDescriptorList = new ArrayList<>(sources.length);
            for (PlanningVariableReference source : sources) {
                EntityDescriptor<Solution_> sourceEntityDescriptor;
                Class<?> sourceEntityClass = source.entityClass();
                if (sourceEntityClass.equals(PlanningVariableReference.NullEntityClass.class)) {
                    sourceEntityDescriptor = entityDescriptor;
                } else {
                    sourceEntityDescriptor = entityDescriptor.getSolutionDescriptor()
                            .findEntityDescriptor(sourceEntityClass);
                    if (sourceEntityDescriptor == null) {
                        throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                                + ") has a @" + CustomShadowVariable.class.getSimpleName()
                                + " annotated property (" + variableMemberAccessor.getName()
                                + ") with a sourceEntityClass (" + sourceEntityClass
                                + ") which is not a valid planning entity."
                                + "\nMaybe check the annotations of the class (" + sourceEntityClass + ")."
                                + "\nMaybe add the class (" + sourceEntityClass
                                + ") among planning entities in the solver configuration.");
                    }
                }
                String sourceVariableName = source.variableName();
                VariableDescriptor<Solution_> sourceVariableDescriptor = sourceEntityDescriptor.getVariableDescriptor(
                        sourceVariableName);
                if (sourceVariableDescriptor == null) {
                    throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                            + ") has a @" + CustomShadowVariable.class.getSimpleName()
                            + " annotated property (" + variableMemberAccessor.getName()
                            + ") with sourceVariableName (" + sourceVariableName
                            + ") which is not a valid planning variable on entityClass ("
                            + sourceEntityDescriptor.getEntityClass() + ").\n"
                            + sourceEntityDescriptor.buildInvalidVariableNameExceptionMessage(sourceVariableName));
                }
                if (sourceVariableDescriptor.isGenuineListVariable()) {
                    throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                            + ") has a @" + CustomShadowVariable.class.getSimpleName()
                            + " annotated property (" + variableMemberAccessor.getName()
                            + ") with sourceVariableName (" + sourceVariableName
                            + ") which is a list variable.\n"
                            + "Custom shadow variables sourced on list variables are not yet supported.");
                }
                sourceVariableDescriptor.registerSinkVariableDescriptor(this);
                sourceVariableDescriptorList.add(sourceVariableDescriptor);
            }
        }
    }

    @Override
    public List<VariableDescriptor<Solution_>> getSourceVariableDescriptorList() {
        if (refVariableDescriptor != null) {
            return Collections.singletonList(refVariableDescriptor);
        }
        return sourceVariableDescriptorList;
    }

    @Override
    public Collection<Class<? extends AbstractVariableListener>> getVariableListenerClasses() {
        if (isRef()) {
            return refVariableDescriptor.getVariableListenerClasses();
        }
        return Collections.singleton(variableListenerClass);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Demand<?> getProvidedDemand() {
        throw new UnsupportedOperationException("Custom shadow variable cannot be demanded.");
    }

    @Override
    public boolean hasVariableListener() {
        return refVariableDescriptor == null;
    }

    @Override
    public Iterable<VariableListenerWithSources<Solution_>> buildVariableListeners(SupplyManager supplyManager) {
        if (refVariableDescriptor != null) {
            throw new IllegalStateException("The shadowVariableDescriptor (" + this
                    + ") references another shadowVariableDescriptor (" + refVariableDescriptor
                    + ") so it cannot build a " + VariableListener.class.getSimpleName() + ".");
        }
        VariableListener<Solution_, Object> variableListener =
                ConfigUtils.newInstance(this::toString, "variableListenerClass", variableListenerClass);
        return new VariableListenerWithSources<>(variableListener, sourceVariableDescriptorList).toCollection();
    }

}
