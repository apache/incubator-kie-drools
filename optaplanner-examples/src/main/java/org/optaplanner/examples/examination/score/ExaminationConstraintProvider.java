package org.optaplanner.examples.examination.score;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThanOrEqual;

import java.util.function.Function;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.ExaminationConstraintConfiguration;
import org.optaplanner.examples.examination.domain.Period;
import org.optaplanner.examples.examination.domain.PeriodPenalty;
import org.optaplanner.examples.examination.domain.PeriodPenaltyType;
import org.optaplanner.examples.examination.domain.Room;
import org.optaplanner.examples.examination.domain.RoomPenalty;
import org.optaplanner.examples.examination.domain.RoomPenaltyType;
import org.optaplanner.examples.examination.domain.solver.TopicConflict;

public class ExaminationConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                conflictingExamsInSamePeriod(constraintFactory),
                periodDurationTooShort(constraintFactory),
                roomCapacityTooSmall(constraintFactory),
                periodPenaltyExamCoincidence(constraintFactory),
                periodPenaltyExclusion(constraintFactory),
                periodPenaltyAfter(constraintFactory),
                roomPenaltyExclusive(constraintFactory),

                // Soft constraints
                twoExamsInARow(constraintFactory),
                twoExamsInADay(constraintFactory),
                periodSpread(constraintFactory),
                mixedDurations(constraintFactory),
                frontLoad(constraintFactory),
                periodPenalty(constraintFactory),
                roomPenalty(constraintFactory)
        };
    }

    protected Constraint conflictingExamsInSamePeriod(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TopicConflict.class)
                .join(Exam.class,
                        equal(TopicConflict::getLeftTopic, Exam::getTopic),
                        filtering((topicConflict, leftExam) -> leftExam.getPeriod() != null))
                .ifExists(Exam.class,
                        equal((topicConflict, leftExam) -> topicConflict.getRightTopic(), Exam::getTopic),
                        equal((topicConflict, leftExam) -> leftExam.getPeriod(), Exam::getPeriod))
                .penalizeConfigurable((topicConflict, leftExam) -> topicConflict.getStudentSize())
                .asConstraint("conflictingExamsInSamePeriod");
    }

    protected Constraint periodDurationTooShort(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Exam.class)
                .filter(exam -> exam.getTopicDuration() > exam.getPeriodDuration())
                .penalizeConfigurable(Exam::getTopicStudentSize)
                .asConstraint("periodDurationTooShort");
    }

    protected Constraint roomCapacityTooSmall(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Exam.class)
                //  Period is genuine planning variable on LeadingExam, shadow var on FollowingExam, nothing on Exam.
                .filter(exam -> exam.getPeriod() != null)
                .groupBy(Exam::getRoom, Exam::getPeriod, sum(Exam::getTopicStudentSize))
                .filter((room, period, totalStudentSize) -> totalStudentSize > room.getCapacity())
                .penalizeConfigurable((room, period, totalStudentSize) -> totalStudentSize - room.getCapacity())
                .asConstraint("roomCapacityTooSmall");
    }

    protected Constraint periodPenaltyExamCoincidence(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(PeriodPenalty.class)
                .filter(periodPenalty -> periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.EXAM_COINCIDENCE)
                .join(Exam.class,
                        equal(PeriodPenalty::getLeftTopic, Exam::getTopic),
                        filtering((periodPenalty, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((periodPenalty, leftExam) -> periodPenalty.getRightTopic(), Exam::getTopic),
                        filtering((periodPenalty, leftExam, rightExam) -> rightExam.getPeriod() != null),
                        filtering((periodPenalty, leftExam, rightExam) -> leftExam.getPeriod() != rightExam.getPeriod()))
                .penalizeConfigurable((periodPenalty, leftExam, rightExam) -> leftExam.getTopic().getStudentSize()
                        + rightExam.getTopic().getStudentSize())
                .asConstraint("periodPenaltyExamCoincidence");
    }

    protected Constraint periodPenaltyExclusion(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(PeriodPenalty.class)
                .filter(periodPenalty -> periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.EXCLUSION)
                .join(Exam.class,
                        equal(PeriodPenalty::getLeftTopic, Exam::getTopic),
                        filtering((periodPenalty, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((periodPenalty, leftExam) -> periodPenalty.getRightTopic(), Exam::getTopic),
                        equal((periodPenalty, leftExam) -> leftExam.getPeriod(), Exam::getPeriod))
                .penalizeConfigurable((periodPenalty, leftExam, rightExam) -> leftExam.getTopic().getStudentSize()
                        + rightExam.getTopic().getStudentSize())
                .asConstraint("periodPenaltyExclusion");
    }

    protected Constraint periodPenaltyAfter(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(PeriodPenalty.class)
                .filter(periodPenalty -> periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.AFTER)
                .join(Exam.class,
                        equal(PeriodPenalty::getLeftTopic, Exam::getTopic),
                        filtering((periodPenalty, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((periodPenalty, leftExam) -> periodPenalty.getRightTopic(), Exam::getTopic),
                        lessThanOrEqual((periodPenalty, leftExam) -> leftExam.getPeriodIndex(), Exam::getPeriodIndex))
                .penalizeConfigurable((periodPenalty, leftExam, rightExam) -> leftExam.getTopic().getStudentSize()
                        + rightExam.getTopic().getStudentSize())
                .asConstraint("periodPenaltyAfter");
    }

    protected Constraint roomPenaltyExclusive(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(RoomPenalty.class)
                .filter(roomPenalty -> roomPenalty.getRoomPenaltyType() == RoomPenaltyType.ROOM_EXCLUSIVE)
                .join(Exam.class,
                        equal(RoomPenalty::getTopic, Exam::getTopic),
                        filtering((roomPenalty, leftExam) -> leftExam.getPeriod() != null && leftExam.getRoom() != null))
                .join(Exam.class,
                        equal((roomPenalty, leftExam) -> leftExam.getRoom(), Exam::getRoom),
                        equal((roomPenalty, leftExam) -> leftExam.getPeriod(), Exam::getPeriod),
                        filtering((roomPenalty, leftExam, rightExam) -> leftExam.getTopic() != rightExam.getTopic()))
                .penalizeConfigurable((periodPenalty, leftExam, rightExam) -> leftExam.getTopic().getStudentSize()
                        + rightExam.getTopic().getStudentSize())
                .asConstraint("roomPenaltyExclusive");
    }

    protected Constraint twoExamsInARow(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TopicConflict.class)
                .join(Exam.class,
                        equal(TopicConflict::getLeftTopic, Exam::getTopic),
                        filtering((topicConflict, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((topicConflict, leftExam) -> topicConflict.getRightTopic(), Exam::getTopic),
                        equal((topicConflict, leftExam) -> leftExam.getDayIndex(), Exam::getDayIndex),
                        filtering((topicConflict, leftExam,
                                rightExam) -> getPeriodIndexDifferenceBetweenExams(leftExam, rightExam) == 1))
                .penalizeConfigurable((topicConflict, leftExam, rightExam) -> topicConflict.getStudentSize())
                .asConstraint("twoExamsInARow");
    }

    protected Constraint twoExamsInADay(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TopicConflict.class)
                .join(Exam.class,
                        equal(TopicConflict::getLeftTopic, Exam::getTopic),
                        filtering((topicConflict, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((topicConflict, leftExam) -> topicConflict.getRightTopic(), Exam::getTopic),
                        equal((topicConflict, leftExam) -> leftExam.getDayIndex(), Exam::getDayIndex),
                        // Find exams in a day, but not being held right after each other. That case is handled in the twoExamsInARow constraint.
                        filtering((topicConflict, leftExam,
                                rightExam) -> getPeriodIndexDifferenceBetweenExams(leftExam, rightExam) > 1))
                .penalizeConfigurable((topicConflict, leftExam, rightExam) -> topicConflict.getStudentSize())
                .asConstraint("twoExamsInADay");
    }

    protected Constraint periodSpread(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ExaminationConstraintConfiguration.class)
                .join(TopicConflict.class)
                .join(Exam.class,
                        equal((config, topicConflict) -> topicConflict.getLeftTopic(), Exam::getTopic),
                        filtering((config, topicConflict, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((config, topicConflict, leftExam) -> topicConflict.getRightTopic(), Exam::getTopic),
                        filtering((config, topicConflict, leftExam, rightExam) -> rightExam.getPeriod() != null),
                        filtering((config, topicConflict, leftExam,
                                rightExam) -> getPeriodIndexDifferenceBetweenExams(leftExam,
                                        rightExam) < (config.getPeriodSpreadLength() + 1)))
                .penalizeConfigurable((config, topicConflict, leftExam, rightExam) -> topicConflict.getStudentSize())
                .asConstraint("periodSpread");
    }

    protected Constraint mixedDurations(ConstraintFactory constraintFactory) {
        // 4 mixed durations of 100, 150, 200 and 200 should only result in 2 penalties (for 100&150 and 100&200).
        return constraintFactory.forEach(Exam.class)
                //  Period is genuine planning variable on LeadingExam, shadow var on FollowingExam, nothing on Exam.
                .filter(leftExam -> leftExam.getPeriod() != null)
                .ifNotExistsOther(Exam.class,
                        equal(Exam::getPeriod),
                        equal(Exam::getRoom),
                        greaterThan(Exam::getId))
                .join(Exam.class,
                        equal(Exam::getPeriod),
                        equal(Exam::getRoom),
                        lessThan(Exam::getId),
                        filtering((leftExam, rightExam) -> leftExam.getTopicDuration() != rightExam.getTopicDuration()))
                .ifNotExists(Exam.class,
                        equal((leftExam, rightExam) -> leftExam.getPeriod(), Exam::getPeriod),
                        equal((leftExam, rightExam) -> leftExam.getRoom(), Exam::getRoom),
                        equal((leftExam, rightExam) -> rightExam.getTopicDuration(), Exam::getTopicDuration),
                        greaterThan((leftExam, rightExam) -> rightExam.getId(), Exam::getId))
                .penalizeConfigurable()
                .asConstraint("mixedDurations");
    }

    protected Constraint frontLoad(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Exam.class)
                .filter(exam -> exam.isTopicFrontLoadLarge() && exam.isPeriodFrontLoadLast())
                .penalizeConfigurable()
                .asConstraint("frontLoad");
    }

    protected Constraint periodPenalty(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Period.class)
                .filter(period -> period.getPenalty() != 0)
                .join(Exam.class,
                        equal(Function.identity(), Exam::getPeriod))
                .penalizeConfigurable((period, exam) -> period.getPenalty())
                .asConstraint("periodPenalty");
    }

    protected Constraint roomPenalty(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Room.class)
                .filter(room -> room.getPenalty() != 0)
                .join(Exam.class,
                        equal(Function.identity(), Exam::getRoom))
                .penalizeConfigurable((room, exam) -> room.getPenalty())
                .asConstraint("roomPenalty");
    }

    private int getPeriodIndexDifferenceBetweenExams(Exam leftExam, Exam rightExam) {
        return Math.abs(leftExam.getPeriodIndex() - rightExam.getPeriodIndex());
    }
}
