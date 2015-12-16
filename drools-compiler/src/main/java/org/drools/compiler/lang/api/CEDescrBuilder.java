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

package org.drools.compiler.lang.api;

import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;

/**
 *  A descriptor builder for Conditional Elements
 */
public interface CEDescrBuilder<P extends DescrBuilder<?, ?>, T extends AnnotatedBaseDescr>
    extends
    AnnotatedDescrBuilder<CEDescrBuilder<P, T>>,
    PatternContainerDescrBuilder<CEDescrBuilder<P, T>, T>,
    DescrBuilder< P, T > {

    /**
     * Creates a set of AND'ed Conditional Elements
     * 
     * @return a descriptor builder for the AND'ed set of CEs
     */
    CEDescrBuilder<CEDescrBuilder<P, T>, AndDescr> and();
    
    /**
     * Creates a set of OR'ed Conditional Elements
     * 
     * @return a descriptor builder for the OR'ed set of CEs
     */
    CEDescrBuilder<CEDescrBuilder<P, T>, OrDescr> or();
    
    /**
     * Creates a set of NOT'ed Conditional Elements
     * 
     * @return a descriptor builder for the NOT'ed set of CEs
     */
    CEDescrBuilder<CEDescrBuilder<P, T>, NotDescr> not();

    /**
     * Creates a set of EXIST'ed Conditional Elements
     * 
     * @return a descriptor builder for the EXIST'ed set of CEs
     */
    CEDescrBuilder<CEDescrBuilder<P, T>, ExistsDescr> exists();
    
    /**
     * Defines a FORALL Conditional Element
     * 
     * @return a descriptor builder for the FORALL CE
     */
    ForallDescrBuilder<CEDescrBuilder<P, T>> forall();
    
    /**
     * Defines a top level ACCUMULATE CE
     * 
     * @return the accumulate descriptor builder
     */
    AccumulateDescrBuilder<CEDescrBuilder<P, T>> accumulate();

    /**
     * Defines an EVAL Conditional Elements
     * 
     * @return a descriptor builder for the EVAL CE
     */
    EvalDescrBuilder<CEDescrBuilder<P, T>> eval();

    /**
     * Defines a Named Consequence Conditional Elements
     *
     * @return a descriptor builder for the Named Consequence CE
     */
    NamedConsequenceDescrBuilder<CEDescrBuilder<P, T>> namedConsequence();

    /**
     * Defines a Conditional Branch Conditional Elements
     *
     * @return a descriptor builder for the Conditional Branch CE
     */
    ConditionalBranchDescrBuilder<CEDescrBuilder<P, T>> conditionalBranch();
}
