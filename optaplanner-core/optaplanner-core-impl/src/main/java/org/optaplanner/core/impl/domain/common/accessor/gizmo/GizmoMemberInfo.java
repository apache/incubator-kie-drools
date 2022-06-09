package org.optaplanner.core.impl.domain.common.accessor.gizmo;

import java.lang.annotation.Annotation;

public final class GizmoMemberInfo {
    private final GizmoMemberDescriptor descriptor;
    private final Class<? extends Annotation> annotationClass;

    public GizmoMemberInfo(GizmoMemberDescriptor descriptor, Class<? extends Annotation> annotationClass) {
        this.descriptor = descriptor;
        this.annotationClass = annotationClass;
    }

    public GizmoMemberDescriptor getDescriptor() {
        return descriptor;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }
}
