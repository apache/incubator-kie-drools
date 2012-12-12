package org.drools.kproject;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.KieServices;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;

public class KProjectTest {

    @Test @Ignore
    public void testKJar() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession("FireAlarmKBase.session");
    }

    private void useKSession(KieSession ksession) throws InstantiationException, IllegalAccessException {

        int rules = ksession.fireAllRules();
    }

}
