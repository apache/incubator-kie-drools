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

import org.drools.lang.descr.EntryPointDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.MVELExprDescr;
import org.drools.lang.descr.PatternDescr;

/**
 * A descr builder implementation for pattern sources
 */
public class SourceDescrBuilderImpl<P extends PatternDescrBuilder<?>> extends BaseDescrBuilderImpl<PatternDescr>
    implements
    SourceDescrBuilder<P> {

    private P parent;

    protected SourceDescrBuilderImpl(P parent) {
        super( parent.getDescr() );
        this.parent = parent;
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

}
