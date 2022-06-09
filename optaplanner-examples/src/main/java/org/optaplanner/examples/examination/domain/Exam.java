package org.optaplanner.examples.examination.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.examination.domain.solver.ExamDifficultyWeightFactory;
import org.optaplanner.examples.examination.domain.solver.RoomStrengthWeightFactory;

import com.thoughtworks.xstream.annotations.XStreamInclude;

@PlanningEntity(difficultyWeightFactoryClass = ExamDifficultyWeightFactory.class)
@XStreamInclude({
        LeadingExam.class,
        FollowingExam.class
})
public abstract class Exam extends AbstractPersistable {

    protected Topic topic;

    // Planning variables: changes during planning, between score calculations.
    protected Room room;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @PlanningVariable(valueRangeProviderRefs = { "roomRange" }, strengthWeightFactoryClass = RoomStrengthWeightFactory.class)
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public abstract Period getPeriod();

    public int getTopicDuration() {
        return getTopic().getDuration();
    }

    public int getTopicStudentSize() {
        return getTopic().getStudentSize();
    }

    public int getDayIndex() {
        Period period = getPeriod();
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getDayIndex();
    }

    public int getPeriodIndex() {
        Period period = getPeriod();
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getPeriodIndex();
    }

    public int getPeriodDuration() {
        Period period = getPeriod();
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getDuration();
    }

    public boolean isTopicFrontLoadLarge() {
        return topic.isFrontLoadLarge();
    }

    public boolean isPeriodFrontLoadLast() {
        Period period = getPeriod();
        if (period == null) {
            return false;
        }
        return period.isFrontLoadLast();
    }

    public String getLabel() {
        return Long.toString(topic.getId());
    }

    @Override
    public String toString() {
        return topic.toString();
    }

}
