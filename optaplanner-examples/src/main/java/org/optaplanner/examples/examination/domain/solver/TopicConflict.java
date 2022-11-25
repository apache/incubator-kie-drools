package org.optaplanner.examples.examination.domain.solver;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.examination.domain.Topic;

/**
 * Calculated during initialization, not modified during score calculation.
 */
public class TopicConflict extends AbstractPersistable implements Comparable<TopicConflict> {

    private static final Comparator<Topic> TOPIC_COMPARATOR = Comparator.comparingLong(Topic::getId);
    private static final Comparator<TopicConflict> COMPARATOR = Comparator
            .comparing(TopicConflict::getLeftTopic, TOPIC_COMPARATOR)
            .thenComparing(TopicConflict::getRightTopic, TOPIC_COMPARATOR);
    private Topic leftTopic;
    private Topic rightTopic;
    private int studentSize;

    public TopicConflict() {
    }

    public TopicConflict(long id, Topic leftTopic, Topic rightTopic, int studentSize) {
        super(id);
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
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TopicConflict other = (TopicConflict) o;
        return Objects.equals(leftTopic, other.leftTopic) &&
                Objects.equals(rightTopic, other.rightTopic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftTopic, rightTopic);
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
