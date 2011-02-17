/**
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

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;

/**
 * An implementation for the CollectDescrBuilder
 */
public class AccumulateDescrBuilderImpl<P extends DescrBuilder< ? >> extends BaseDescrBuilderImpl<AccumulateDescr>
    implements
    AccumulateDescrBuilder<P> {

    private P parent;

    public AccumulateDescrBuilderImpl(P parent) {
        super( new AccumulateDescr() );
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<AccumulateDescrBuilder<P>> pattern( String type ) {
        PatternDescrBuilder<AccumulateDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<AccumulateDescrBuilder<P>>( this,
                                                                                                                         type );
        descr.setInputPattern( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<AccumulateDescrBuilder<P>> pattern() {
        PatternDescrBuilder<AccumulateDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<AccumulateDescrBuilder<P>>( this );
        descr.setInputPattern( pattern.getDescr() );
        return pattern;
    }

    public P end() {
        return parent;
    }

    public CEDescrBuilder<AccumulateDescrBuilder<P>, AndDescr > source() {
        CEDescrBuilder<AccumulateDescrBuilder<P>, AndDescr> and = new CEDescrBuilderImpl<AccumulateDescrBuilder<P>, AndDescr>( this, new AndDescr() );
        descr.setInput( and.getDescr() );
        return and;
    }

    public AccumulateDescrBuilder<P> function( String name,
                                               String[] parameters ) {
        descr.addFunction( name, parameters );
        return this;
    }

    public AccumulateDescrBuilder<P> init( String block ) {
        descr.setInitCode( block );
        return this;
    }

    public AccumulateDescrBuilder<P> action( String block ) {
        descr.setActionCode( block );
        return this;
    }

    public AccumulateDescrBuilder<P> reverse( String block ) {
        descr.setReverseCode( block );
        return this;
    }

    public AccumulateDescrBuilder<P> result( String expr ) {
        descr.setResultCode( expr );
        return this;
    }

}
