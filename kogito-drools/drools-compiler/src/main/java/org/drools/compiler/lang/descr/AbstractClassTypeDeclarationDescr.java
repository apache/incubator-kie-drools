/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.lang.descr;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.rule.Namespaceable;
import org.kie.api.io.Resource;


public abstract class AbstractClassTypeDeclarationDescr extends AnnotatedBaseDescr
        implements
        Namespaceable {

    private static final long            serialVersionUID = 510l;
    private QualifiedName                type;
    private Map<String, TypeFieldDescr>  fields;

    public AbstractClassTypeDeclarationDescr( String name ) {
        this.type = new QualifiedName( name, null );
    }

    public AbstractClassTypeDeclarationDescr(final String typeName, final String typeNamespace) {
        this.type = new QualifiedName( typeName, typeNamespace );
    }


    @SuppressWarnings("unchecked")
    @Override
    public void readExternal( ObjectInput in ) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        type = (QualifiedName) in.readObject();
        fields =  (Map<String, TypeFieldDescr>) in.readObject();
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( type );
        out.writeObject( fields );
    }

    public void setNamespace( String namespace ) {
        super.setNamespace( namespace );
        this.type.setNamespace( namespace );
    }

    /**
     * @return the identifier
     */
    public String getTypeName() {
        return this.type.getName();
    }

    public String getFullTypeName() {
        return this.type.getFullName();
    }

    /**
     * @param typeName the identifier to set
     */
    public void setTypeName( String typeName ) {
        if ( typeName.indexOf( '.' ) < 0 ) {
            this.type.setName( typeName );
        } else {
            this.setNamespace( typeName.substring( 0, typeName.lastIndexOf( '.' ) ) );
            this.type.setName( typeName.substring( typeName.lastIndexOf( '.' ) + 1 ) );
        }
    }

    public QualifiedName getType() {
        return type;
    }

    public void setType( QualifiedName qname ) {
        type = qname;
    }

    public void setType( String name, String namespace ) {
        type = new QualifiedName( name, namespace );
    }


    public String getSuperTypeName() {
        return "Object";
    }

    public String getSuperTypeNamespace() {
        return "java.lang";
    }

    public String getSupertTypeFullName() {
        return "java.lang.Object";
    }

    public List<QualifiedName> getSuperTypes() {
        List<QualifiedName> l = new ArrayList<QualifiedName>( 1 );
        l.add( new QualifiedName( "Object", "java.lang" ) );
        return l;
    }

    /**
     * @return the fields
     */
    @SuppressWarnings("unchecked")
    public Map<String, TypeFieldDescr> getFields() {
        if (fields == null) {
            fields = new LinkedHashMap<>();
        }
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields( Map<String, TypeFieldDescr> fields ) {
        this.fields = fields;
    }

    public void addField( TypeFieldDescr field ) {
        // Setting the resource on the field does not seem to be necessary (because it's always already been set)
        // but I'm leaving in this just to be safe..
        field.setResource(getResource());
        getFields().put( field.getFieldName(), field );
    }

    @Override
    public void setResource(Resource resource) {
        super.setResource(resource);
        if (fields != null) {
            for (TypeFieldDescr field : fields.values()) {
                field.setResource(resource);
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractClassTypeDeclarationDescr that = (AbstractClassTypeDeclarationDescr) o;

        return !(getType() != null ? !getType().equals(that.getType()) : that.getType() != null);
    }

    public int hashCode() {
        return getType()  != null ? getType() .hashCode() : 0;
    }
}
