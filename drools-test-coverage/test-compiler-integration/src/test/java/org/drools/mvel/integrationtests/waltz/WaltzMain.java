package org.drools.mvel.integrationtests.waltz;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;

public class WaltzMain extends ReteOOWaltzTest {

    public WaltzMain(KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    public static final void main(String[] args) {
        new WaltzMain(KieBaseTestConfiguration.CLOUD_IDENTITY).testWaltz();
    }
}
