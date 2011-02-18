/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.report.components;

import java.util.Collection;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.LiteralRestriction;

/**
 * 
 * @author Toni Rikkola
 */
public class Gap extends MissingRange
    implements
    Comparable<MissingRange> {

    private final LiteralRestriction restriction;

    public int compareTo(MissingRange another) {
        return super.compareTo( another );
    }

    /**
     * 
     * @param field
     *            Field from where the value is missing.
     * @param evaluator
     *            Evaluator for the missing value.
     * @param cause
     *            The restriction that the gap begins from.
     */
    public Gap(Field field,
               Operator operator,
               LiteralRestriction restriction) {
        super( field,
               operator );

        this.restriction = restriction;
    }

    public String getRuleName() {
        return restriction.getRuleName();
    }

    public LiteralRestriction getRestriction() {
        return restriction;
    }

    public String getValueAsString() {
        return restriction.getValueAsString();
    }

    @Override
    public String toString() {
        return "Gap: (" + field + ") " + getOperator() + " " + getValueAsString() + " from rule: [" + getRuleName() + "]";
    }

    public Collection<Cause> getCauses() {
        return null;
    }
}
