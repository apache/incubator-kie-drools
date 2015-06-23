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
import org.drools.compiler.lang.api.FunctionDescrBuilder;
import org.drools.compiler.lang.descr.FunctionDescr;

public class FunctionDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, FunctionDescr>
    implements
    FunctionDescrBuilder {

    protected FunctionDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, new FunctionDescr() );
    }

    public FunctionDescrBuilder namespace( String namespace ) {
        descr.setNamespace( namespace );
        return this;
    }

    public FunctionDescrBuilder returnType( String type ) {
        descr.setReturnType( type );
        return this;
    }

    public FunctionDescrBuilder name( String name ) {
        descr.setName( name );
        return this;
    }

    public FunctionDescrBuilder body( String body ) {
        descr.setBody( body );
        return this;
    }

    public FunctionDescrBuilder parameter( String type,
                                           String variable ) {
        descr.addParameter( type,
                            variable );
        return this;
    }

    public FunctionDescrBuilder dialect( String dialect ) {
        descr.setDialect( dialect );
        return this;
    }
}
