/**
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

package org.drools.planner.core.score.constraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Geoffrey De Smet
 */
public class IntConstraintOccurrence extends ConstraintOccurrence {

    protected int weight;

    public IntConstraintOccurrence(String ruleId, Object... causes) {
        this(ruleId, ConstraintType.NEGATIVE_HARD, causes);
    }

    public IntConstraintOccurrence(String ruleId, ConstraintType constraintType, Object... causes) {
        this(ruleId, constraintType, 1, causes);
    }

    public IntConstraintOccurrence(String ruleId, int weight, Object... causes) {
        this(ruleId, ConstraintType.NEGATIVE_HARD, weight, causes);
    }

    public IntConstraintOccurrence(String ruleId, ConstraintType constraintType, int weight, Object... causes) {
        super(ruleId, constraintType, causes);
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof IntConstraintOccurrence) {
            IntConstraintOccurrence other = (IntConstraintOccurrence) o;
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
