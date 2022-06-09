package org.optaplanner.core.impl.domain.solution.cloner.gizmo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberDescriptor;
import org.optaplanner.core.impl.domain.solution.cloner.DeepCloningUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public class GizmoSolutionOrEntityDescriptor {
    SolutionDescriptor<?> solutionDescriptor;
    Map<Field, GizmoMemberDescriptor> solutionFieldToMemberDescriptorMap;
    Set<Field> deepClonedFields;
    Set<Field> shallowlyClonedFields;

    public GizmoSolutionOrEntityDescriptor(SolutionDescriptor<?> solutionDescriptor, Class<?> entityOrSolutionClass) {
        this(solutionDescriptor, entityOrSolutionClass,
                getFieldsToSolutionFieldToMemberDescriptorMap(entityOrSolutionClass, new HashMap<>()));
    }

    public GizmoSolutionOrEntityDescriptor(SolutionDescriptor<?> solutionDescriptor, Class<?> entityOrSolutionClass,
            Map<Field, GizmoMemberDescriptor> solutionFieldToMemberDescriptorMap) {
        this.solutionDescriptor = solutionDescriptor;
        this.solutionFieldToMemberDescriptorMap = solutionFieldToMemberDescriptorMap;
        deepClonedFields = new HashSet<>();
        shallowlyClonedFields = new HashSet<>();

        DeepCloningUtils deepCloningUtils = new DeepCloningUtils(solutionDescriptor);
        for (Field field : solutionFieldToMemberDescriptorMap.keySet()) {
            if (deepCloningUtils.getDeepCloneDecision(field, entityOrSolutionClass, field.getType())) {
                deepClonedFields.add(field);
            } else {
                shallowlyClonedFields.add(field);
            }
        }
    }

    private static Map<Field, GizmoMemberDescriptor> getFieldsToSolutionFieldToMemberDescriptorMap(Class<?> clazz,
            Map<Field, GizmoMemberDescriptor> solutionFieldToMemberDescriptorMap) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                solutionFieldToMemberDescriptorMap.put(field, new GizmoMemberDescriptor(field));
            }
        }
        if (clazz.getSuperclass() != null) {
            getFieldsToSolutionFieldToMemberDescriptorMap(clazz.getSuperclass(), solutionFieldToMemberDescriptorMap);
        }
        return solutionFieldToMemberDescriptorMap;
    }

    public SolutionDescriptor<?> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public Set<GizmoMemberDescriptor> getShallowClonedMemberDescriptors() {
        return solutionFieldToMemberDescriptorMap.keySet().stream()
                .filter(field -> shallowlyClonedFields.contains(field))
                .map(solutionFieldToMemberDescriptorMap::get).collect(Collectors.toSet());
    }

    public Set<Field> getDeepClonedFields() {
        return deepClonedFields;
    }

    public GizmoMemberDescriptor getMemberDescriptorForField(Field field) {
        return solutionFieldToMemberDescriptorMap.get(field);
    }

}
