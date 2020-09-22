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

package org.optaplanner.examples.tennis.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.persistence.TennisXmlSolutionFileIO;
import org.optaplanner.examples.tennis.swingui.TennisPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class TennisApp extends CommonApp<TennisSolution> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/tennis/solver/tennisSolverConfig.xml";

    public static final String DATA_DIR_NAME = "tennis";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TennisApp().init();
    }

    public TennisApp() {
        super("Tennis club scheduling",
                "Assign available spots to teams.\n\n" +
                        "Each team must play an almost equal number of times.\n" +
                        "Each team must play against each other team an almost equal number of times.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                TennisPanel.LOGO_PATH);
    }

    @Override
    protected TennisPanel createSolutionPanel() {
        return new TennisPanel();
    }

    @Override
    public SolutionFileIO<TennisSolution> createSolutionFileIO() {
        return new TennisXmlSolutionFileIO();
    }

}
