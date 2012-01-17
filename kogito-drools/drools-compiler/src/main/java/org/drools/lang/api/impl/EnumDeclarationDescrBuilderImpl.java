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

package org.drools.lang.api.impl;

import org.drools.lang.api.AbstractClassTypeDeclarationBuilder;
import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.EnumDeclarationDescrBuilder;
import org.drools.lang.api.EnumLiteralDescrBuilder;
import org.drools.lang.api.FieldDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.descr.EnumDeclarationDescr;

public class EnumDeclarationDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, EnumDeclarationDescr>
    implements
        EnumDeclarationDescrBuilder {

    protected EnumDeclarationDescrBuilderImpl(PackageDescrBuilder parent) {
        super( parent, new EnumDeclarationDescr() );
    }




    public AnnotationDescrBuilder<EnumDeclarationDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<EnumDeclarationDescrBuilder> annotation = new AnnotationDescrBuilderImpl<EnumDeclarationDescrBuilder>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }


    public EnumDeclarationDescrBuilder name( String type ) {
        descr.setTypeName( type );
        return this;
    }

    public FieldDescrBuilder<AbstractClassTypeDeclarationBuilder<EnumDeclarationDescr>> newField( String name ) {
        FieldDescrBuilder<AbstractClassTypeDeclarationBuilder<EnumDeclarationDescr>> field = new FieldDescrBuilderImpl( this, name );
        descr.addField( field.getDescr() );
        return field;
    }

    public EnumLiteralDescrBuilder newEnumLiteral( String lit ) {
        EnumLiteralDescrBuilder literal = new EnumLiteralDescrBuilderImpl( this );
        literal.name( lit );
        descr.addLiteral( literal.getDescr() );
        return literal;
    }

}
