package org.optaplanner.examples.examination.solver.score;

import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;

import java.util.function.Function;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
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

// TODO The ExaminationConstraintProvider is unusually slow. For more information, see: https://issues.redhat.com/browse/PLANNER-2011
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
        return constraintFactory.from(TopicConflict.class)
                .join(Exam.class,
                        equal(TopicConflict::getLeftTopic, Exam::getTopic),
                        filtering((topicConflict, leftExam) -> leftExam.getPeriod() != null))
                .ifExists(Exam.class,
                        equal((topicConflict, leftExam) -> topicConflict.getRightTopic(), Exam::getTopic),
                        equal((topicConflict, leftExam) -> leftExam.getPeriod(), Exam::getPeriod))
                .penalizeConfigurable("conflictingExamsInSamePeriod",
                        (topicConflict, leftExam) -> topicConflict.getStudentSize());
    }

    protected Constraint periodDurationTooShort(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Exam.class)
                .filter(exam -> exam.getTopicDuration() > exam.getPeriodDuration())
                .penalizeConfigurable("periodDurationTooShort", Exam::getTopicStudentSize);
    }

    protected Constraint roomCapacityTooSmall(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Room.class)
                .join(Exam.class, equal(Function.identity(), Exam::getRoom))
                .join(Period.class, equal((room, exam) -> exam.getPeriod(), Function.identity()))
                .groupBy((room, exam, period) -> room,
                        (room, exam, period) -> period,
                        ConstraintCollectors.sum((period, exam, room) -> exam.getTopicStudentSize()))
                .filter((room, period, totalStudentSize) -> totalStudentSize > room.getCapacity())
                .penalizeConfigurable("roomCapacityTooSmall",
                        (room, period, totalStudentSize) -> totalStudentSize - room.getCapacity());
    }

    protected Constraint periodPenaltyExamCoincidence(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PeriodPenalty.class)
                .filter(periodPenalty -> periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.EXAM_COINCIDENCE)
                .join(Exam.class,
                        equal(PeriodPenalty::getLeftTopic, Exam::getTopic),
                        filtering((periodPenalty, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((periodPenalty, leftExam) -> periodPenalty.getRightTopic(), Exam::getTopic),
                        filtering((periodPenalty, leftExam, rightExam) -> rightExam.getPeriod() != null),
                        filtering((periodPenalty, leftExam, rightExam) -> leftExam.getPeriod() != rightExam.getPeriod()))
                .penalizeConfigurable("periodPenaltyExamCoincidence",
                        (periodPenalty, leftExam, rightExam) -> leftExam.getTopic().getStudentSize()
                                + rightExam.getTopic().getStudentSize());
    }

    protected Constraint periodPenaltyExclusion(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PeriodPenalty.class)
                .filter(periodPenalty -> periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.EXCLUSION)
                .join(Exam.class,
                        equal(PeriodPenalty::getLeftTopic, Exam::getTopic),
                        filtering((periodPenalty, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((periodPenalty, leftExam) -> periodPenalty.getRightTopic(), Exam::getTopic),
                        filtering((periodPenalty, leftExam, rightExam) -> rightExam.getPeriod() != null),
                        filtering((periodPenalty, leftExam, rightExam) -> leftExam.getPeriod().equals(rightExam.getPeriod())))
                .penalizeConfigurable("periodPenaltyExclusion",
                        (periodPenalty, leftExam, rightExam) -> leftExam.getTopic().getStudentSize()
                                + rightExam.getTopic().getStudentSize());
    }

    protected Constraint periodPenaltyAfter(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PeriodPenalty.class)
                .filter(periodPenalty -> periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.AFTER)
                .join(Exam.class,
                        equal(PeriodPenalty::getLeftTopic, Exam::getTopic),
                        filtering((periodPenalty, leftExam) -> leftExam.getPeriod() != null))
                .join(Exam.class,
                        equal((periodPenalty, leftExam) -> periodPenalty.getRightTopic(), Exam::getTopic),
                        filtering((periodPenalty, leftExam, rightExam) -> rightExam.getPeriod() != null),
                        filtering((periodPenalty, leftExam,
                                rightExam) -> leftExam.getPeriod().getPeriodIndex() <= rightExam.getPeriod().getPeriodIndex()))
                .penalizeConfigurable("periodPenaltyAfter",
                        (periodPenalty, leftExam, rightExam) -> leftExam.getTopic().getStudentSize()
                                + rightExam.getTopic().getStudentSize());
    }

    protected Constraint roomPenaltyExclusive(ConstraintFactory constraintFactory) {
        return constraintFactory.from(RoomPenalty.class)
                .filter(roomPenalty -> roomPenalty.getRoomPenaltyType() == RoomPenaltyType.ROOM_EXCLUSIVE)
                .join(Exam.class,
                        equal(RoomPenalty::getTopic, Exam::getTopic),
                        filtering((roomPenalty, leftExam) -> leftExam.getPeriod() != null && leftExam.getRoom() != null))
                .join(Exam.class,
                        equal((roomPenalty, leftExam) -> leftExam.getRoom(), Exam::getRoom),
                        equal((roomPenalty, leftExam) -> leftExam.getPeriod(), Exam::getPeriod),
                        filtering((roomPenalty, leftExam, rightExam) -> leftExam.getTopic() != rightExam.getTopic()))
                .penalizeConfigurable("roomPenaltyExclusive",
                        (periodPenalty, leftExam, rightExam) -> leftExam.getTopic().getStudentSize()
                                + rightExam.getTopic().getStudentSize());
    }

    protected Constraint twoExamsInARow(ConstraintFactory constraintFactory) {
        return constraintFactory.from(TopicConflict.class)
                .join(Exam.class,
                        equal(TopicConflict::getLeftTopic, Exam::getTopic),
                        filtering((topicConflict, leftExam) -> leftExam.getPeriod() != null))
                .ifExists(Exam.class,
                        equal((topicConflict, leftExam) -> topicConflict.getRightTopic(), Exam::getTopic),
                        equal((topicConflict, leftExam) -> leftExam.getDayIndex(), Exam::getDayIndex),
                        filtering((topicConflict, leftExam,
                                rightExam) -> getPeriodIndexDifferenceBetweenExams(leftExam, rightExam) == 1))
                .penalizeConfigurable("twoExamsInARow", (topicConflict, leftExam) -> topicConflict.getStudentSize());
    }

    protected Constraint twoExamsInADay(ConstraintFactory constraintFactory) {
        return constraintFactory.from(TopicConflict.class)
                .join(Exam.class,
                        equal(TopicConflict::getLeftTopic, Exam::getTopic),
                        filtering((topicConflict, leftExam) -> leftExam.getPeriod() != null))
                .ifExists(Exam.class,
                        equal((topicConflict, leftExam) -> topicConflict.getRightTopic(), Exam::getTopic),
                        equal((topicConflict, leftExam) -> leftExam.getDayIndex(), Exam::getDayIndex),
                        // Find exams in a day, but not being held right after each other. That case is handled in the twoExamsInARow constraint.
                        filtering((topicConflict, leftExam,
                                rightExam) -> getPeriodIndexDifferenceBetweenExams(leftExam, rightExam) > 1))
                .penalizeConfigurable("twoExamsInADay", (topicConflict, leftExam) -> topicConflict.getStudentSize());
    }

    protected Constraint periodSpread(ConstraintFactory constraintFactory) {
        return constraintFactory.from(ExaminationConstraintConfiguration.class)
                .join(TopicConflict.class)
                .join(Exam.class,
                        equal((config, topicConflict) -> topicConflict.getLeftTopic(), Exam::getTopic),
                        filtering((config, topicConflict, leftExam) -> leftExam.getPeriod() != null))
                .ifExists(Exam.class,
                        equal((config, topicConflict, leftExam) -> topicConflict.getRightTopic(), Exam::getTopic),
                        filtering((config, topicConflict, leftExam, rightExam) -> rightExam.getPeriod() != null),
                        filtering((config, topicConflict, leftExam,
                                rightExam) -> getPeriodIndexDifferenceBetweenExams(leftExam,
                                        rightExam) < (config.getPeriodSpreadLength() + 1)))
                .penalizeConfigurable("periodSpread",
                        (config, topicConflict, leftExam) -> topicConflict.getStudentSize());
    }

    protected Constraint mixedDurations(ConstraintFactory constraintFactory) {
        // 4 mixed durations of 100, 150, 200 and 200 should only result in 2 penalties (for 100&150 and 100&200).
        return constraintFactory.fromUniquePair(Exam.class,
                equal(Exam::getPeriod),
                equal(Exam::getRoom))
                // Keep only those that have different topic durations in the same room-period pair.
                .filter((leftExam, rightExam) -> leftExam.getTopicDuration() != rightExam.getTopicDuration())
                // Keep only those where the left exam's id is the lowest one in the room-period pair.
                .ifNotExists(Exam.class,
                        equal((leftExam, rightExam) -> leftExam.getPeriod(), Exam::getPeriod),
                        equal((leftExam, rightExam) -> leftExam.getRoom(), Exam::getRoom),
                        greaterThan((leftExam, rightExam) -> leftExam.getId(), Exam::getId))
                // Keep only those where the left exam's id is the lowest one in the room-period pair.
                .ifNotExists(Exam.class, equal((leftExam, rightExam) -> leftExam.getPeriod(), Exam::getPeriod),
                        equal((leftExam, rightExam) -> leftExam.getRoom(), Exam::getRoom),
                        equal((leftExam, rightExam) -> rightExam.getTopicDuration(), Exam::getTopicDuration),
                        greaterThan((leftExam, rightExam) -> rightExam.getId(), Exam::getId))
                .penalizeConfigurable("mixedDurations");
    }

    protected Constraint frontLoad(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Exam.class)
                .filter(exam -> exam.isTopicFrontLoadLarge() && exam.isPeriodFrontLoadLast())
                .penalizeConfigurable("frontLoad");
    }

    protected Constraint periodPenalty(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Exam.class)
                .filter(exam -> exam.getPeriod().getPenalty() != 0)
                .penalizeConfigurable("periodPenalty", exam -> exam.getPeriod().getPenalty());
    }

    protected Constraint roomPenalty(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Exam.class)
                .filter(exam -> exam.getRoom().getPenalty() != 0)
                .penalizeConfigurable("roomPenalty", exam -> exam.getRoom().getPenalty());
    }

    private int getPeriodIndexDifferenceBetweenExams(Exam leftExam, Exam rightExam) {
        return Math.abs(leftExam.getPeriodIndex() - rightExam.getPeriodIndex());
    }
}
