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

package org.optaplanner.examples.curriculumcourse.persistence;

import java.io.IOException;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.curriculumcourse.app.CurriculumCourseApp;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;

public class CurriculumCourseExporter extends AbstractTxtSolutionExporter<CourseSchedule> {
    private static final String OUTPUT_FILE_SUFFIX = "sol";

    public static void main(String[] args) {
        SolutionConverter<CourseSchedule> converter = SolutionConverter.createExportConverter(
                CurriculumCourseApp.DATA_DIR_NAME, CourseSchedule.class, new CurriculumCourseExporter());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    @Override
    public TxtOutputBuilder<CourseSchedule> createTxtOutputBuilder() {
        return new CurriculumCourseOutputBuilder();
    }

    public static class CurriculumCourseOutputBuilder extends TxtOutputBuilder<CourseSchedule> {

        @Override
        public void writeSolution() throws IOException {
            for (Lecture lecture : solution.getLectureList()) {
                bufferedWriter.write(lecture.getCourse().getCode()
                        + " r" + lecture.getRoom().getCode()
                        + " " + lecture.getPeriod().getDay().getDayIndex()
                        + " " + lecture.getPeriod().getTimeslot().getTimeslotIndex() + "\r\n");
            }
        }
    }

}
