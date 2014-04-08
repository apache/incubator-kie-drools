/*
 * Copyright 2014 JBoss Inc
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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.curriculumcourse.domain.Course;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.domain.FollowingExam;
import org.optaplanner.examples.examination.domain.LeadingExam;
import org.optaplanner.examples.examination.domain.PeriodPenalty;

public class ExamDifficultyWeightFactory implements SelectionSorterWeightFactory<Examination, Exam> {

    public Comparable createSorterWeight(Examination examination, Exam exam) {
        int studentSizeTotal = exam.getTopicStudentSize();
        int maximumDuration = exam.getTopicDuration();
        for (PeriodPenalty periodPenalty : examination.getPeriodPenaltyList()) {
            if (periodPenalty.getLeftSideTopic().equals(exam.getTopic())) {
                switch (periodPenalty.getPeriodPenaltyType()) {
                    case EXAM_COINCIDENCE:
                        studentSizeTotal += periodPenalty.getRightSideTopic().getStudentSize();
                        maximumDuration = Math.max(maximumDuration, periodPenalty.getRightSideTopic().getDuration());
                        break;
                    case EXCLUSION:
                        // Do nothing
                        break;
                    case AFTER:
                        // Do nothing
                        break;
                    default:
                        throw new IllegalStateException("The periodPenaltyType ("
                                + periodPenalty.getPeriodPenaltyType() + ") is not implemented.");
                }
            } else if (periodPenalty.getRightSideTopic().equals(exam.getTopic())) {
                switch (periodPenalty.getPeriodPenaltyType()) {
                    case EXAM_COINCIDENCE:
                        studentSizeTotal += periodPenalty.getLeftSideTopic().getStudentSize();
                        maximumDuration = Math.max(maximumDuration, periodPenalty.getLeftSideTopic().getDuration());
                        break;
                    case EXCLUSION:
                        // Do nothing
                        break;
                    case AFTER:
                        studentSizeTotal += periodPenalty.getLeftSideTopic().getStudentSize();
                        maximumDuration = Math.max(maximumDuration, periodPenalty.getLeftSideTopic().getDuration());
                        break;
                    default:
                        throw new IllegalStateException("The periodPenaltyType ("
                                + periodPenalty.getPeriodPenaltyType() + ") is not implemented.");
                }
            }
        }
        return new ExamDifficultyWeight(exam, studentSizeTotal, maximumDuration);
    }

    public static class ExamDifficultyWeight implements Comparable<ExamDifficultyWeight> {

        private final Exam exam;
        private final int studentSizeTotal;
        private final int maximumDuration;

        public ExamDifficultyWeight(Exam exam, int studentSizeTotal, int maximumDuration) {
            this.exam = exam;
            this.studentSizeTotal = studentSizeTotal;
            this.maximumDuration = maximumDuration;
        }

        public int compareTo(ExamDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(studentSizeTotal, other.studentSizeTotal)
                    .append(maximumDuration, other.maximumDuration)
                    .append(exam.getId(), other.exam.getId())
                    .toComparison();
        }

    }

}
