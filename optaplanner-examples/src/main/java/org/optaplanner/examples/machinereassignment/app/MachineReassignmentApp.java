package org.optaplanner.examples.machinereassignment.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentExporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentImporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentSolutionFileIO;
import org.optaplanner.examples.machinereassignment.swingui.MachineReassignmentPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class MachineReassignmentApp extends CommonApp<MachineReassignment> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/machinereassignment/machineReassignmentSolverConfig.xml";

    public static final String DATA_DIR_NAME = "machinereassignment";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new MachineReassignmentApp().init();
    }

    public MachineReassignmentApp() {
        super("Machine reassignment",
                "Official competition name: Google ROADEF 2012 - Machine reassignment\n\n" +
                        "Reassign processes to machines.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                MachineReassignmentPanel.LOGO_PATH);
    }

    @Override
    protected MachineReassignmentPanel createSolutionPanel() {
        return new MachineReassignmentPanel();
    }

    @Override
    public SolutionFileIO<MachineReassignment> createSolutionFileIO() {
        return new MachineReassignmentSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<MachineReassignment>> createSolutionImporters() {
        return Collections.singleton(new MachineReassignmentImporter());
    }

    @Override
    protected Set<AbstractSolutionExporter<MachineReassignment>> createSolutionExporters() {
        return Collections.singleton(new MachineReassignmentExporter());
    }

}
