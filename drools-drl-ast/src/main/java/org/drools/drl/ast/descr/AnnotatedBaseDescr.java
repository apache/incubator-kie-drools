package org.drools.drl.ast.descr;

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
public class AnnotatedBaseDescr extends BaseDescr implements Externalizable {

    private Map<String, AnnotationDescr> annotations;
    
    private static final long serialVersionUID = 520l;
    
    public AnnotatedBaseDescr() {
        this.annotations = new HashMap<>();
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
            this.annotations = new HashMap<>();
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
            this.annotations = new HashMap<>();
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

    public boolean hasAnnotation( Class<? extends Annotation> annotationClass ) {
        return getAnnotation(annotationClass) != null;
    }

    public boolean hasAnnotation( String name ) {
        return getAnnotation(name) != null;
    }

    /**
    * Returns the set of annotation names for this type
    * @return
    */
    public Set<String> getAnnotationNames() {
        return annotations == null ? null : annotations.keySet();
    }
    
    public Collection<AnnotationDescr> getAnnotations() {
        return annotations != null ? annotations.values() : Collections.emptySet();
    }

    public void indexByFQN(boolean isStrict) {
        Map<String, AnnotationDescr> fqnAnnotations = new HashMap<>();
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
