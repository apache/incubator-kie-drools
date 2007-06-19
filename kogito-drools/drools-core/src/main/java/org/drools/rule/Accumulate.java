/*
 * Copyright 2005 JBoss Inc
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

package org.drools.rule;

import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.Accumulator;
import org.drools.spi.Tuple;

/**
 * A class to represent the Accumulate CE
 */
public class Accumulate extends ConditionalElement {

    private static final long serialVersionUID = 4608000398919355806L;

    private Accumulator       accumulator;
    private Pattern           sourcePattern;
    private Pattern           resultPattern;
    private Declaration[]     requiredDeclarations;
    private Declaration[]     innerDeclarations;

    public Accumulate(final Pattern sourcePattern,
                      final Pattern resultPattern) {

        this( sourcePattern,
              resultPattern,
              new Declaration[0],
              new Declaration[0],
              null );
    }

    public Accumulate(final Pattern sourcePattern,
                      final Pattern resultPattern,
                      final Declaration[] requiredDeclarations,
                      final Declaration[] innerDeclarations) {

        this( sourcePattern,
              resultPattern,
              requiredDeclarations,
              innerDeclarations,
              null );
    }

    public Accumulate(final Pattern sourcePattern,
                      final Pattern resultPattern,
                      final Declaration[] requiredDeclarations,
                      final Declaration[] innerDeclarations,
                      final Accumulator accumulator) {

        this.sourcePattern = sourcePattern;
        this.resultPattern = resultPattern;
        this.requiredDeclarations = requiredDeclarations;
        this.innerDeclarations = innerDeclarations;
        this.accumulator = accumulator;
    }

    public Accumulator getAccumulator() {
        return this.accumulator;
    }

    public void setAccumulator(final Accumulator accumulator) {
        this.accumulator = accumulator;
    }

    public Object createContext() {
        return this.accumulator.createContext();
    }
    
    /**
     * Executes the initialization block of code
     * 
     * @param leftTuple tuple causing the rule fire
     * @param declarations previous declarations
     * @param workingMemory
     * @throws Exception
     */
    public void init(final Object context,
                     final Tuple leftTuple,
                     final WorkingMemory workingMemory) {
        try {
            this.accumulator.init( context,
                                   leftTuple,
                                   this.requiredDeclarations,
                                   workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    /**
     * Executes the accumulate (action) code for the given fact handle
     * 
     * @param leftTuple
     * @param handle
     * @param declarations
     * @param innerDeclarations
     * @param workingMemory
     * @throws Exception
     */
    public void accumulate(final Object context,
                           final Tuple leftTuple,
                           final InternalFactHandle handle,
                           final WorkingMemory workingMemory) {
        try {
            this.accumulator.accumulate( context,
                                         leftTuple,
                                         handle,
                                         this.requiredDeclarations,
                                         this.innerDeclarations,
                                         workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    /**
     * Gets the result of the accummulation
     * 
     * @param leftTuple
     * @param declarations
     * @param workingMemory
     * @return
     * @throws Exception
     */
    public Object getResult(final Object context,
                            final Tuple leftTuple,
                            final WorkingMemory workingMemory) {
        try {
            return this.accumulator.getResult( context,
                                               leftTuple,
                                               this.requiredDeclarations,
                                               workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public Object clone() {
        return new Accumulate( this.sourcePattern,
                               this.resultPattern,
                               this.requiredDeclarations,
                               this.innerDeclarations,
                               this.accumulator );
    }

    public Pattern getResultPattern() {
        return this.resultPattern;
    }

    public Pattern getSourcePattern() {
        return this.sourcePattern;
    }

    public Map getInnerDeclarations() {
        return this.sourcePattern.getInnerDeclarations();
    }

    public Map getOuterDeclarations() {
        return this.resultPattern.getOuterDeclarations();
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.sourcePattern.getInnerDeclarations().get( identifier );
    }

}
