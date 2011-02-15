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

package org.drools.planner.examples.curriculumcourse.app;

import org.drools.planner.config.XmlSolverConfigurer;
import org.drools.planner.core.Solver;
import org.drools.planner.examples.common.app.CommonApp;
import org.drools.planner.examples.common.persistence.AbstractSolutionExporter;
import org.drools.planner.examples.common.persistence.AbstractSolutionImporter;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.curriculumcourse.persistence.CurriculumCourseDaoImpl;
import org.drools.planner.examples.curriculumcourse.persistence.CurriculumCourseSolutionExporter;
import org.drools.planner.examples.curriculumcourse.persistence.CurriculumCourseSolutionImporter;
import org.drools.planner.examples.curriculumcourse.swingui.CurriculumCoursePanel;

public class CurriculumCourseApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "/org/drools/planner/examples/curriculumcourse/solver/curriculumCourseSolverConfig.xml";

    public static void main(String[] args) {
        new CurriculumCourseApp().init();
    }

    @Override
    protected Solver createSolver() {
        XmlSolverConfigurer configurer = new XmlSolverConfigurer();
        configurer.configure(SOLVER_CONFIG);
        return configurer.buildSolver();
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
