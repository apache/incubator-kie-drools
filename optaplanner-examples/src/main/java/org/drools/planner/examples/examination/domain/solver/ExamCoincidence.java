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

package org.drools.planner.examples.examination.domain.solver;

import java.io.Serializable;
import java.util.Set;

import org.drools.planner.examples.examination.domain.Exam;

/**
 * Calculated during initialization, not used for score calculation, used for move creation.
 */
public class ExamCoincidence implements Serializable {

    private Set<Exam> coincidenceExamSet;
    private Exam firstExam;

    public ExamCoincidence(Set<Exam> coincidenceExamSet) {
        this.coincidenceExamSet = coincidenceExamSet;
        for (Exam exam : coincidenceExamSet) {
            if (firstExam == null || firstExam.getId() > exam.getId()) {
                firstExam = exam;
            }
        }
    }

    public Set<Exam> getCoincidenceExamSet() {
        return coincidenceExamSet;
    }

    public void setCoincidenceExamSet(Set<Exam> coincidenceExamSet) {
        this.coincidenceExamSet = coincidenceExamSet;
    }

    public Exam getFirstExam() {
        return firstExam;
    }

    public void setFirstExam(Exam firstExam) {
        this.firstExam = firstExam;
    }

}
