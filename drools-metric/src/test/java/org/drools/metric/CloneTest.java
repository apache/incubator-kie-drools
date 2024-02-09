/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.metric;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class CloneTest extends AbstractMetricTest {

    @Test
    public void testComplexEval() {
        String drl =
                "rule R1 when\n" +
                     "    $s : String()\n" +
                     "    Integer()\n" +
                     "    not( ( eval($s.length() < 2) and (eval(true) or eval(false))))\n" +
                     "then \n" +
                     "end\n";

        KieSession kieSession = new KieHelper().addContent(drl, ResourceType.DRL)
                                               .build().newKieSession();

        kieSession.insert(42);
        kieSession.insert("test");
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }
}
