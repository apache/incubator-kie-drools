package org.drools.testcoverage.regression;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.NodeMemories;
import org.drools.core.reteoo.LeftTuple;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Taken from DROOLS-913.
 */
public class BetaMemoryLeakOnDeleteTest {

    @Test
    public void testBetaMemoryLeakOnFactDelete() {
        String drl =
                "rule R1 when\n" +
                        "    $a : Integer(this == 1)\n" +
                        "    $b : String()\n" +
                        "    $c : Integer(this == 2)\n" +
                        "then \n" +
                        "end\n" +

                        "rule R2 when\n" +
                        "    $a : Integer(this == 1)\n" +
                        "    $b : String()\n" +
                        "then \n" +
                        "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();

        FactHandle fh1 = ksession.insert(1);
        FactHandle fh2 = ksession.insert("test");

        ksession.fireAllRules();

        ksession.delete(fh1);
        ksession.delete(fh2);

        ksession.fireAllRules();

        NodeMemories nodeMemories = ((InternalWorkingMemory) ksession).getNodeMemories();

        for (int i = 0; i < nodeMemories.length(); i++) {
            Memory memory = nodeMemories.peekNodeMemory(i);

            if (memory != null && memory.getSegmentMemory() != null) {
                LeftTuple deleteFirst = memory.getSegmentMemory().getStagedLeftTuples().getDeleteFirst();

                assertThat(deleteFirst).isNull();
            }
        }
    }
}
