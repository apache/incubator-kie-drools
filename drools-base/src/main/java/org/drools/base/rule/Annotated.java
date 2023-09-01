package org.drools.base.rule;

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
