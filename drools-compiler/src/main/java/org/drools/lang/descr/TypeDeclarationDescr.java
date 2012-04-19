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
import java.util.List;
import java.util.ArrayList;

public class TypeDeclarationDescr
    extends AbstractClassTypeDeclarationDescr
    implements Comparable<TypeDeclarationDescr> {

    private List<QualifiedName>          superTypes;

    public TypeDeclarationDescr() {
        this( null );
    }

    public TypeDeclarationDescr(final String typeName) {
        super( typeName );
    }

    public TypeDeclarationDescr(final String typeName, final String typeNamespace) {
        super( typeName, typeNamespace );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        this.superTypes = (List<QualifiedName>) in.readObject();
    }
    
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal(out);
        out.writeObject( superTypes );
    }

    public String toString() {
        return "TypeDeclaration[ " + this.getType().getFullName() + " ]";
    }

    public String getSuperTypeName() {
        return superTypes == null ? null : superTypes.get(0).getName();
    }

    public String getSuperTypeNamespace() {
        return superTypes == null ? null : superTypes.get(0).getNamespace();
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
            if ( descr.getTypeName().equals( field.getPattern().getObjectType() ) ) {
                return -1;
            }
        }
        for ( TypeFieldDescr field : descr.getFields().values() ) {
            if ( this.getTypeName().equals( field.getPattern().getObjectType() ) ) {
                return +1;
            }
        }
        return 0;
    }
}
