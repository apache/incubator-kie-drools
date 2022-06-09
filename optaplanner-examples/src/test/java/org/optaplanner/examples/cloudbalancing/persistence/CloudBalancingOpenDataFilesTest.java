package org.optaplanner.examples.cloudbalancing.persistence;

import org.optaplanner.examples.cloudbalancing.app.CloudBalancingApp;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;

class CloudBalancingOpenDataFilesTest extends OpenDataFilesTest<CloudBalance> {

    @Override
    protected CommonApp<CloudBalance> createCommonApp() {
        return new CloudBalancingApp();
    }
}
