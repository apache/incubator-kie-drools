package org.optaplanner.examples.vehiclerouting.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.vehiclerouting.app.VehicleRoutingApp;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

class VehicleRoutingImportDataFilesTest extends ImportDataFilesTest<VehicleRoutingSolution> {

    @Override
    protected AbstractSolutionImporter<VehicleRoutingSolution> createSolutionImporter() {
        return new VehicleRoutingImporter();
    }

    @Override
    protected String getDataDirName() {
        return VehicleRoutingApp.DATA_DIR_NAME;
    }
}
