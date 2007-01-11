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
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.spi.Accumulator;
import org.drools.spi.Tuple;

/**
 * A class to represent a Accumulate CE
 */
public class Accumulate extends ConditionalElement {

    private static final long serialVersionUID = 4608000398919355806L;

    private Accumulator       accumulator;
    private Column            sourceColumn;
    private Column            resultColumn;
    private Declaration[]     requiredDeclarations;
    private Declaration[]     innerDeclarations;

    public Accumulate(final Column sourceColumn,
                      final Column resultColumn) {

        this( sourceColumn,
              resultColumn,
              new Declaration[0],
              new Declaration[0],
              null );
    }

    public Accumulate(final Column sourceColumn,
                      final Column resultColumn,
                      final Declaration[] requiredDeclarations,
                      final Declaration[] innerDeclarations) {

        this( sourceColumn,
              resultColumn,
              requiredDeclarations,
              innerDeclarations,
              null );
    }

    public Accumulate(final Column sourceColumn,
                      final Column resultColumn,
                      final Declaration[] requiredDeclarations,
                      final Declaration[] innerDeclarations,
                      final Accumulator accumulator) {

        this.sourceColumn = sourceColumn;
        this.resultColumn = resultColumn;
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

    public Object accumulate(final Tuple leftTuple,
                             final List matchingObjects,
                             final WorkingMemory workingMemory) {
        try {
            return this.accumulator.accumulate( leftTuple,
                                                matchingObjects,
                                                this.requiredDeclarations,
                                                this.innerDeclarations,
                                                workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public Object clone() {
        return new Accumulate( this.sourceColumn,
                               this.resultColumn,
                               this.requiredDeclarations,
                               this.innerDeclarations,
                               this.accumulator );
    }

    public Column getResultColumn() {
        return this.resultColumn;
    }

    public Column getSourceColumn() {
        return this.sourceColumn;
    }

    public Map getInnerDeclarations() {
        return this.sourceColumn.getInnerDeclarations();
    }

    public Map getOuterDeclarations() {
        return this.resultColumn.getOuterDeclarations();
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(String identifier) {
        return (Declaration) this.sourceColumn.getInnerDeclarations().get( identifier );
    }

}
