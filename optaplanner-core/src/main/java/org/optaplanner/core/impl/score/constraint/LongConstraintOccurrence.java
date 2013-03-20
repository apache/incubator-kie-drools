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

package org.optaplanner.core.impl.score.constraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class LongConstraintOccurrence extends ConstraintOccurrence {

    protected long weight;

    public LongConstraintOccurrence(String ruleId, Object... causes) {
        this(ruleId, ConstraintType.HARD, causes);
    }

    public LongConstraintOccurrence(String ruleId, ConstraintType constraintType, Object... causes) {
        this(ruleId, constraintType, 1, causes);
    }

    public LongConstraintOccurrence(String ruleId, long weight, Object... causes) {
        this(ruleId, ConstraintType.HARD, weight, causes);
    }

    public LongConstraintOccurrence(String ruleId, ConstraintType constraintType, long weight, Object... causes) {
        super(ruleId, constraintType, causes);
        this.weight = weight;
    }

    public long getWeight() {
        return weight;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof LongConstraintOccurrence) {
            LongConstraintOccurrence other = (LongConstraintOccurrence) o;
            return new EqualsBuilder()
                    .appendSuper(super.equals(other))
                    .append(weight, other.weight)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(weight)
                .toHashCode();
    }

    @Override
    public String toString() {
        return super.toString() + "=" + weight;
    }

}
