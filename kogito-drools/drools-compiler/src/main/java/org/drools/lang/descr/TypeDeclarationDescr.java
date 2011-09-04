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
import java.util.List;
import java.util.ArrayList;

import org.drools.core.util.StringUtils;
import org.drools.rule.Namespaceable;
 
public class TypeDeclarationDescr extends AnnotatedBaseDescr
    implements
    Namespaceable, Comparable<TypeDeclarationDescr> {

    private static final long            serialVersionUID = 510l;
    private QualifiedName                type;
    private Map<String, TypeFieldDescr>  fields;
    private List<QualifiedName>          superTypes;


    public TypeDeclarationDescr() {
        this( null );
    }

    public TypeDeclarationDescr(final String typeName) {
        this.type = new QualifiedName( typeName, null );
    }

    public TypeDeclarationDescr(final String typeName, final String typeNamespace ) {
        this.type = new QualifiedName( typeName, typeNamespace );
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        this.type = (QualifiedName) in.readObject();
        this.superTypes = (List<QualifiedName>) in.readObject();
        this.fields = (Map<String, TypeFieldDescr>) in.readObject();
    }
    
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal(out);
        out.writeObject( type );
        out.writeObject( superTypes );
        out.writeObject( fields );
    }

    public void setNamespace( String namespace ) {
        this.type.setNamespace(namespace);
    }

    public String getNamespace() {
        return this.type.getNamespace();
    }

    /**
     * @return the identifier
     */
    public String getTypeName() {
        return this.type.getName();
    }

    /**
     * @param typeName the identifier to set
     */
    public void setTypeName( String typeName ) {
        this.type.setName( typeName );
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
        return "TypeDeclaration[ " + this.getType().getFullName() + " ]";
    }





    public List<QualifiedName> getSuperTypes() {
        return superTypes != null ? superTypes : Collections.<QualifiedName>emptyList();
    }

    public void addSuperType( String type ) {
        addSuperType( new QualifiedName( type ) );
    }

    public void addSuperType( QualifiedName type ) {
        if ( superTypes == null ) {
            superTypes = new ArrayList<QualifiedName>();
        }
        this.superTypes.add( type );
    }





    public int compareTo(TypeDeclarationDescr descr) {
        System.out.println("Comp");

        if ( ! this.getSuperTypes().isEmpty() && ! descr.getSuperTypes().isEmpty() ) {
            for ( QualifiedName q : descr.getSuperTypes() ) {
                if ( this.getSuperTypes().contains( q ) ) {
                    return -1;
                }
            }
            for ( QualifiedName q : this.getSuperTypes() ) {
                if ( descr.getSuperTypes().contains( q ) ) {
                    return +1;
                }
            }
        }
        for ( TypeFieldDescr field : this.getFields().values() ) {
            if ( descr.getType().equals( field.getPattern().getObjectType() ) ) {
                return -1;
            }
        }
        for ( TypeFieldDescr field : descr.getFields().values() ) {
            if ( this.getType().equals( field.getPattern().getObjectType() ) ) {
                return +1;
            }
        }
        return 0;
    }



    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeDeclarationDescr that = (TypeDeclarationDescr) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }


    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }




    public static class QualifiedName {

        private String name;
        private String namespace;

        public QualifiedName(String name) {
            int pos = name.lastIndexOf( '.' );
            if ( pos < 0 ) {
                this.name = name;
                this.namespace = "";
            } else {
                this.name = name.substring( pos + 1 );
                this.namespace = name.substring( 0, pos );
            }
        }

        public QualifiedName(String name, String namespace) {
            this.name = name;
            this.namespace = namespace;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QualifiedName that = (QualifiedName) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;

            return true;
        }

        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
            return result;
        }

        public String getFullName() {
            if ( StringUtils.isEmpty( namespace ) ) {
                return name;
            } else {
                return namespace + "." + name;
            }
        }

        public String toString() {
            return getFullName();
        }
    }

}
