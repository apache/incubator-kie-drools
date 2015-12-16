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
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.ConditionalBranchDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.api.NamedConsequenceDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.api.AnnotationDescrBuilder;
import org.drools.compiler.lang.api.EvalDescrBuilder;
import org.drools.compiler.lang.api.ForallDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.ExistsDescr;

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
        return new CEDescrBuilderImpl<CEDescrBuilder<P, T>, AndDescr>( this, andDescr );
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, OrDescr> or() {
        OrDescr orDescr = new OrDescr();
        ((ConditionalElementDescr) descr).addDescr( orDescr );
        return new CEDescrBuilderImpl<CEDescrBuilder<P, T>, OrDescr>( this, orDescr );
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, NotDescr> not() {
        CEDescrBuilder<CEDescrBuilder<P, T>, NotDescr> not = new CEDescrBuilderImpl<CEDescrBuilder<P, T>, NotDescr>( this, new NotDescr() );
        ((ConditionalElementDescr) descr).addDescr( not.getDescr() );
        return not;
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, ExistsDescr> exists() {
        CEDescrBuilder<CEDescrBuilder<P, T>, ExistsDescr> exists = new CEDescrBuilderImpl<CEDescrBuilder<P, T>, ExistsDescr>( this, new ExistsDescr() );
        ((ConditionalElementDescr) descr).addDescr( exists.getDescr() );
        return exists;
    }

    /**
     * {@inheritDoc}
     */
    public ForallDescrBuilder<CEDescrBuilder<P, T>> forall() {
        ForallDescrBuilder<CEDescrBuilder<P, T>> forall = new ForallDescrBuilderImpl<CEDescrBuilder<P, T>>( this );
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
        PatternDescrBuilder<CEDescrBuilder<P,T>> pdb = pattern("Object[]").isQuery( false );
        
        // create the accumulate builder with this CE as its parent 
        AccumulateDescrBuilder<CEDescrBuilder<P, T>> accumulate = new AccumulateDescrBuilderImpl<CEDescrBuilder<P, T>>(this);
        
        // set the accumulate descriptor as the source of that pattern descr
        pdb.getDescr().setSource( accumulate.getDescr() );
        
        // return the accumulate builder, that has the properly set parent
        return accumulate;
    }

    /**
     * {@inheritDoc}
     */
    public EvalDescrBuilder<CEDescrBuilder<P, T>> eval() {
        EvalDescrBuilder<CEDescrBuilder<P, T>> eval = new EvalDescrBuilderImpl<CEDescrBuilder<P, T>>( this );
        ((ConditionalElementDescr) descr).addDescr( eval.getDescr() );
        return eval;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<CEDescrBuilder<P, T>> pattern( String type ) {
        PatternDescrBuilder<CEDescrBuilder<P, T>> pattern = new PatternDescrBuilderImpl<CEDescrBuilder<P, T>>( this, type );
        ((ConditionalElementDescr) descr).addDescr( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<CEDescrBuilder<P, T>> pattern() {
        PatternDescrBuilder<CEDescrBuilder<P, T>> pattern = new PatternDescrBuilderImpl<CEDescrBuilder<P, T>>( this );
        ((ConditionalElementDescr) descr).addDescr( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public AnnotationDescrBuilder<CEDescrBuilder<P, T>> newAnnotation( String name ) {
        AnnotationDescrBuilder<CEDescrBuilder<P, T>> annotation = new AnnotationDescrBuilderImpl<CEDescrBuilder<P, T>>( this, name );
        descr.addAnnotation(annotation.getDescr());
        return annotation;
    }

    /**
     * {@inheritDoc}
     */
    public NamedConsequenceDescrBuilder<CEDescrBuilder<P, T>> namedConsequence() {
        NamedConsequenceDescrBuilder<CEDescrBuilder<P, T>> namedConsequence = new NamedConsequenceDescrBuilderImpl<CEDescrBuilder<P, T>>( this );
        ((ConditionalElementDescr) descr).addDescr(namedConsequence.getDescr());
        return namedConsequence;
    }

    /**
     * {@inheritDoc}
     */
    public ConditionalBranchDescrBuilder<CEDescrBuilder<P, T>> conditionalBranch() {
        ConditionalBranchDescrBuilder<CEDescrBuilder<P, T>> conditionalBranch = new ConditionalBranchDescrBuilderImpl<CEDescrBuilder<P, T>>( this );
        ((ConditionalElementDescr) descr).addDescr(conditionalBranch.getDescr());
        return conditionalBranch;
    }
}
