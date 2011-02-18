/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.api;

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.QueryDescr;

/**
 * A descr builder for queries
 */
public class QueryDescrBuilderImpl extends BaseDescrBuilderImpl<QueryDescr>
    implements
    QueryDescrBuilder {

    protected QueryDescrBuilderImpl() {
        super( new QueryDescr() );
    }


    public AnnotationDescrBuilder newAnnotation( String name ) {
        AnnotationDescrBuilder annotation = new AnnotationDescrBuilderImpl( name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public QueryDescrBuilder name( String name ) {
        descr.setName( name );
        return this;
    }

    public CEDescrBuilder<QueryDescrBuilder, AndDescr> lhs() {
        CEDescrBuilder<QueryDescrBuilder, AndDescr> ce = new CEDescrBuilderImpl<QueryDescrBuilder, AndDescr>( this, new AndDescr() );
        descr.setLhs( ce.getDescr() );
        return ce;
    }


    public QueryDescrBuilder parameter( String type,
                                        String variable ) {
        descr.addParameter( type,
                            variable );
        return this;
    }


}
