/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;


/**
 * A base class for all annotated assets
 * 
 */
public class BaseAnnotatedAsset implements AnnotatedElement {

    private Map<Class<?>,Annotation> annotations;
    
    private static final Annotation[] EMPTY_ANN_ARRAY = new Annotation[0];
    
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation( Class<T> annotation ) {
        return (T) annotations.get( annotation );
    }

    public Annotation[] getAnnotations() {
        return annotations.values().toArray(EMPTY_ANN_ARRAY);
    }

    public Annotation[] getDeclaredAnnotations() {
        return annotations.values().toArray(EMPTY_ANN_ARRAY);
    }

    public boolean isAnnotationPresent( Class<? extends Annotation> annotation ) {
        return annotations.containsKey( annotation );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( annotations == null ) ? 0 : annotations.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseAnnotatedAsset other = (BaseAnnotatedAsset) obj;
        if (annotations == null) {
            if (other.annotations != null)
                return false;
        } else if (!annotations.equals( other.annotations ))
            return false;
        return true;
    }
}
