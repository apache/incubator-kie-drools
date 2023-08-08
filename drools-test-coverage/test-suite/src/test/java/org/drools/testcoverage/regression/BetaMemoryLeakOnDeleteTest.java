/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        final String drl =
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

        final KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();

        final FactHandle fh1 = ksession.insert(1);
        final FactHandle fh2 = ksession.insert("test");
        ksession.fireAllRules();
        ksession.delete(fh1);
        ksession.delete(fh2);
        ksession.fireAllRules();

        final NodeMemories nodeMemories = ((InternalWorkingMemory) ksession).getNodeMemories();

        for (int i = 0; i < nodeMemories.length(); i++) {
            final Memory memory = nodeMemories.peekNodeMemory(i);
            if (memory != null && memory.getSegmentMemory() != null) {
                final LeftTuple deleteFirst = memory.getSegmentMemory().getStagedLeftTuples().getDeleteFirst();
                assertThat(deleteFirst).isNull();
            }
        }
    }
}
