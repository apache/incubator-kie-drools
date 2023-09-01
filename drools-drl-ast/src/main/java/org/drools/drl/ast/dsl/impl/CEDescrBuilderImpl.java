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

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AnnotatedBaseDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.dsl.AccumulateDescrBuilder;
import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.CEDescrBuilder;
import org.drools.drl.ast.dsl.ConditionalBranchDescrBuilder;
import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.EvalDescrBuilder;
import org.drools.drl.ast.dsl.ForallDescrBuilder;
import org.drools.drl.ast.dsl.GroupByDescrBuilder;
import org.drools.drl.ast.dsl.NamedConsequenceDescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;

/**
 * An implementation for the CEDescrBuilder
 */
public class CEDescrBuilderImpl<P extends DescrBuilder< ? , ? >, T extends AnnotatedBaseDescr> extends BaseDescrBuilderImpl<P, T>
    implements
        CEDescrBuilder<P, T> {

    public CEDescrBuilderImpl(P parent,
                              T descr) {
        super( parent, descr );
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, AndDescr> and() {
        AndDescr andDescr = new AndDescr();
        ((ConditionalElementDescr) descr).addDescr( andDescr );
        return new CEDescrBuilderImpl<>( this, andDescr );
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, OrDescr> or() {
        OrDescr orDescr = new OrDescr();
        ((ConditionalElementDescr) descr).addDescr( orDescr );
        return new CEDescrBuilderImpl<>( this, orDescr );
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, NotDescr> not() {
        CEDescrBuilder<CEDescrBuilder<P, T>, NotDescr> not = new CEDescrBuilderImpl<>( this, new NotDescr() );
        ((ConditionalElementDescr) descr).addDescr( not.getDescr() );
        return not;
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, ExistsDescr> exists() {
        CEDescrBuilder<CEDescrBuilder<P, T>, ExistsDescr> exists = new CEDescrBuilderImpl<>( this, new ExistsDescr() );
        ((ConditionalElementDescr) descr).addDescr( exists.getDescr() );
        return exists;
    }

    /**
     * {@inheritDoc}
     */
    public ForallDescrBuilder<CEDescrBuilder<P, T>> forall() {
        ForallDescrBuilder<CEDescrBuilder<P, T>> forall = new ForallDescrBuilderImpl<>( this );
        ((ConditionalElementDescr) descr).addDescr( forall.getDescr() );
        return forall;
    }

    /**
     * {@inheritDoc}
     */
    public AccumulateDescrBuilder<CEDescrBuilder<P, T>> accumulate() {
        // here we have to do a trick as a top level accumulate is just an accumulate
        // whose result pattern is Object[]

        // create a linked Object[] pattern and set it to query false
        PatternDescrBuilder<CEDescrBuilder<P,T>> pdb = pattern("Object").isQuery( false );

        // create the accumulate builder with this CE as its parent
        AccumulateDescrBuilder<CEDescrBuilder<P, T>> accumulate = new AccumulateDescrBuilderImpl<>(this);

        // set the accumulate descriptor as the source of that pattern descr
        pdb.getDescr().setSource( accumulate.getDescr() );

        // return the accumulate builder, that has the properly set parent
        return accumulate;
    }

    public GroupByDescrBuilder<CEDescrBuilder<P, T>> groupBy() {
        PatternDescrBuilder<CEDescrBuilder<P, T>> pdb = pattern("Object").isQuery( false );
        GroupByDescrBuilder<CEDescrBuilder<P, T>> groupBy = new GroupByDescrBuilderImpl<>(this);
        pdb.getDescr().setSource( groupBy.getDescr() );
        return groupBy;
    }

    /**
     * {@inheritDoc}
     */
    public EvalDescrBuilder<CEDescrBuilder<P, T>> eval() {
        EvalDescrBuilder<CEDescrBuilder<P, T>> eval = new EvalDescrBuilderImpl<>( this );
        ((ConditionalElementDescr) descr).addDescr( eval.getDescr() );
        return eval;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<CEDescrBuilder<P, T>> pattern( String type ) {
        PatternDescrBuilder<CEDescrBuilder<P, T>> pattern = new PatternDescrBuilderImpl<>( this, type );
        ((ConditionalElementDescr) descr).addDescr( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<CEDescrBuilder<P, T>> pattern() {
        PatternDescrBuilder<CEDescrBuilder<P, T>> pattern = new PatternDescrBuilderImpl<>( this );
        ((ConditionalElementDescr) descr).addDescr( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public AnnotationDescrBuilder<CEDescrBuilder<P, T>> newAnnotation( String name ) {
        AnnotationDescrBuilder<CEDescrBuilder<P, T>> annotation = new AnnotationDescrBuilderImpl<>( this, name );
        descr.addAnnotation(annotation.getDescr());
        return annotation;
    }

    /**
     * {@inheritDoc}
     */
    public NamedConsequenceDescrBuilder<CEDescrBuilder<P, T>> namedConsequence() {
        NamedConsequenceDescrBuilder<CEDescrBuilder<P, T>> namedConsequence = new NamedConsequenceDescrBuilderImpl<>( this );
        ((ConditionalElementDescr) descr).addDescr(namedConsequence.getDescr());
        return namedConsequence;
    }

    /**
     * {@inheritDoc}
     */
    public ConditionalBranchDescrBuilder<CEDescrBuilder<P, T>> conditionalBranch() {
        ConditionalBranchDescrBuilder<CEDescrBuilder<P, T>> conditionalBranch = new ConditionalBranchDescrBuilderImpl<>( this );
        ((ConditionalElementDescr) descr).addDescr(conditionalBranch.getDescr());
        return conditionalBranch;
    }
}
