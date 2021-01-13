/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.curriculumcourse.app;

import java.util.HashSet;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseExporter;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseImporter;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseXmlSolutionFileIO;
import org.optaplanner.examples.curriculumcourse.swingui.CurriculumCoursePanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class CurriculumCourseApp extends CommonApp<CourseSchedule> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/curriculumcourse/solver/curriculumCourseSolverConfig.xml";

    public static final String DATA_DIR_NAME = "curriculumcourse";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new CurriculumCourseApp().init();
    }

    public CurriculumCourseApp() {
        super("Course timetabling",
                "Official competition name: ITC 2007 track3 - Curriculum course scheduling\n\n" +
                        "Assign lectures to periods and rooms.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                CurriculumCoursePanel.LOGO_PATH);
    }

    @Override
    protected CurriculumCoursePanel createSolutionPanel() {
        return new CurriculumCoursePanel();
    }

    @Override
    public SolutionFileIO<CourseSchedule> createSolutionFileIO() {
        return new CurriculumCourseXmlSolutionFileIO();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[] {
                new CurriculumCourseImporter()
        };
    }

    @Override
    protected Set<AbstractSolutionExporter> createSolutionExporters() {
        Set<AbstractSolutionExporter> exporters = new HashSet<>(1);
        exporters.add(new CurriculumCourseExporter());
        return exporters;
    }

}
