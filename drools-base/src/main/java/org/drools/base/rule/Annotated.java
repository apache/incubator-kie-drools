/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.rule;

import java.lang.annotation.Annotation;

import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.PropertyReactive;

public interface Annotated {
    boolean hasAnnotation( Class<? extends Annotation> annotationClass );
    <A extends Annotation> A getTypedAnnotation( Class<A> annotationClass );

    static Annotated withFallback(Annotated primary, Annotated fallback) {
        // @PropertyReactive and @ClassReactive are mutually exclusive declarations of the
        // same setting. If primary declares either one, suppress fallback for both so that
        // a DRL @propertyReactive does not get combined with a Java @ClassReactive.
        boolean primaryDeclaresReactivity =
                primary.hasAnnotation(PropertyReactive.class) || primary.hasAnnotation(ClassReactive.class);
        return new Annotated() {
            @Override
            public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
                if (primary.hasAnnotation(annotationClass)) {
                    return true;
                }
                if (primaryDeclaresReactivity
                        && (annotationClass == PropertyReactive.class || annotationClass == ClassReactive.class)) {
                    return false;
                }
                return fallback.hasAnnotation(annotationClass);
            }

            @Override
            public <A extends Annotation> A getTypedAnnotation(Class<A> annotationClass) {
                A annotation = primary.getTypedAnnotation(annotationClass);
                if (annotation != null) {
                    return annotation;
                }
                if (primaryDeclaresReactivity
                        && (annotationClass == PropertyReactive.class || annotationClass == ClassReactive.class)) {
                    return null;
                }
                return fallback.getTypedAnnotation(annotationClass);
            }
        };
    }

    class ClassAdapter implements Annotated {

        private final Class<?> cls;

        public ClassAdapter( Class<?> cls ) {
            this.cls = cls;
        }

        @Override
        public boolean hasAnnotation( Class<? extends Annotation> annotationClass ) {
            return cls.isAnnotationPresent(annotationClass);
        }

        @Override
        public <A extends Annotation> A getTypedAnnotation( Class<A> annotationClass ) {
            return cls.getAnnotation(annotationClass);
        }
    }
}
