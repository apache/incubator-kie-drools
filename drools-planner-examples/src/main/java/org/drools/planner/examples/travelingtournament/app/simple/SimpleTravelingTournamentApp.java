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

package org.drools.planner.examples.travelingtournament.app.simple;

import org.drools.planner.config.XmlSolverConfigurer;
import org.drools.planner.core.Solver;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.travelingtournament.app.AbstractTravelingTournamentApp;
import org.drools.planner.examples.travelingtournament.persistence.simple.SimpleTravelingTournamentDaoImpl;
import org.drools.planner.examples.travelingtournament.persistence.smart.SmartTravelingTournamentDaoImpl;

public class SimpleTravelingTournamentApp extends AbstractTravelingTournamentApp {

    public static final String SOLVER_CONFIG
            = "/org/drools/planner/examples/travelingtournament/solver/simple/simpleTravelingTournamentSolverConfig.xml";

    public static void main(String[] args) {
        new SimpleTravelingTournamentApp().init();
    }

    @Override
    protected Solver createSolver() {
        XmlSolverConfigurer configurer = new XmlSolverConfigurer();
        configurer.configure(SOLVER_CONFIG);
        return configurer.buildSolver();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new SimpleTravelingTournamentDaoImpl();
    }

}
