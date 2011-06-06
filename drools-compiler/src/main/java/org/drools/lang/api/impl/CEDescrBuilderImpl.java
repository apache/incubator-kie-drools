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

import org.drools.lang.api.CEDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.EvalDescrBuilder;
import org.drools.lang.api.ForallDescrBuilder;
import org.drools.lang.api.PatternDescrBuilder;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;

/**
 * An implementation for the CEDescrBuilder
 */
public class CEDescrBuilderImpl<P extends DescrBuilder<?>, T extends BaseDescr> extends BaseDescrBuilderImpl<T>
    implements
    CEDescrBuilder<P, T> {

    private P parent;

    public CEDescrBuilderImpl(P parent, T descr) {
        super( descr );
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, AndDescr> and() {
        AndDescr andDescr = null;
//        if( descr instanceof AndDescr ) {
//            andDescr = (AndDescr) descr;
//        } else {
            andDescr = new AndDescr();
            ((ConditionalElementDescr) descr).addDescr( andDescr );
//        }
        CEDescrBuilder<CEDescrBuilder<P, T>, AndDescr> and = new CEDescrBuilderImpl<CEDescrBuilder<P, T>, AndDescr>( this, andDescr );
        return and;
    }

    /**
     * {@inheritDoc}
     */
    public CEDescrBuilder<CEDescrBuilder<P, T>, OrDescr> or() {
        OrDescr orDescr = null;
//        if( descr instanceof OrDescr ) {
//            orDescr = (OrDescr) descr;
//        } else {
            orDescr = new OrDescr();
            ((ConditionalElementDescr) descr).addDescr( orDescr );
//        }
        CEDescrBuilder<CEDescrBuilder<P, T>, OrDescr> or = new CEDescrBuilderImpl<CEDescrBuilder<P, T>, OrDescr>( this, orDescr );
        return or;
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
    public EvalDescrBuilder<CEDescrBuilder<P, T>> eval() {
        EvalDescrBuilder<CEDescrBuilder<P, T>> eval = new EvalDescrBuilderImpl<CEDescrBuilder<P,T>>();
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

    public P end() {
        return parent;
    }

}
