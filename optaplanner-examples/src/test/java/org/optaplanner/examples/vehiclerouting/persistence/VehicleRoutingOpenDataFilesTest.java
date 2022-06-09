package org.optaplanner.examples.vehiclerouting.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.vehiclerouting.app.VehicleRoutingApp;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

class VehicleRoutingOpenDataFilesTest extends OpenDataFilesTest<VehicleRoutingSolution> {

    @Override
    protected CommonApp<VehicleRoutingSolution> createCommonApp() {
        return new VehicleRoutingApp();
    }
}
