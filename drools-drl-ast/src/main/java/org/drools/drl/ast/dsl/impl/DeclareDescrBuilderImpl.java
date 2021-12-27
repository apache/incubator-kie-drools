/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.DeclareDescrBuilder;
import org.drools.drl.ast.dsl.EnumDeclarationDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.TypeDeclarationDescrBuilder;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.dsl.EntryPointDeclarationDescrBuilder;
import org.drools.drl.ast.dsl.WindowDeclarationDescrBuilder;

public class DeclareDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, PackageDescr>
    implements
        DeclareDescrBuilder {

    protected DeclareDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, parent.getDescr() );
    }

    public EntryPointDeclarationDescrBuilder entryPoint() {
        EntryPointDeclarationDescrBuilder epb = new EntryPointDeclarationDescrBuilderImpl( parent);
        descr.addEntryPointDeclaration( epb.getDescr() );
        return epb;
    }

    public TypeDeclarationDescrBuilder type() {
        TypeDeclarationDescrBuilder tddb = new TypeDeclarationDescrBuilderImpl( parent );
        descr.addTypeDeclaration( tddb.getDescr() );
        return tddb;
    }

    public WindowDeclarationDescrBuilder window() {
        WindowDeclarationDescrBuilder wddb = new WindowDeclarationDescrBuilderImpl( parent );
        descr.addWindowDeclaration( wddb.getDescr() );
        return wddb;
	}

    public EnumDeclarationDescrBuilder enumerative() {
        EnumDeclarationDescrBuilder eddb = new EnumDeclarationDescrBuilderImpl( parent );
        descr.addEnumDeclaration( eddb.getDescr() );
        return eddb;
    }


}
