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

package org.drools.planner.examples.common.app;

import javax.swing.JFrame;

import org.drools.planner.core.Solver;
import org.drools.planner.examples.common.business.SolutionBusiness;
import org.drools.planner.examples.common.persistence.AbstractSolutionExporter;
import org.drools.planner.examples.common.persistence.AbstractSolutionImporter;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.common.swingui.WorkflowFrame;

public abstract class CommonApp extends LoggingMain {

    private WorkflowFrame workflowFrame;
    private SolutionBusiness solutionBusiness;

    public CommonApp() {
        solutionBusiness = createSolutionBusiness();
        workflowFrame = new WorkflowFrame(solutionBusiness, createSolutionPanel(), solutionBusiness.getDirName());
    }

    public void init() {
        init(true);
    }

    public void init(boolean exitOnClose) {
        workflowFrame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        workflowFrame.init();
        workflowFrame.setVisible(true);
    }

    public SolutionBusiness createSolutionBusiness() {
        SolutionDao solutionDao = createSolutionDao();
        SolutionBusiness solutionBusiness = new SolutionBusiness();
        solutionBusiness.setSolutionDao(solutionDao);
        solutionBusiness.setImporter(createSolutionImporter());
        solutionBusiness.setExporter(createSolutionExporter());
        solutionBusiness.updateDataDirs();
        solutionBusiness.setSolver(createSolver());
        return solutionBusiness;
    }

    protected abstract Solver createSolver();

    protected abstract SolutionPanel createSolutionPanel();

    protected abstract SolutionDao createSolutionDao();

    protected AbstractSolutionImporter createSolutionImporter() {
        return null;
    }

    protected AbstractSolutionExporter createSolutionExporter() {
        return null;
    }

}
