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

import java.util.Arrays;
import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Geoffrey De Smet
 */
public abstract class ConstraintOccurrence implements Comparable<ConstraintOccurrence>, Serializable {

    protected String ruleId;
    protected ConstraintType constraintType;
    protected Object[] causes;

    public ConstraintOccurrence(String ruleId, Object... causes) {
        this(ruleId, ConstraintType.NEGATIVE_HARD, causes);
    }

    public ConstraintOccurrence(String ruleId, ConstraintType constraintType, Object... causes) {
        this.ruleId = ruleId;
        this.constraintType = constraintType;
        this.causes = causes;
    }

    public String getRuleId() {
        return ruleId;
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public Object[] getCauses() {
        return causes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ConstraintOccurrence) {
            ConstraintOccurrence other = (ConstraintOccurrence) o;
            return new EqualsBuilder()
                    .append(ruleId, other.ruleId)
                    .append(constraintType, other.constraintType)
                    .append(causes, other.causes)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(ruleId)
                .append(constraintType)
                .append(causes)
                .toHashCode();
    }

    public int compareTo(ConstraintOccurrence other) {
        return new CompareToBuilder()
                .append(ruleId, other.ruleId)
                .append(constraintType, other.constraintType)
                .append(causes, other.causes)
                .toComparison();
    }

    public String toString() {
        return ruleId + "/" + constraintType + ":" + Arrays.toString(causes);
    }

}
