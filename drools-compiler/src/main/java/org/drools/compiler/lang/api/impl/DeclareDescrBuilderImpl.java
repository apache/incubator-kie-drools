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

import org.drools.compiler.lang.api.DeclareDescrBuilder;
import org.drools.compiler.lang.api.EnumDeclarationDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.TypeDeclarationDescrBuilder;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.api.EntryPointDeclarationDescrBuilder;
import org.drools.compiler.lang.api.WindowDeclarationDescrBuilder;

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
