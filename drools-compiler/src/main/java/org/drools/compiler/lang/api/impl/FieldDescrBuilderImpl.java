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

import org.drools.compiler.lang.api.AnnotationDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.api.FieldDescrBuilder;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;

public class FieldDescrBuilderImpl<T extends DescrBuilder<?,?>> extends BaseDescrBuilderImpl<T, TypeFieldDescr>
    implements
        FieldDescrBuilder<T> {

    protected FieldDescrBuilderImpl( T parent, String name ) {
        super( parent, new TypeFieldDescr( name ) );
    }

    public AnnotationDescrBuilder<FieldDescrBuilder<T>> newAnnotation( String name ) {
        AnnotationDescrBuilder<FieldDescrBuilder<T>> annotation = new AnnotationDescrBuilderImpl<FieldDescrBuilder<T>>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public FieldDescrBuilder index( int index ) {
        descr.setIndex( index );
        return this;
    }

    public FieldDescrBuilder name( String name ) {
        descr.setFieldName( name );
        return this;
    }

    public FieldDescrBuilder type( String type ) {
        descr.setPattern( new PatternDescr( type ) );
        return this;
    }

    public FieldDescrBuilder initialValue( String value ) {
        descr.setInitExpr( value );
        return this;
    }
}
