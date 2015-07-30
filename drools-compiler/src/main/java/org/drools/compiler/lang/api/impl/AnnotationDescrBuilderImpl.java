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

import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.api.AnnotationDescrBuilder;

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
        return new AnnotationDescrBuilderImpl<AnnotationDescrBuilder<P>>( this, name );
    }
}
