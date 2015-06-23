/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.AnnotationDescrBuilder;
import org.drools.compiler.lang.api.EntryPointDeclarationDescrBuilder;
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;

public class EntryPointDeclarationDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, EntryPointDeclarationDescr>
    implements
    EntryPointDeclarationDescrBuilder {

    protected EntryPointDeclarationDescrBuilderImpl(PackageDescrBuilder parent) {
        super( parent, new EntryPointDeclarationDescr() );
    }

    public EntryPointDeclarationDescrBuilder entryPointId( String id ) {
        descr.setEntryPointId( id );
        return this;
    }

    public AnnotationDescrBuilder<EntryPointDeclarationDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<EntryPointDeclarationDescrBuilder> annotation = new AnnotationDescrBuilderImpl<EntryPointDeclarationDescrBuilder>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

}
