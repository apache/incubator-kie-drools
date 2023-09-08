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

import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.BehaviorDescrBuilder;
import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.dsl.SourceDescrBuilder;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.PatternDescr;

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
        constr.setResource(descr.getResource());
        descr.addConstraint( constr );
        return this;
    }

    public PatternDescrBuilder<P> constraint( String constraint,
                                              boolean positional ) {
        ExprConstraintDescr constr = new ExprConstraintDescr( constraint );
        constr.setType( positional ? ExprConstraintDescr.Type.POSITIONAL : ExprConstraintDescr.Type.NAMED );
        constr.setPosition( descr.getConstraint().getDescrs().size() );
        constr.setResource(descr.getResource());
        descr.addConstraint( constr );
        return this;
    }

    public PatternDescrBuilder<P> bind( String var,
                                        String target,
                                        boolean isUnification ) {
        BindingDescr bindDescr = new BindingDescr( var,
                                                   target,
                                                   isUnification );
        bindDescr.setResource(descr.getResource());
        descr.addConstraint( bindDescr );
        return this;
    }

    public SourceDescrBuilder<PatternDescrBuilder<P>> from() {
        return new SourceDescrBuilderImpl<>( this );
    }

    public BehaviorDescrBuilder<PatternDescrBuilder<P>> behavior() {
        return new BehaviorDescrBuilderImpl<>( this );
    }

    public AnnotationDescrBuilder<PatternDescrBuilder<P>> newAnnotation(String name) {
        AnnotationDescrBuilder<PatternDescrBuilder<P>> annotation = new AnnotationDescrBuilderImpl<>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }
}
