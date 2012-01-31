/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.descr;

import org.drools.rule.Namespaceable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class EnumDeclarationDescr extends AbstractClassTypeDeclarationDescr {

    private List<EnumLiteralDescr>       literals   = Collections.emptyList();


    public EnumDeclarationDescr() {
        this( null );
    }

    public EnumDeclarationDescr(final String typeName) {
        super( typeName );
    }

    public EnumDeclarationDescr(final String typeName, final String typeNamespace) {
        super( typeName, typeNamespace );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal( ObjectInput in ) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        this.literals = (List<EnumLiteralDescr>) in.readObject();
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal(out);
        out.writeObject( literals );
    }






    public String toString() {
        return "EnumDeclaration[ " + this.getType().getFullName() + " ]";
    }




    public List<EnumLiteralDescr> getLiterals() {
        return this.literals;
    }

    /**
     * @param literals the fields to set
     */
    public void setLiterals( List<EnumLiteralDescr> literals ) {
        this.literals = literals;
    }

    public void addLiteral( EnumLiteralDescr lit ) {
        if ( this.literals == Collections.EMPTY_LIST ) {
            this.literals = new ArrayList<EnumLiteralDescr>();
        }
        this.literals.add( lit );
    }


    public String getSuperTypeName() {
        return "Enum";
    }

    public String getSuperTypeNamespace() {
        return "java.lang";
    }

    public String getSupertTypeFullName() {
        return "java.lang.Enum";
    }

    public List<QualifiedName> getSuperTypes() {
        List<QualifiedName> l = new ArrayList<QualifiedName>( 1 );
        l.add( new QualifiedName( "Enum", "java.lang" ) );
        return l;
    }


}
