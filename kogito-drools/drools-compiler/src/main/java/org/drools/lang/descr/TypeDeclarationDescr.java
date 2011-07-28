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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.drools.rule.Namespaceable;
 
public class TypeDeclarationDescr extends AnnotatedBaseDescr
    implements
    Namespaceable, Comparable<TypeDeclarationDescr> {

    private static final long            serialVersionUID = 510l;
    private String                       namespace;
    private String                       typeName;
    private Map<String, TypeFieldDescr>  fields;
    private String                       superTypeName;
    private String                       superTypeNamespace;

    public TypeDeclarationDescr() {
        this( null );
    }

    public TypeDeclarationDescr(final String typeName) {
        this.typeName = typeName;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        this.namespace = (String) in.readObject();
        this.typeName = (String) in.readObject();
        this.superTypeName = (String) in.readObject();
        this.fields = (Map<String, TypeFieldDescr>) in.readObject();
    }
    
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( namespace );
        out.writeObject( typeName );
        out.writeObject( superTypeName );
        out.writeObject( fields );
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


    public String getSuperTypeNamespace() {
        return superTypeNamespace;
    }

    public void setSuperTypeNamespace(String superTypeNamespace) {
        this.superTypeNamespace = superTypeNamespace;
    }

    public int compareTo(TypeDeclarationDescr descr) {
        int result = 0;
        if (this.getSuperTypeName() == null && descr.getSuperTypeName() == null) result = 0;
        else if (this.getSuperTypeName() != null && this.getSuperTypeName().equals(descr.getTypeName())) result = -1;
        else if (descr.getSuperTypeName() != null && descr.getSuperTypeName().equals(this.getTypeName())) result = 1;
        System.err.println("TypeDeclaration Descr compareTo : Compare descr "+ this.getTypeName() + " vs " + descr.getTypeName() + " >>  " + result);
        return result;
    }

}
