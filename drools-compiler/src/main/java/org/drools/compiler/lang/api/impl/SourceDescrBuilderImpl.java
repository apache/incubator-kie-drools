/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.AccumulateDescrBuilder;
import org.drools.compiler.lang.descr.MVELExprDescr;
import org.drools.compiler.lang.api.CollectDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.api.SourceDescrBuilder;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.WindowReferenceDescr;

/**
 * A descr builder implementation for pattern sources
 */
public class SourceDescrBuilderImpl<P extends PatternDescrBuilder<?>> extends BaseDescrBuilderImpl<P, PatternDescr>
    implements
    SourceDescrBuilder<P> {

    protected SourceDescrBuilderImpl(P parent) {
        super( parent, parent.getDescr() );
    }

    public P expression( String expression ) {
        FromDescr from = new FromDescr();
        from.setDataSource( new MVELExprDescr( expression ) );
        descr.setSource( from );
        return parent;
    }

    public P entryPoint( String entryPoint ) {
        EntryPointDescr ep = new EntryPointDescr( entryPoint );
        descr.setSource( ep );
        return parent;
    }

    public CollectDescrBuilder<P> collect() {
        CollectDescrBuilder<P> collect = new CollectDescrBuilderImpl<P>( parent );
        descr.setSource( collect.getDescr() );
        return collect;
    }

    public AccumulateDescrBuilder<P> accumulate() {
        AccumulateDescrBuilder<P> accumulate = new AccumulateDescrBuilderImpl<P>( parent );
        descr.setSource( accumulate.getDescr() );
        return accumulate;
    }

    public P window( String window ) {
        WindowReferenceDescr wd = new WindowReferenceDescr( window );
        descr.setSource( wd );
        return parent;
    }
}
