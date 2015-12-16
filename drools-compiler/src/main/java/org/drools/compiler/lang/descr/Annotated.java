/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.compiler.lang.descr;

import java.lang.annotation.Annotation;

public interface Annotated {
    boolean hasAnnotation( Class<? extends Annotation> annotationClass );
    <A extends Annotation> A getTypedAnnotation( Class<A> annotationClass );

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
