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
public class ExamBefore implements Serializable {

    private Set<Exam> afterExamSet;

    public ExamBefore(Set<Exam> afterExamSet) {
        this.afterExamSet = afterExamSet;
    }

    public Set<Exam> getAfterExamSet() {
        return afterExamSet;
    }

    public void setAfterExamSet(Set<Exam> afterExamSet) {
        this.afterExamSet = afterExamSet;
    }

}
