package org.drools.vsm;

import org.drools.vsm.local.ServiceManagerLocalClient;

public class ServiceManagerLocalTest extends ServiceManagerTestBase {

    protected void setUp() throws Exception {
        this.client = new ServiceManagerLocalClient();
    }

    protected void tearDown() throws Exception {
    }

}
