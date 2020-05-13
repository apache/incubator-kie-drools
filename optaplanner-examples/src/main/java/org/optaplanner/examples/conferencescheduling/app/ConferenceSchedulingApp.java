/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.conferencescheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;
import org.optaplanner.examples.conferencescheduling.swingui.ConferenceCFPImportAction;
import org.optaplanner.examples.conferencescheduling.swingui.ConferenceSchedulingPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ConferenceSchedulingApp extends CommonApp<ConferenceSolution> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/conferencescheduling/solver/conferenceSchedulingSolverConfig.xml";

    public static final String DATA_DIR_NAME = "conferencescheduling";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new ConferenceSchedulingApp().init();
    }

    public ConferenceSchedulingApp() {
        super("Conference scheduling",
                "Assign conference talks to a timeslot and a room.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                ConferenceSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected ConferenceSchedulingPanel createSolutionPanel() {
        return new ConferenceSchedulingPanel();
    }

    @Override
    public SolutionFileIO<ConferenceSolution> createSolutionFileIO() {
        return new ConferenceSchedulingXlsxFileIO();
    }

    @Override
    protected ExtraAction<ConferenceSolution>[] createExtraActions() {
        return new ExtraAction[] { new ConferenceCFPImportAction() };
    }
}
