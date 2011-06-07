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

package org.drools.lang.api.impl;

import org.drools.lang.api.BehaviorDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.PatternDescrBuilder;
import org.drools.lang.api.SourceDescrBuilder;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.PatternDescr;

/**
 * A descr builder implementation for Patterns
 */
public class PatternDescrBuilderImpl<P extends DescrBuilder< ?, ? >> extends BaseDescrBuilderImpl<P, PatternDescr>
    implements
    PatternDescrBuilder<P> {

    protected PatternDescrBuilderImpl(P parent) {
        this( parent,
              null );
    }

    protected PatternDescrBuilderImpl(P parent,
                                      String type) {
        super( parent, new PatternDescr( type ) );
        this.parent = parent;
    }

    public PatternDescrBuilder<P> id( String id,
                                      boolean isUnification ) {
        descr.setIdentifier( id );
        descr.setUnification( isUnification );
        return this;
    }

    public PatternDescrBuilder<P> type( String type ) {
        descr.setObjectType( type );
        return this;
    }

    public PatternDescrBuilder<P> isQuery( boolean query ) {
        descr.setQuery( query );
        return this;
    }

    public PatternDescrBuilder<P> constraint( String constraint ) {
        ExprConstraintDescr constr = new ExprConstraintDescr( constraint );
        constr.setType( ExprConstraintDescr.Type.NAMED );
        constr.setPosition( descr.getConstraint().getDescrs().size() );
        descr.addConstraint( constr );
        return this;
    }

    public PatternDescrBuilder<P> constraint( String constraint,
                                              boolean positional ) {
        ExprConstraintDescr constr = new ExprConstraintDescr( constraint );
        constr.setType( positional ? ExprConstraintDescr.Type.POSITIONAL : ExprConstraintDescr.Type.NAMED );
        constr.setPosition( descr.getConstraint().getDescrs().size() );
        descr.addConstraint( constr );
        return this;
    }

    public PatternDescrBuilder<P> bind( String var,
                                        String target,
                                        boolean isUnification ) {
        descr.addConstraint( new BindingDescr( var,
                                               target,
                                               isUnification ) );
        return this;
    }

    public SourceDescrBuilder<PatternDescrBuilder<P>> from() {
        return new SourceDescrBuilderImpl<PatternDescrBuilder<P>>( this );
    }

    public BehaviorDescrBuilder<PatternDescrBuilder<P>> behavior() {
        return new BehaviorDescrBuilderImpl<PatternDescrBuilder<P>>( this );
    }

}
