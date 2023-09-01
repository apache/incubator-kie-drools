package org.drools.testcoverage.common;

import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.util.*;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.io.Resource;

@RunWith(Parameterized.class)
public abstract class KieSessionTest {

    protected final KieBaseTestConfiguration kieBaseTestConfiguration;
    protected final KieSessionTestConfiguration kieSessionTestConfiguration;

    protected Session session;
    protected TrackingAgendaEventListener firedRules;

    public KieSessionTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                          final KieSessionTestConfiguration kieSessionTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
        this.kieSessionTestConfiguration = kieSessionTestConfiguration;
    }

    @Before
    public void createKieSession() {
        Resource[] resources = createResources();
        if (resources == null) {
            session = KieSessionUtil.getKieSessionFromKieBaseModel(TestConstants.PACKAGE_REGRESSION,
                      kieBaseTestConfiguration, kieSessionTestConfiguration);
        } else {
            session = KieSessionUtil.getKieSessionFromKieBaseModel(TestConstants.PACKAGE_REGRESSION,
                    kieBaseTestConfiguration, kieSessionTestConfiguration, resources);
        }
        firedRules = new TrackingAgendaEventListener();
        session.addEventListener(firedRules);
    }

    @After
    public void disposeKieSession() {
        if (session != null) {
            session.dispose();
        }
    }

    protected abstract Resource[] createResources();
}
