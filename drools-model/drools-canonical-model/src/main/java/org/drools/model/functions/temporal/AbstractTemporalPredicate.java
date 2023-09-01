/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.functions.temporal;

import org.drools.model.impl.ModelComponent;

public abstract class AbstractTemporalPredicate<T extends AbstractTemporalPredicate> implements TemporalPredicate, ModelComponent {

    protected boolean negated = false;
    protected boolean thisOnRight = false;

    public TemporalPredicate setNegated( boolean negated ) {
        this.negated = negated;
        return this;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public boolean isEqualTo( ModelComponent other ) {
        if (!(other instanceof AbstractTemporalPredicate)) {
            return false;
        }

        AbstractTemporalPredicate tempPred = (( AbstractTemporalPredicate ) other);

        if (negated != tempPred.negated || thisOnRight != tempPred.thisOnRight || getClass() != tempPred.getClass()) {
            return false;
        }

        return isTemporalPredicateEqualTo( (T) tempPred );
    }

    protected abstract boolean isTemporalPredicateEqualTo(T other);

    @Override
    public TemporalPredicate negate() {
        this.negated = !this.negated;
        return this;
    }

    @Override
    public boolean isThisOnRight() {
        return thisOnRight;
    }

    @Override
    public TemporalPredicate thisOnRight() {
        this.thisOnRight = true;
        return this;
    }
}
