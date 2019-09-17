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

package org.optaplanner.examples.examination.persistence;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.examination.app.ExaminationApp;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.Examination;

public class ExaminationExporter extends AbstractTxtSolutionExporter<Examination> {

    private static final String OUTPUT_FILE_SUFFIX = "sln";

    public static void main(String[] args) {
        SolutionConverter<Examination> converter = SolutionConverter.createExportConverter(
                ExaminationApp.DATA_DIR_NAME, Examination.class, new ExaminationExporter());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    @Override
    public TxtOutputBuilder<Examination> createTxtOutputBuilder() {
        return new ExaminationOutputBuilder();
    }

    public static class ExaminationOutputBuilder extends TxtOutputBuilder<Examination> {

        @Override
        public void writeSolution() throws IOException {
            Collections.sort(solution.getExamList(), Comparator.comparingLong(Exam::getId)); // TODO remove me when obsolete
            for (Exam exam : solution.getExamList()) {
                bufferedWriter.write(exam.getPeriod().getId() + ", " + exam.getRoom().getId() + "\r\n");
            }
        }

    }

}
