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

import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.PatternDescr;

/**
 * A descr builder implementation for Patterns
 *
 */
public class PatternDescrBuilderImpl<P extends DescrBuilder<?>> extends BaseDescrBuilderImpl<PatternDescr>
    implements
    PatternDescrBuilder<P> {

    private P parent;

    protected PatternDescrBuilderImpl(P parent, String type) {
        super( new PatternDescr( type ) );
        this.parent = parent;
    }

    public PatternDescrBuilder<P> label( String id ) {
        descr.setIdentifier( id );
        return this;
    }

    public PatternDescrBuilder<P> constraint( String constraint ) {
        descr.addConstraint( new ExprConstraintDescr( constraint ) );
        return this;
    }

    public PatternDescrBuilder<P> bind( String var,
                                        String target ) {
        return null;
    }

    public SourceDescrBuilder<PatternDescrBuilder<P>> from() {
        return new SourceDescrBuilderImpl<PatternDescrBuilder<P>>( this );
    }

    public P end() {
        return parent;
    }

}
