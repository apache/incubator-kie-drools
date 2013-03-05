/*
 * Copyright 2013 JBoss Inc
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

package org.drools.planner.examples.app;

import org.drools.planner.examples.cloudbalancing.persistence.CloudBalancingGenerator;
import org.drools.planner.examples.common.app.LoggingMain;
import org.drools.planner.examples.curriculumcourse.persistence.CurriculumCourseSolutionImporter;
import org.drools.planner.examples.examination.persistence.ExaminationSolutionImporter;
import org.drools.planner.examples.machinereassignment.persistence.MachineReassignmentSolutionImporter;
import org.drools.planner.examples.manners2009.persistence.Manners2009SolutionImporter;
import org.drools.planner.examples.nqueens.persistence.NQueensGenerator;
import org.drools.planner.examples.nurserostering.persistence.NurseRosteringSolutionImporter;
import org.drools.planner.examples.pas.persistence.PatientAdmissionScheduleSolutionImporter;
import org.drools.planner.examples.travelingtournament.persistence.TravelingTournamentSolutionImporter;
import org.drools.planner.examples.tsp.persistence.TspSolutionImporter;
import org.drools.planner.examples.vehiclerouting.persistence.VehicleRoutingSolutionImporter;

public class AllExamplesSolutionImporter extends LoggingMain {

    public static void main(String[] args) {
        new AllExamplesSolutionImporter().importAll();
    }

    public void importAll() {
        NQueensGenerator.main(new String[0]);
        CloudBalancingGenerator.main(new String[0]);
        TspSolutionImporter.main(new String[0]);
        Manners2009SolutionImporter.main(new String[0]);
        CurriculumCourseSolutionImporter.main(new String[0]);
        MachineReassignmentSolutionImporter.main(new String[0]);
        VehicleRoutingSolutionImporter.main(new String[0]);
        PatientAdmissionScheduleSolutionImporter.main(new String[0]);
        ExaminationSolutionImporter.main(new String[0]);
        NurseRosteringSolutionImporter.main(new String[0]);
        TravelingTournamentSolutionImporter.main(new String[0]);
    }

}
