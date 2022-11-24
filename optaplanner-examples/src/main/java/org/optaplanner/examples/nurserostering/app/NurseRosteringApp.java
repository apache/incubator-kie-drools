package org.optaplanner.examples.nurserostering.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.persistence.NurseRosterSolutionFileIO;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringExporter;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringImporter;
import org.optaplanner.examples.nurserostering.swingui.NurseRosteringPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class NurseRosteringApp extends CommonApp<NurseRoster> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/nurserostering/nurseRosteringSolverConfig.xml";

    public static final String DATA_DIR_NAME = "nurserostering";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new NurseRosteringApp().init();
    }

    public NurseRosteringApp() {
        super("Nurse rostering",
                "Official competition name: INRC2010 - Nurse rostering\n\n" +
                        "Assign shifts to nurses.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                NurseRosteringPanel.LOGO_PATH);
    }

    @Override
    protected NurseRosteringPanel createSolutionPanel() {
        return new NurseRosteringPanel();
    }

    @Override
    public SolutionFileIO<NurseRoster> createSolutionFileIO() {
        return new NurseRosterSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<NurseRoster>> createSolutionImporters() {
        return Collections.singleton(new NurseRosteringImporter());
    }

    @Override
    protected Set<AbstractSolutionExporter<NurseRoster>> createSolutionExporters() {
        return Collections.singleton(new NurseRosteringExporter());
    }

}
