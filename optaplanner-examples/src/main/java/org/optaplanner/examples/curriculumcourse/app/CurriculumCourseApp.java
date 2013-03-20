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

package org.optaplanner.examples.curriculumcourse.app;

import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseDaoImpl;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseSolutionExporter;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseSolutionImporter;
import org.optaplanner.examples.curriculumcourse.swingui.CurriculumCoursePanel;

public class CurriculumCourseApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "/org/optaplanner/examples/curriculumcourse/solver/curriculumCourseSolverConfig.xml";

    public static void main(String[] args) {
        fixateLookAndFeel();
        new CurriculumCourseApp().init();
    }

    @Override
    protected Solver createSolver() {
        XmlSolverFactory solverFactory = new XmlSolverFactory();
        solverFactory.configure(SOLVER_CONFIG);
        return solverFactory.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new CurriculumCoursePanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new CurriculumCourseDaoImpl();
    }

    @Override
    protected AbstractSolutionImporter createSolutionImporter() {
        return new CurriculumCourseSolutionImporter();
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new CurriculumCourseSolutionExporter();
    }

}
