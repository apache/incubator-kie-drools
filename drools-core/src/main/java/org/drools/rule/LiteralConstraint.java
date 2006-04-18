package org.drools.rule;
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



import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.Tuple;

public class LiteralConstraint
    implements
    FieldConstraint {

    private final FieldValue           field;

    private final FieldExtractor       extractor;

    private final Evaluator            evaluator;

    private static final Declaration[] requiredDeclarations = new Declaration[]{};

    public LiteralConstraint(FieldValue field,
                             FieldExtractor extractor,
                             Evaluator evaluator) {
        this.field = field;
        this.extractor = extractor;
        this.evaluator = evaluator;
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public FieldValue getField() {
        return this.field;
    }

    public FieldExtractor getFieldExtractor() {
        return this.extractor;
    }

    /**
     * Literal constraints cannot have required declarations, so always return an empty array.
     * @return
     *      Return an empty <code>Declaration[]</code>
     */
    public Declaration[] getRequiredDeclarations() {
        return LiteralConstraint.requiredDeclarations;
    }

    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        return evaluator.evaluate( this.extractor.getValue( workingMemory.getObject( handle ) ),
                                   this.field.getValue() );
    }

    public boolean equals(Object other) {
        if ( this == other ) {
            return true;
        }
        if ( !(other instanceof LiteralConstraint) ) {
            return false;
        }
        LiteralConstraint lit = (LiteralConstraint) other;

        return this.field.equals( lit.field ) && this.extractor.equals( lit.extractor ) && this.evaluator.equals( lit.evaluator );
    }

    public int hashCode() {
        return (this.field.hashCode() * 17) ^ (this.extractor.hashCode() * 11) ^ (this.evaluator.hashCode());

    }

    public String toString() {
        return "LiteralConstraint { Field(" + this.extractor.getIndex() + ") " + this.evaluator.toString() + " [" + this.field.getValue() + "] }";
    }
};