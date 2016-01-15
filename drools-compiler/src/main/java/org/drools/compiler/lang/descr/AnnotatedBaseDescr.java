/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;

import org.drools.compiler.rule.builder.util.AnnotationFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is the super type for all pattern AST nodes.
 */
public class AnnotatedBaseDescr extends BaseDescr
    implements
    Annotated, Externalizable {

    private Map<String, AnnotationDescr> annotations;
    
    private static final long serialVersionUID = 520l;
    
    public AnnotatedBaseDescr() {
        this.annotations = new HashMap<String, AnnotationDescr>();
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        this.annotations = (Map<String, AnnotationDescr>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( annotations );
    }
    
    /**
     * Assigns a new annotation to this type
     * @param annotation
     * @return returns the previous value of this annotation
     */
    public AnnotationDescr addAnnotation( AnnotationDescr annotation ) {
        return addAnnotation( annotation.getName(), annotation );
    }

    public AnnotationDescr addQualifiedAnnotation( AnnotationDescr annotation ) {
        return addAnnotation( annotation.getFullyQualifiedName(), annotation );
    }

    protected AnnotationDescr addAnnotation( String name, AnnotationDescr annotation ) {
        if ( this.annotations == null ) {
            this.annotations = new HashMap<String, AnnotationDescr>();
        } else {
            AnnotationDescr existingAnnotation = annotations.get( name );
            if (existingAnnotation != null) {
                existingAnnotation.setDuplicated();
                return existingAnnotation;
            }
        }
        return this.annotations.put( name,
                                     annotation );
    }

    /**
     * Assigns a new annotation to this type with the respective name and value
     * @param name
     * @param value
     * @return returns the previous value of this annotation
     */
    public AnnotationDescr addAnnotation( String name,
                                          String value ) {
        if ( this.annotations == null ) {
            this.annotations = new HashMap<String, AnnotationDescr>();
        } else {
            AnnotationDescr existingAnnotation = annotations.get( name );
            if (existingAnnotation != null) {
                existingAnnotation.setDuplicated();
                return existingAnnotation;
            }
        }
        AnnotationDescr annotation = new AnnotationDescr( name,
                                                          value );
        annotation.setResource(getResource());
        return this.annotations.put( annotation.getName(),
                                     annotation );
    }

    /**
     * Returns the annotation with the given name
     * @param name
     */
    public AnnotationDescr getAnnotation( String name ) {
        return annotations == null ? null : annotations.get( name );
    }

    public AnnotationDescr getAnnotation( Class<? extends Annotation> annotationClass ) {
        return annotations == null ? null : annotations.get(annotationClass.getCanonicalName());
    }

    public <A extends Annotation> A getTypedAnnotation( Class<A> annotationClass ) {
        AnnotationDescr annotationDescr = getAnnotation(annotationClass);
        return annotationDescr == null ? null : (A)AnnotationFactory.buildAnnotation( annotationDescr, annotationClass );
    }

    public boolean hasAnnotation( Class<? extends Annotation> annotationClass ) {
        return getAnnotation(annotationClass) != null;
    }

    /**
    * Returns the set of annotation names for this type
    * @return
    */
    public Set<String> getAnnotationNames() {
        return annotations == null ? null : annotations.keySet();
    }
    
    public Collection<AnnotationDescr> getAnnotations() {
        return annotations != null ? annotations.values() : Collections.<AnnotationDescr>emptySet();
    }

    public void indexByFQN(boolean isStrict) {
        Map<String, AnnotationDescr> fqnAnnotations = new HashMap<String, AnnotationDescr>();
        for (AnnotationDescr annotationDescr : annotations.values()) {
            if (annotationDescr.getFullyQualifiedName() != null) {
                fqnAnnotations.put(annotationDescr.getFullyQualifiedName(), annotationDescr);
            } else if (!isStrict) {
                fqnAnnotations.put(annotationDescr.getName(), annotationDescr);
            }
        }
        annotations = fqnAnnotations;
    }
}
