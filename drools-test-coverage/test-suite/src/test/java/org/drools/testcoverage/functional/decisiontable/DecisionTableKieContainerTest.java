package org.drools.testcoverage.functional.decisiontable;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.model.Record;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

public class DecisionTableKieContainerTest {

    @Test
    public void testCSVWithRuleTemplate() {
        final KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();
        final KieBase kieBase = kieContainer.getKieBase("csvwithtemplate");
        Assertions.assertThat(kieBase).isNotNull();

        final StatelessKieSession kieSession = kieContainer.newStatelessKieSession("csvwithtemplatesession");
        Record record1 = new Record();
        record1.setCategory("Test");
        kieSession.execute(record1);
        Assertions.assertThat(record1.getPhoneNumber()).isNotNull();

        Record record2 = new Record();
        record2.setCategory("Test2");
        kieSession.execute(record2);
        Assertions.assertThat(record2.getPhoneNumber()).isNotNull();
    }
}
