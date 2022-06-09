package org.optaplanner.examples.app;

import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingGenerator;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseImporter;
import org.optaplanner.examples.examination.persistence.ExaminationImporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentImporter;
import org.optaplanner.examples.nqueens.persistence.NQueensGenerator;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringImporter;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleImporter;
import org.optaplanner.examples.projectjobscheduling.persistence.ProjectJobSchedulingImporter;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentImporter;
import org.optaplanner.examples.tsp.persistence.TspImporter;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingImporter;

public class AllExamplesSolutionImporter extends LoggingMain {

    public static void main(String[] args) {
        new AllExamplesSolutionImporter().importAll();
    }

    public void importAll() {
        NQueensGenerator.main(new String[0]);
        CloudBalancingGenerator.main(new String[0]);
        TspImporter.main(new String[0]);
        CurriculumCourseImporter.main(new String[0]);
        MachineReassignmentImporter.main(new String[0]);
        VehicleRoutingImporter.main(new String[0]);
        ProjectJobSchedulingImporter.main(new String[0]);
        PatientAdmissionScheduleImporter.main(new String[0]);
        ExaminationImporter.main(new String[0]);
        NurseRosteringImporter.main(new String[0]);
        TravelingTournamentImporter.main(new String[0]);
    }

}
