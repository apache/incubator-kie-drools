/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.examination.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.examples.examination.domain.Topic;

/**
 * Calculated during initialization, not modified during score calculation.
 */
public class TopicConflict implements Serializable,
        Comparable<TopicConflict> {

    private static final Comparator<Topic> TOPIC_COMPARATOR = Comparator.comparingLong(Topic::getId);
    private static final Comparator<TopicConflict> COMPARATOR =
            Comparator.comparing(TopicConflict::getLeftTopic, TOPIC_COMPARATOR)
                    .thenComparing(TopicConflict::getRightTopic, TOPIC_COMPARATOR);
    private Topic leftTopic;
    private Topic rightTopic;
    private int studentSize;

    public TopicConflict(Topic leftTopic, Topic rightTopic, int studentSize) {
        this.leftTopic = leftTopic;
        this.rightTopic = rightTopic;
        this.studentSize = studentSize;
    }

    public Topic getLeftTopic() {
        return leftTopic;
    }

    public void setLeftTopic(Topic leftTopic) {
        this.leftTopic = leftTopic;
    }

    public Topic getRightTopic() {
        return rightTopic;
    }

    public void setRightTopic(Topic rightTopic) {
        this.rightTopic = rightTopic;
    }

    public int getStudentSize() {
        return studentSize;
    }

    public void setStudentSize(int studentSize) {
        this.studentSize = studentSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TopicConflict) {
            TopicConflict other = (TopicConflict) o;
            return new EqualsBuilder()
                    .append(leftTopic, other.leftTopic)
                    .append(rightTopic, other.rightTopic)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftTopic)
                .append(rightTopic)
                .toHashCode();
    }

    @Override
    public int compareTo(TopicConflict other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return leftTopic + " & " + rightTopic + " = " + studentSize;
    }
}
