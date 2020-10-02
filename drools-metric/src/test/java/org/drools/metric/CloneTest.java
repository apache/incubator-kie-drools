/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.metric;

import org.drools.metric.util.MetricLogUtils;
import org.drools.mvel.CommonTestMethodBase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class CloneTest extends CommonTestMethodBase {

    @Before
    public void setup() {
        System.setProperty(MetricLogUtils.METRIC_LOGGER_ENABLED, "true");
        System.setProperty(MetricLogUtils.METRIC_LOGGER_THRESHOLD, "-1");
    }

    @Test
    public void testComplexEval() throws Exception {
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
        assertEquals(1, kieSession.fireAllRules());
    }
}
