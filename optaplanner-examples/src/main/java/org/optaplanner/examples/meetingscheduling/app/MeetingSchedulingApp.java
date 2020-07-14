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

package org.optaplanner.examples.meetingscheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.examples.meetingscheduling.swingui.MeetingSchedulingPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class MeetingSchedulingApp extends CommonApp<MeetingSchedule> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/meetingscheduling/solver/meetingSchedulingSolverConfig.xml";

    public static final String DATA_DIR_NAME = "meetingscheduling";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new MeetingSchedulingApp().init();
    }

    public MeetingSchedulingApp() {
        super("Meeting scheduling",
                "Assign meetings a starting time and a room.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                MeetingSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected MeetingSchedulingPanel createSolutionPanel() {
        return new MeetingSchedulingPanel();
    }

    @Override
    public SolutionFileIO<MeetingSchedule> createSolutionFileIO() {
        return new MeetingSchedulingXlsxFileIO();
    }

}
