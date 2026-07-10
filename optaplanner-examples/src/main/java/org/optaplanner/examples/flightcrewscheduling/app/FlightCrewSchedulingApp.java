/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.flightcrewscheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.persistence.FlightCrewSchedulingXlsxFileIO;
import org.optaplanner.examples.flightcrewscheduling.swingui.FlightCrewSchedulingPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class FlightCrewSchedulingApp extends CommonApp<FlightCrewSolution> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/flightcrewscheduling/flightCrewSchedulingSolverConfig.xml";

    public static final String DATA_DIR_NAME = "flightcrewscheduling";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new FlightCrewSchedulingApp().init();
    }

    public FlightCrewSchedulingApp() {
        super("Flight crew scheduling",
                "Assign flights to pilots and flight attendants.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                FlightCrewSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected FlightCrewSchedulingPanel createSolutionPanel() {
        return new FlightCrewSchedulingPanel();
    }

    @Override
    public SolutionFileIO<FlightCrewSolution> createSolutionFileIO() {
        return new FlightCrewSchedulingXlsxFileIO();
    }

}
