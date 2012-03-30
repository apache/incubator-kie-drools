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

package org.drools.planner.examples.examination.solver.solution.initializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.core.phase.custom.CustomSolverPhaseCommand;
import org.drools.planner.core.score.buildin.hardandsoft.DefaultHardAndSoftScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.examples.common.domain.PersistableIdComparator;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Examination;
import org.drools.planner.examples.examination.domain.Period;
import org.drools.planner.examples.examination.domain.PeriodPenalty;
import org.drools.planner.examples.examination.domain.PeriodPenaltyType;
import org.drools.planner.examples.examination.domain.Room;
import org.drools.planner.examples.examination.domain.Topic;
import org.drools.planner.examples.examination.domain.solver.ExamBefore;
import org.drools.planner.examples.examination.domain.solver.ExamCoincidence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExaminationSolutionInitializer implements CustomSolverPhaseCommand {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public void changeWorkingSolution(ScoreDirector scoreDirector) {
        Examination examination = (Examination) scoreDirector.getWorkingSolution();
        initializeExamList(scoreDirector, examination);
    }

    private void initializeExamList(ScoreDirector scoreDirector, Examination examination) {
        List<Period> periodList = examination.getPeriodList();
        List<Room> roomList = examination.getRoomList();
        // TODO the planning entity list from the solution should be used and might already contain initialized entities
        List<Exam> examList = new ArrayList<Exam>(examination.getTopicList().size()); // TODO this can be returned from createExamAssigningScoreList

        List<ExamInitializationWeight> examInitialWeightList = createExamAssigningScoreList(examination);

        for (ExamInitializationWeight examInitialWeight : examInitialWeightList) {
            Score unscheduledScore = scoreDirector.calculateScore();
            Exam leader = examInitialWeight.getExam();

            List<ExamToHandle> examToHandleList = new ArrayList<ExamToHandle>(5);
            if (leader.getExamCoincidence() == null) {
                examToHandleList.add(new ExamToHandle(leader));
            } else {
                for (Exam coincidenceExam : leader.getExamCoincidence().getCoincidenceExamSet()) {
                    examToHandleList.add(new ExamToHandle(coincidenceExam));
                }
            }

            List<PeriodScoring> periodScoringList = new ArrayList<PeriodScoring>(periodList.size());
            for (Period period : periodList) {
                for (ExamToHandle examToHandle : examToHandleList) {
                    Exam exam = examToHandle.getExam();
                    if (!examToHandle.isAdded()) {
                        scoreDirector.beforeEntityAdded(exam);
                        exam.setPeriod(period);
                        scoreDirector.afterEntityAdded(exam);
                        examToHandle.setAdded(true);
                    } else {
                        scoreDirector.beforeVariableChanged(exam, "period");
                        exam.setPeriod(period);
                        scoreDirector.afterVariableChanged(exam, "period");
                    }
                }
                Score score = scoreDirector.calculateScore();
                periodScoringList.add(new PeriodScoring(period, score));
            }
            Collections.sort(periodScoringList);

            scheduleLeader(periodScoringList, roomList, scoreDirector, unscheduledScore, examToHandleList, leader);
            examList.add(leader);

            // Schedule the non leaders
            for (ExamToHandle examToHandle : examToHandleList) {
                Exam exam = examToHandle.getExam();
                // Leader already has a room
                if (!exam.isCoincidenceLeader()) {
                    scheduleNonLeader(roomList, scoreDirector, exam);
                    examList.add(exam);
                }
            }
        }
        Collections.sort(examList, new PersistableIdComparator());
        examination.setExamList(examList);
    }

    private void scheduleLeader(List<PeriodScoring> periodScoringList, List<Room> roomList,
            ScoreDirector scoreDirector, Score unscheduledScore,
            List<ExamToHandle> examToHandleList, Exam leader) {
        boolean perfectMatch = false;
        Score bestScore = DefaultHardAndSoftScore.valueOf(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Period bestPeriod = null;
        Room bestRoom = null;
        for (PeriodScoring periodScoring : periodScoringList) {
            if (bestScore.compareTo(periodScoring.getScore()) >= 0) {
                // No need to check the rest
                break;
            }
            for (ExamToHandle examToHandle : examToHandleList) {
                Exam exam = examToHandle.getExam();
                scoreDirector.beforeVariableChanged(exam, "period");
                exam.setPeriod(periodScoring.getPeriod());
                scoreDirector.afterVariableChanged(exam, "period");
            }
            for (Room room : roomList) {
                scoreDirector.beforeVariableChanged(leader, "room");
                leader.setRoom(room);
                scoreDirector.afterVariableChanged(leader, "room");
                Score score = scoreDirector.calculateScore();
                if (score.compareTo(unscheduledScore) < 0) {
                    if (score.compareTo(bestScore) > 0) {
                        bestScore = score;
                        bestPeriod = periodScoring.getPeriod();
                        bestRoom = room;
                    }
                } else if (score.equals(unscheduledScore)) {
                    perfectMatch = true;
                    break;
                } else {
                    throw new IllegalStateException("The score (" + score
                            + ") cannot be higher than unscheduledScore (" + unscheduledScore + ").");
                }
            }
            if (perfectMatch) {
                break;
            }
        }
        if (!perfectMatch) {
            if (bestPeriod == null || bestRoom == null) {
                throw new IllegalStateException("The bestPeriod (" + bestPeriod + ") or the bestRoom ("
                        + bestRoom + ") cannot be null.");
            }
            scoreDirector.beforeVariableChanged(leader, "room");
            leader.setRoom(bestRoom);
            scoreDirector.afterVariableChanged(leader, "room");
            for (ExamToHandle examToHandle : examToHandleList) {
                Exam exam = examToHandle.getExam();
                scoreDirector.beforeVariableChanged(exam, "period");
                exam.setPeriod(bestPeriod);
                scoreDirector.afterVariableChanged(exam, "period");
            }
        }
        logger.debug("    Exam ({}) initialized.", leader);
    }

    private void scheduleNonLeader(List<Room> roomList, ScoreDirector scoreDirector, Exam exam) {
        if (exam.getRoom() != null) {
            throw new IllegalStateException("Exam (" + exam + ") already has a room.");
        }
        Score unscheduledScore = scoreDirector.calculateScore();
        boolean perfectMatch = false;
        Score bestScore = DefaultHardAndSoftScore.valueOf(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Room bestRoom = null;
        for (Room room : roomList) {
            scoreDirector.beforeVariableChanged(exam, "room");
            exam.setRoom(room);
            scoreDirector.afterVariableChanged(exam, "room");
            Score score = scoreDirector.calculateScore();
            if (score.compareTo(unscheduledScore) < 0) {
                if (score.compareTo(bestScore) > 0) {
                    bestScore = score;
                    bestRoom = room;
                }
            } else if (score.equals(unscheduledScore)) {
                perfectMatch = true;
                break;
            } else {
                throw new IllegalStateException("The score (" + score
                        + ") cannot be higher than unscheduledScore (" + unscheduledScore + ").");
            }
        }
        if (!perfectMatch) {
            if (bestRoom == null) {
                throw new IllegalStateException("The bestRoom ("
                        + bestRoom + ") cannot be null.");
            }
            scoreDirector.beforeVariableChanged(exam, "room");
            exam.setRoom(bestRoom);
            scoreDirector.afterVariableChanged(exam, "room");
        }
        logger.debug("    Exam ({}) initialized.", exam);
    }

    public static class ExamToHandle {

        private Exam exam;
        private boolean added = false;

        public ExamToHandle(Exam exam) {
            this.exam = exam;
        }

        public Exam getExam() {
            return exam;
        }

        public boolean isAdded() {
            return added;
        }

        public void setAdded(boolean added) {
            this.added = added;
        }
    }

    /**
     * Create and order the exams in the order which we 'll assign them into periods and rooms.
     * @param examination not null
     * @return not null
     */
    private List<ExamInitializationWeight> createExamAssigningScoreList(Examination examination) {
        List<Exam> examList = createExamList(examination);
        List<ExamInitializationWeight> examInitialWeightList = new ArrayList<ExamInitializationWeight>(examList.size());
        for (Exam exam : examList) {
            if (exam.isCoincidenceLeader()) {
                examInitialWeightList.add(new ExamInitializationWeight(exam));
            }
        }
        Collections.sort(examInitialWeightList);
        return examInitialWeightList;
    }

    public List<Exam> createExamList(Examination examination) {
        List<Topic> topicList = examination.getTopicList();
        List<Exam> examList = new ArrayList<Exam>(topicList.size());
        Map<Topic, Exam> topicToExamMap = new HashMap<Topic, Exam>(topicList.size());
        for (Topic topic : topicList) {
            Exam exam = new Exam();
            exam.setId(topic.getId());
            exam.setTopic(topic);
            examList.add(exam);
            topicToExamMap.put(topic, exam);
        }
        for (PeriodPenalty periodPenalty : examination.getPeriodPenaltyList()) {
            if (periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.EXAM_COINCIDENCE) {
                Exam leftExam = topicToExamMap.get(periodPenalty.getLeftSideTopic());
                Exam rightExam = topicToExamMap.get(periodPenalty.getRightSideTopic());

                Set<Exam> newCoincidenceExamSet = new LinkedHashSet<Exam>(4);
                ExamCoincidence leftExamCoincidence = leftExam.getExamCoincidence();
                if (leftExamCoincidence != null) {
                    newCoincidenceExamSet.addAll(leftExamCoincidence.getCoincidenceExamSet());
                } else {
                    newCoincidenceExamSet.add(leftExam);
                }
                ExamCoincidence rightExamCoincidence = rightExam.getExamCoincidence();
                if (rightExamCoincidence != null) {
                    newCoincidenceExamSet.addAll(rightExamCoincidence.getCoincidenceExamSet());
                } else {
                    newCoincidenceExamSet.add(rightExam);
                }
                ExamCoincidence newExamCoincidence = new ExamCoincidence(newCoincidenceExamSet);
                for (Exam exam : newCoincidenceExamSet) {
                    exam.setExamCoincidence(newExamCoincidence);
                }
            } else if (periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.AFTER) {
                Exam afterExam = topicToExamMap.get(periodPenalty.getLeftSideTopic());
                Exam beforeExam = topicToExamMap.get(periodPenalty.getRightSideTopic());
                ExamBefore examBefore = beforeExam.getExamBefore();
                if (examBefore == null) {
                    examBefore = new ExamBefore(new LinkedHashSet<Exam>(2));
                    beforeExam.setExamBefore(examBefore);
                }
                examBefore.getAfterExamSet().add(afterExam);
            }
        }
        return examList;
    }

    private static class ExamInitializationWeight implements Comparable<ExamInitializationWeight> {

        private Exam exam;
        private int totalStudentSize;
        private int maximumDuration;

        private ExamInitializationWeight(Exam exam) {
            this.exam = exam;
            totalStudentSize = calculateTotalStudentSize(exam);
            maximumDuration = calculateMaximumDuration(exam);
        }

        private int calculateTotalStudentSize(Exam innerExam) {
            int innerTotalStudentSize = 0;
            if (innerExam.getExamCoincidence() == null) {
                innerTotalStudentSize = innerExam.getTopicStudentSize();
            } else {
                for (Exam coincidenceExam : innerExam.getExamCoincidence().getCoincidenceExamSet()) {
                    innerTotalStudentSize += coincidenceExam.getTopicStudentSize();
                }
            }
            if (innerExam.getExamBefore() != null) {
                for (Exam afterExam : innerExam.getExamBefore().getAfterExamSet()) {
                    innerTotalStudentSize += calculateTotalStudentSize(afterExam); // recursive
                }
            }
            return innerTotalStudentSize;
        }

        private int calculateMaximumDuration(Exam innerExam) {
            int innerMaximumDuration = innerExam.getTopic().getDuration();
            if (innerExam.getExamCoincidence() != null) {
                for (Exam coincidenceExam : innerExam.getExamCoincidence().getCoincidenceExamSet()) {
                    innerMaximumDuration = Math.max(innerMaximumDuration, coincidenceExam.getTopicStudentSize());
                }
            }
            if (innerExam.getExamBefore() != null) {
                for (Exam afterExam : innerExam.getExamBefore().getAfterExamSet()) {
                    innerMaximumDuration = Math.max(innerMaximumDuration, calculateMaximumDuration(afterExam)); // recursive
                }
            }
            return innerMaximumDuration;
        }

        public Exam getExam() {
            return exam;
        }

        public int compareTo(ExamInitializationWeight other) {
            // TODO calculate a assigningScore based on the properties of a topic and sort on that assigningScore
            return new CompareToBuilder()
                    .append(other.totalStudentSize, totalStudentSize) // Descending
                    .append(other.maximumDuration, maximumDuration) // Descending
                    .append(exam.getId(), other.exam.getId()) // Ascending
                    .toComparison();
        }

    }

    private static class PeriodScoring implements Comparable<PeriodScoring> {

        private Period period;
        private Score score;

        private PeriodScoring(Period period, Score score) {
            this.period = period;
            this.score = score;
        }

        public Period getPeriod() {
            return period;
        }

        public Score getScore() {
            return score;
        }

        public int compareTo(PeriodScoring other) {
            return -new CompareToBuilder().append(score, other.score).toComparison();
        }

    }

}
