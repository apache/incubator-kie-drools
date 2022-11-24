package org.optaplanner.examples.pas.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleExporter;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleImporter;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleSolutionFileIO;
import org.optaplanner.examples.pas.swingui.PatientAdmissionSchedulePanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class PatientAdmissionScheduleApp extends CommonApp<PatientAdmissionSchedule> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/pas/patientAdmissionScheduleSolverConfig.xml";

    public static final String DATA_DIR_NAME = "pas";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new PatientAdmissionScheduleApp().init();
    }

    public PatientAdmissionScheduleApp() {
        super("Hospital bed planning",
                "Official competition name: PAS - Patient admission scheduling\n\n" +
                        "Assign patients to beds.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                PatientAdmissionSchedulePanel.LOGO_PATH);
    }

    @Override
    protected PatientAdmissionSchedulePanel createSolutionPanel() {
        return new PatientAdmissionSchedulePanel();
    }

    @Override
    public SolutionFileIO<PatientAdmissionSchedule> createSolutionFileIO() {
        return new PatientAdmissionScheduleSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<PatientAdmissionSchedule>> createSolutionImporters() {
        return Collections.singleton(new PatientAdmissionScheduleImporter());
    }

    @Override
    protected Set<AbstractSolutionExporter<PatientAdmissionSchedule>> createSolutionExporters() {
        return Collections.singleton(new PatientAdmissionScheduleExporter());
    }

}
