/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.gizmo.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;

public class QuarkusRecordableAnnotatedElement implements AnnotatedElement {

    public List<Annotation> annotationList;

    public QuarkusRecordableAnnotatedElement() {
    }

    public QuarkusRecordableAnnotatedElement(org.jboss.jandex.TypeVariable typeVariable, IndexView indexView) {
        annotationList = getAnnotationListFromAnnotationInstanceList(typeVariable.annotations(), indexView);
    }

    public QuarkusRecordableAnnotatedElement(org.jboss.jandex.UnresolvedTypeVariable typeVariable, IndexView indexView) {
        annotationList = getAnnotationListFromAnnotationInstanceList(typeVariable.annotations(), indexView);
    }

    public QuarkusRecordableAnnotatedElement(FieldInfo fieldInfo, IndexView indexView) {
        annotationList = getAnnotationListFromAnnotationInstanceList(fieldInfo.annotations(), indexView);
    }

    public QuarkusRecordableAnnotatedElement(MethodInfo methodInfo, IndexView indexView) {
        annotationList = getAnnotationListFromAnnotationInstanceList(methodInfo.annotations(), indexView);
    }

    private static List<Annotation> getAnnotationListFromAnnotationInstanceList(
            List<AnnotationInstance> annotationInstanceList, IndexView indexView) {
        return annotationInstanceList.stream()
                .filter(ann -> AllOptaPlannerAnnotationEnum
                        .isOptaPlannerAnnotation(ann.name().toString()))
                .map(m -> QuarkusRecordableAnnotations.getQuarkusRecorderFriendlyAnnotation(m, indexView))
                .collect(Collectors.toList());
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        if (annotationClass == null) {
            return null;
        }
        final String targetClassName = annotationClass.getName();
        return (T) getAnnotationList().stream()
                .filter(annotation -> annotation.annotationType().getName().equals(targetClassName)).findFirst()
                .orElse(null);
    }

    @Override
    public Annotation[] getAnnotations() {
        return getAnnotationList().toArray(new Annotation[0]);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return getAnnotations();
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public void setAnnotationList(List<Annotation> annotationList) {
        this.annotationList = annotationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuarkusRecordableAnnotatedElement that = (QuarkusRecordableAnnotatedElement) o;
        return annotationList.equals(that.annotationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotationList);
    }
}
