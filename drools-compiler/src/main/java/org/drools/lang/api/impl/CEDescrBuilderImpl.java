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

import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.AccumulateDescrBuilder;
import org.drools.lang.api.CEDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.EvalDescrBuilder;
import org.drools.lang.api.ForallDescrBuilder;
import org.drools.lang.api.PatternDescrBuilder;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AnnotatedBaseDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;

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
        CEDescrBuilder<CEDescrBuilder<P, T>, AndDescr> and = new CEDescrBuilderImpl<CEDescrBuilder<P, T>, AndDescr>( this,
                                                                                                                     andDescr );
        return and;
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, OrDescr> or() {
        OrDescr orDescr = new OrDescr();
        ((ConditionalElementDescr) descr).addDescr( orDescr );
        CEDescrBuilder<CEDescrBuilder<P, T>, OrDescr> or = new CEDescrBuilderImpl<CEDescrBuilder<P, T>, OrDescr>( this,
                                                                                                                  orDescr );
        return or;
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, NotDescr> not() {
        CEDescrBuilder<CEDescrBuilder<P, T>, NotDescr> not = new CEDescrBuilderImpl<CEDescrBuilder<P, T>, NotDescr>( this,
                                                                                                                     new NotDescr() );
        ((ConditionalElementDescr) descr).addDescr( not.getDescr() );
        return not;
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, ExistsDescr> exists() {
        CEDescrBuilder<CEDescrBuilder<P, T>, ExistsDescr> exists = new CEDescrBuilderImpl<CEDescrBuilder<P, T>, ExistsDescr>( this,
                                                                                                                              new ExistsDescr() );
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
        AccumulateDescrBuilder<CEDescrBuilder<P, T>> accumulate = new AccumulateDescrBuilderImpl<CEDescrBuilder<P, T>>(this)
                .multiFunction( true );
        
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
        PatternDescrBuilder<CEDescrBuilder<P, T>> pattern = new PatternDescrBuilderImpl<CEDescrBuilder<P, T>>( this,
                                                                                                               type );
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
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }
}
