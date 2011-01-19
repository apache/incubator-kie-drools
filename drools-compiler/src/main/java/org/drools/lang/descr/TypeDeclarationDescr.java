/*
 * Copyright 2008 Red Hat
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

package org.drools.lang.descr;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.drools.rule.Namespaceable;

public class TypeDeclarationDescr extends BaseDescr
    implements
    Namespaceable {

    private static final long            serialVersionUID = 510l;
    private String                       namespace;
    private String                       typeName;
    private Map<String, AnnotationDescr> annotations;
    private Map<String, TypeFieldDescr>  fields;
    private String                       superTypeName;

    public TypeDeclarationDescr() {
        this( null );
    }

    public TypeDeclarationDescr(final String typeName) {
        this.typeName = typeName;
        this.annotations = new HashMap<String, AnnotationDescr>();
    }

    public void setNamespace( String namespace ) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return this.namespace;
    }

    /**
     * @return the identifier
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName the identifier to set
     */
    public void setTypeName( String typeName ) {
        this.typeName = typeName;
    }

    /**
     * Assigns a new annotation to this type
     * @param annotation
     * @return returns the previous value of this annotation
     */
    public AnnotationDescr addAnnotation( AnnotationDescr annotation ) {
        if ( this.annotations == null ) {
            this.annotations = new HashMap<String, AnnotationDescr>();
        }
        return this.annotations.put( annotation.getName(),
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
        }
        AnnotationDescr annotation = new AnnotationDescr( name,
                                                          value );
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

    /**
    * Returns the set of annotation names for this type
    * @return
    */
    public Set<String> getAnnotationNames() {
        return annotations == null ? null : annotations.keySet();
    }

    /**
    * @return the fields
    */
    @SuppressWarnings("unchecked")
    public Map<String, TypeFieldDescr> getFields() {
        return (Map<String, TypeFieldDescr>) (this.fields != null ? this.fields : Collections.emptyMap());
    }

    /**
     * @param fields the fields to set
     */
    public void setFields( Map<String, TypeFieldDescr> fields ) {
        this.fields = fields;
    }

    public void addField( TypeFieldDescr field ) {
        if ( this.fields == null ) {
            this.fields = new LinkedHashMap<String, TypeFieldDescr>();
        }
        this.fields.put( field.getFieldName(),
                         field );
    }

    public String toString() {
        return "TypeDeclaration[ " + this.getTypeName() + " ]";
    }

    public String getSuperTypeName() {
        return superTypeName;
    }

    public void setSuperTypeName( String type ) {
        this.superTypeName = type;
    }

}
