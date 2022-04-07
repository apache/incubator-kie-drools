/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
