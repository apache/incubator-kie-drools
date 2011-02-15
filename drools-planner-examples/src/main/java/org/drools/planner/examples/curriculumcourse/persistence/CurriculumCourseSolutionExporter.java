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

package org.drools.planner.examples.curriculumcourse.persistence;

import java.io.IOException;

import org.drools.planner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;
import org.drools.planner.core.solution.Solution;

public class CurriculumCourseSolutionExporter extends AbstractTxtSolutionExporter {

    private static final String OUTPUT_FILE_SUFFIX = ".sol";

    public static void main(String[] args) {
        new CurriculumCourseSolutionExporter().convertAll();
    }

    public CurriculumCourseSolutionExporter() {
        super(new CurriculumCourseDaoImpl());
    }

    @Override
    protected String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    public TxtOutputBuilder createTxtOutputBuilder() {
        return new CurriculumCourseOutputBuilder();
    }

    public class CurriculumCourseOutputBuilder extends TxtOutputBuilder {

        private CurriculumCourseSchedule schedule;

        public void setSolution(Solution solution) {
            schedule = (CurriculumCourseSchedule) solution;
        }

        public void writeSolution() throws IOException {
            for (Lecture lecture : schedule.getLectureList()) {
                bufferedWriter.write(lecture.getCourse().getCode()
                        + " r" + lecture.getRoom().getCode()
                        + " " + lecture.getPeriod().getDay().getDayIndex()
                        + " " + lecture.getPeriod().getTimeslot().getTimeslotIndex() + "\r\n");
            }
        }
    }

}
