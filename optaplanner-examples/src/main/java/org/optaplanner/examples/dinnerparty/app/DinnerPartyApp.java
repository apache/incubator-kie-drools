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

package org.optaplanner.examples.dinnerparty.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.dinnerparty.domain.DinnerParty;
import org.optaplanner.examples.dinnerparty.persistence.DinnerPartyDao;
import org.optaplanner.examples.dinnerparty.persistence.DinnerPartyImporter;
import org.optaplanner.examples.dinnerparty.swingui.DinnerPartyPanel;

public class DinnerPartyApp extends CommonApp<DinnerParty> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/dinnerparty/solver/dinnerPartySolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new DinnerPartyApp().init();
    }

    public DinnerPartyApp() {
        super("Dinner party",
                "Decide the seating at a big fancy dinner party with round tables.\n" +
                        "Assign guests to seats at tables.",
                SOLVER_CONFIG,
                DinnerPartyPanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new DinnerPartyPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new DinnerPartyDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new DinnerPartyImporter()
        };
    }

}
