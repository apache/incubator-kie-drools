/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.scrabble.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.scrabble.domain.ScrabbleSolution;
import org.optaplanner.examples.scrabble.persistence.ScrabbleDao;
import org.optaplanner.examples.scrabble.swingui.ScrabblePanel;

public class ScrabbleApp extends CommonApp<ScrabbleSolution> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/scrabble/solver/scrabbleSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new ScrabbleApp().init();
    }

    public ScrabbleApp() {
        super("Scrabble compacter",
                "Assign words to a scrabble board as compact as possible.",
                SOLVER_CONFIG,
                ScrabblePanel.LOGO_PATH);
    }

    @Override
    protected ScrabblePanel createSolutionPanel() {
        return new ScrabblePanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new ScrabbleDao();
    }

}
