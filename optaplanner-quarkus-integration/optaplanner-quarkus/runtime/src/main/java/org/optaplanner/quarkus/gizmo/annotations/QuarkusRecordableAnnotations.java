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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;

/*
 * When bytecode is being recorded in Quarkus (i.e. an
 * object is passed to a recorder), the following restrictions apply:
 *
 * - Classes without public getters/setters for all fields,
 * a constructor annotated with @RecordableConstructor with parameter names that match field names or a
 * registered substitution cannot be recorded. (https://quarkus.io/guides/writing-extensions#bytecode-recording)
 *
 * Annotations and Generic Types do not satisfy the above criteria, so they cannot be
 * recorded directly at build time. However, we don't want to use reflection at runtime,
 * and we want to build the GizmoMemberAccessors at build time. So we need to create
 * wrappers for the annotations and types so they can be recorded.
 */
public class QuarkusRecordableAnnotations {

    public static Annotation getQuarkusRecorderFriendlyAnnotation(AnnotationInstance annotationInstance, IndexView indexView) {
        Class<? extends Annotation> annotationType;
        final Map<String, Object> annotationValues = new HashMap<String, Object>();
        try {
            annotationType = (Class<? extends Annotation>) Class.forName(annotationInstance.name().toString());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        if (AllOptaPlannerAnnotationEnum.isOptaPlannerAnnotation(annotationType)) {
            // Annotation is an OptaPlanner annotation (i.e.
            // is a subpackage of org.optaplanner)
            annotationInstance.valuesWithDefaults(indexView)
                    .forEach((value) -> annotationValues.put(value.name(),
                            getJavaObjectForJandexAnnotationValue(value, annotationInstance, indexView)));
            return AllOptaPlannerAnnotationEnum.getForClass(annotationType, annotationValues);
        } else {
            throw new IllegalStateException("The annotation (" + annotationType + ") is not an OptaPlanner annotation");
        }
    }

    // Note: AnnotationValue.value() cannot be used; returns a Jandex type for Class (we need Class<?>),
    // Jandex AnnotationInstance for nested (We need an actual Annotation instance of the annotation class)
    public static Object getJavaObjectForJandexAnnotationValue(AnnotationValue value, AnnotationInstance annotationInstance,
            IndexView indexView) {
        switch (value.kind()) {
            case BOOLEAN:
                return value.asBoolean();
            case BYTE:
                return value.asByte();
            case CHARACTER:
                return value.asChar();
            case SHORT:
                return value.asShort();
            case INTEGER:
                return value.asInt();
            case LONG:
                return value.asLong();
            case FLOAT:
                return value.asFloat();
            case DOUBLE:
                return value.asDouble();
            case STRING:
                return value.asString();
            case ENUM:
                return value.asEnum();
            case CLASS:
                return findClass(annotationInstance, value.asClass());
            case NESTED:
                return getQuarkusRecorderFriendlyAnnotation(value.asNested(), indexView);
            case ARRAY:
                switch (value.componentKind()) {
                    case BOOLEAN:
                        return value.asBooleanArray();
                    case BYTE:
                        return value.asByteArray();
                    case CHARACTER:
                        return value.asCharArray();
                    case SHORT:
                        return value.asShortArray();
                    case INTEGER:
                        return value.asIntArray();
                    case LONG:
                        return value.asLongArray();
                    case FLOAT:
                        return value.asFloatArray();
                    case DOUBLE:
                        return value.asDoubleArray();
                    case STRING:
                        return value.asStringArray();
                    case ENUM:
                        return value.asEnumArray();
                    case CLASS:
                        return Arrays.stream(value.asClassArray())
                                .map(v -> findClass(annotationInstance, v))
                                .toArray(Class[]::new);
                    case NESTED:
                        return Arrays.stream(value.asNestedArray())
                                .map(v -> getQuarkusRecorderFriendlyAnnotation(v, indexView))
                                .toArray(Annotation[]::new);
                    case UNKNOWN:
                        // Note: If an array is empty, it is unknown, but
                        // Jandex doesn't provide a way to check array length
                        // According to Jandex javadoc:
                        //
                        // A special AnnotationValue.Kind.UNKNOWN kind is used to refer to
                        // components of zero-length arrays, as the underlying type is not known.
                        //
                        // So it safe to use an empty Object array
                        return new Object[] {};
                    case ARRAY:
                    default:
                        throw new IllegalStateException("Arrays of " + value.componentKind() +
                                " are unsupported in the enum parser." +
                                " This exception was caused by a" +
                                " @" + annotationInstance.name() + " annotation" +
                                " for parameter " + value.name() + "." +
                                " Maybe put " + annotationInstance.name() +
                                " not in a subpackage of org.optaplanner?");
                }
            case UNKNOWN:
            default:
                throw new IllegalStateException(value.componentKind() +
                        " are unsupported in the enum parser." +
                        " This exception was caused by a" +
                        " @" + annotationInstance.name() + " annotation" +
                        " for parameter " + value.name() + "." +
                        " Maybe put " + annotationInstance.name() +
                        " not in a subpackage of org.optaplanner?");
        }
    }

    private static Class<?> findClass(AnnotationInstance annotationInstance, Type type) {
        try {
            return Class.forName(type.toString(), false,
                    Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not find class (" + type.toString() +
                    ") referenced by annotation (" +
                    annotationInstance.toString() +
                    "). Maybe check your classpath?", e);
        }
    }

    private QuarkusRecordableAnnotations() {

    }
}
