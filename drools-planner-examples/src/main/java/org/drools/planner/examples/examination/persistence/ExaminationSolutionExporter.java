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

package org.drools.planner.examples.examination.persistence;

import java.io.IOException;
import java.util.Collections;

import org.drools.planner.examples.common.domain.PersistableIdComparator;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Examination;
import org.drools.planner.core.solution.Solution;

public class ExaminationSolutionExporter extends AbstractTxtSolutionExporter {

    private static final String OUTPUT_FILE_SUFFIX = ".sln";

    public static void main(String[] args) {
        new ExaminationSolutionExporter().convertAll();
    }

    public ExaminationSolutionExporter() {
        super(new ExaminationDaoImpl());
    }

    @Override
    protected String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    public TxtOutputBuilder createTxtOutputBuilder() {
        return new ExaminationOutputBuilder();
    }

    public class ExaminationOutputBuilder extends TxtOutputBuilder {

        private Examination examination;

        public void setSolution(Solution solution) {
            examination = (Examination) solution;
        }

        public void writeSolution() throws IOException {
            Collections.sort(examination.getExamList(), new PersistableIdComparator()); // TODO remove me when obsolete
            for (Exam exam : examination.getExamList()) {
                bufferedWriter.write(exam.getPeriod().getId() + ", " + exam.getRoom().getId() + "\r\n");
            }
        }

    }

}
