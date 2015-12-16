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

import org.drools.compiler.lang.descr.PatternDescr;

/**
 *  A descriptor builder for Pattern sources
 */
public interface SourceDescrBuilder<P extends PatternDescrBuilder<?>>
    extends
    DescrBuilder<P, PatternDescr> {

    /**
     * Defines the pattern source as being an expression result
     * 
     * @param expression the expression
     * 
     * @return parent descriptor builder
     */
    P expression( String expression );

    /**
     * Defines the pattern source as being an entry point
     * 
     * @param entryPoint the entry point identifier
     * 
     * @return parent descriptor builder
     */
    P entryPoint( String entryPoint );

    /**
     * Defines the pattern source as a collection 
     * 
     * @return the collect descriptor builder
     */
    CollectDescrBuilder<P> collect();

    /**
     * Defines the pattern source as being an accumulation
     * 
     * @return the accumulate descriptor builder
     */
    AccumulateDescrBuilder<P> accumulate();

    /**
     * Defines the pattern source as being a declared window
     * 
     * @param window the declared window identifier
     * 
     * @return parent descriptor builder
     */
    P window( String window );

}
