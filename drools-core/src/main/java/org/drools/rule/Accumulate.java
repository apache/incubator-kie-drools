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

import java.util.List;

import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.Accumulator;

/**
 * A class to represent a Accumulate CE
 */
public class Accumulate extends ConditionalElement {

    private static final long serialVersionUID = 4608000398919355806L;

    private Accumulator       accumulator;
    private Column            sourceColumn;
    private Column            resultColumn;
    private Declaration[]     requiredDeclarations;

    public Accumulate(final Column sourceColumn,
                      final Column resultColumn) {

        this( sourceColumn,
              resultColumn,
              new Declaration[0],
              null);
    }

    public Accumulate(final Column sourceColumn,
                      final Column resultColumn,
                      final Declaration[] requiredDeclarations) {

        this( sourceColumn,
              resultColumn,
              requiredDeclarations,
              null );
    }

    public Accumulate(final Column sourceColumn,
                      final Column resultColumn,
                      final Declaration[] requiredDeclarations,
                      final Accumulator accumulator) {

        this.sourceColumn = sourceColumn;
        this.resultColumn = resultColumn;
        this.requiredDeclarations = requiredDeclarations;
        this.accumulator = accumulator;
    }

    public Accumulator getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(Accumulator accumulator) {
        this.accumulator = accumulator;
    }

    public Object accumulate(final ReteTuple leftTuple,
                             final List matchingObjects,
                             final WorkingMemory workingMemory) {
        try {
            return this.accumulator.accumulate( leftTuple,
                                                matchingObjects,
                                                this.requiredDeclarations,
                                                workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public Object clone() {
        return new Accumulate( this.sourceColumn,
                               this.resultColumn,
                               this.requiredDeclarations,
                               this.accumulator );
    }

    public Column getResultColumn() {
        return this.resultColumn;
    }

    public Column getSourceColumn() {
        return this.sourceColumn;
    }

}
