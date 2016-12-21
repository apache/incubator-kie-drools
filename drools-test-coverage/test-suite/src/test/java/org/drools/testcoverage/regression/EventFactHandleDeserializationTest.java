package org.drools.testcoverage.regression;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reproducer for BZ 1264525.
 */
public class EventFactHandleDeserializationTest {

    @Test
    public void testDisconnectedEventFactHandle() {
        // DROOLS-924
        String drl =
                "declare String \n" +
                        "  @role(event)\n" +
                        "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();

        DefaultFactHandle helloHandle = (DefaultFactHandle) ksession.insert("hello");
        DefaultFactHandle goodbyeHandle = (DefaultFactHandle) ksession.insert("goodbye");

        FactHandle key = DefaultFactHandle.createFromExternalFormat(helloHandle.toExternalForm());
        assertThat(key).isInstanceOf(EventFactHandle.class);
        assertThat(ksession.getObject(key)).isEqualTo("hello");

        key = DefaultFactHandle.createFromExternalFormat(goodbyeHandle.toExternalForm());
        assertThat(key).isInstanceOf(EventFactHandle.class);
        assertThat(ksession.getObject(key)).isEqualTo("goodbye");
    }
}
