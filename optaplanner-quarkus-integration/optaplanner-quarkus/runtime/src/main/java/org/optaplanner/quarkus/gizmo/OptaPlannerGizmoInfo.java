package org.optaplanner.quarkus.gizmo;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Map;

public final class OptaPlannerGizmoInfo {
    Map<String, Type> gizmoMemberAccessorNameToGenericType;
    Map<String, AnnotatedElement> gizmoMemberAccessorNameToAnnotatedElement;

    public OptaPlannerGizmoInfo() {
    }

    public OptaPlannerGizmoInfo(
            Map<String, Type> gizmoMemberAccessorNameToGenericType,
            Map<String, AnnotatedElement> gizmoMemberAccessorNameToAnnotatedElement) {
        this.gizmoMemberAccessorNameToGenericType = gizmoMemberAccessorNameToGenericType;
        this.gizmoMemberAccessorNameToAnnotatedElement = gizmoMemberAccessorNameToAnnotatedElement;
    }

    public Map<String, Type> getGizmoMemberAccessorNameToGenericType() {
        return gizmoMemberAccessorNameToGenericType;
    }

    public void setGizmoMemberAccessorNameToGenericType(Map<String, Type> gizmoMemberAccessorNameToGenericType) {
        this.gizmoMemberAccessorNameToGenericType = gizmoMemberAccessorNameToGenericType;
    }

    public Map<String, AnnotatedElement> getGizmoMemberAccessorNameToAnnotatedElement() {
        return gizmoMemberAccessorNameToAnnotatedElement;
    }

    public void setGizmoMemberAccessorNameToAnnotatedElement(
            Map<String, AnnotatedElement> gizmoMemberAccessorNameToAnnotatedElement) {
        this.gizmoMemberAccessorNameToAnnotatedElement = gizmoMemberAccessorNameToAnnotatedElement;
    }
}
