/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.dsl.AnnotationDescrBuilder;

public class AnnotationDescrBuilderImpl<P extends DescrBuilder< ? , ? >> extends BaseDescrBuilderImpl<P, AnnotationDescr>
    implements
    AnnotationDescrBuilder<P> {

    protected AnnotationDescrBuilderImpl(P parent,
                                         String name) {
        super( parent,
               new AnnotationDescr( name ) );
    }

    public AnnotationDescrBuilder<P> value( Object value ) {
        descr.setValue( value );
        return this;
    }

    public AnnotationDescrBuilder<P> keyValue( String key,
                                               Object value ) {
        descr.setKeyValue( key,
                           value );
        return this;
    }

    @Override
    public AnnotationDescrBuilder<AnnotationDescrBuilder<P>> newAnnotation( String name ) {
        return new AnnotationDescrBuilderImpl<>( this, name );
    }
}
