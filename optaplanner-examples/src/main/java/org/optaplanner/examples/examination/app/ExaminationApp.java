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

package org.optaplanner.examples.examination.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.curriculumcourse.app.CurriculumCourseApp;
import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.persistence.ExaminationDao;
import org.optaplanner.examples.examination.persistence.ExaminationExporter;
import org.optaplanner.examples.examination.persistence.ExaminationImporter;
import org.optaplanner.examples.examination.swingui.ExaminationPanel;

/**
 * Examination is super optimized and a bit complex.
 * {@link CurriculumCourseApp} is arguably a better example to learn from.
 */
public class ExaminationApp extends CommonApp<Examination> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/examination/solver/examinationSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new ExaminationApp().init();
    }

    public ExaminationApp() {
        super("Exam timetabling",
                "Official competition name: ITC 2007 track1 - Examination timetabling\n\n" +
                        "Assign exams to timeslots and rooms.",
                SOLVER_CONFIG,
                ExaminationPanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new ExaminationPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new ExaminationDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new ExaminationImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new ExaminationExporter();
    }

}
