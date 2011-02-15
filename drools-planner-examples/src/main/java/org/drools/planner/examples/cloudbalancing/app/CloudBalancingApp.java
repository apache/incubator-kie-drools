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

package org.drools.planner.examples.cloudbalancing.app;

import org.drools.planner.config.XmlSolverConfigurer;
import org.drools.planner.core.Solver;
import org.drools.planner.examples.cloudbalancing.persistence.CloudBalancingDaoImpl;
import org.drools.planner.examples.cloudbalancing.swingui.CloudBalancingPanel;
import org.drools.planner.examples.common.app.CommonApp;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.common.swingui.SolutionPanel;

public class CloudBalancingApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "/org/drools/planner/examples/cloudbalancing/solver/cloudBalancingSolverConfig.xml";

    public static void main(String[] args) {
        new CloudBalancingApp().init();
    }

    @Override
    protected Solver createSolver() {
        XmlSolverConfigurer configurer = new XmlSolverConfigurer();
        configurer.configure(SOLVER_CONFIG);
        return configurer.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new CloudBalancingPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new CloudBalancingDaoImpl();
    }

}
