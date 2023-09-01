package org.drools.testcoverage.regression;

import org.assertj.core.api.SoftAssertions;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DefaultEventHandle;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;


/**
 * Reproducer for BZ 1264525.
 */
public class EventFactHandleDeserializationTest {

    @Test
    public void testDisconnectedEventFactHandle() {
        // DROOLS-924
        final String drl =
                "declare String \n" +
                "  @role(event)\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();

        final DefaultFactHandle helloHandle = (DefaultFactHandle) ksession.insert("hello");
        final DefaultFactHandle goodbyeHandle = (DefaultFactHandle) ksession.insert("goodbye");

        final SoftAssertions softly = new SoftAssertions();

        FactHandle key = DefaultFactHandle.createFromExternalFormat(helloHandle.toExternalForm());
        softly.assertThat(key).isInstanceOf(DefaultEventHandle.class);
        softly.assertThat(ksession.getObject(key)).isEqualTo("hello");

        key = DefaultFactHandle.createFromExternalFormat(goodbyeHandle.toExternalForm());
        softly.assertThat(key).isInstanceOf(DefaultEventHandle.class);
        softly.assertThat(ksession.getObject(key)).isEqualTo("goodbye");

        softly.assertAll();
    }
}
