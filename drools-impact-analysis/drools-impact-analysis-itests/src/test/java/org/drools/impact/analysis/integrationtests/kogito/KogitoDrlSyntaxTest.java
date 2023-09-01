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
package org.drools.impact.analysis.integrationtests.kogito;

import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.integrationtests.AbstractGraphTest;
import org.drools.impact.analysis.integrationtests.kogito.domain.LoanApplication;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

/**
 *
 * Tests related to Kogito DRL syntax: RuleUnit, OOPath with DataSource
 *
 */
public class KogitoDrlSyntaxTest extends AbstractGraphTest {

    @Test
    public void testModify() {
        String str =
                "package org.drools.impact.analysis.integrationtests.kogito;\n" +
                     "unit LoanUnit;\n" +
                     "import " + LoanApplication.class.getCanonicalName() + ";\n" +
                     "rule R1 when\n" +
                     "    $l: /loanApplications[ applicant.age >= 20, deposit < 1000, amount <= 2000 ]\n" +
                     "then\n" +
                     "    modify($l) { setApproved(true) };\n" +
                     "end\n" +
                     "\n" +
                     "rule R2 when\n" +
                     "    $l: /loanApplications[ approved ]\n" +
                     "then\n" +
                     "    System.out.println(\"APPROVED! $l : \" + $l);\n" +
                     "end";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "org.drools.impact.analysis.integrationtests.kogito.R1", "org.drools.impact.analysis.integrationtests.kogito.R2", ReactivityType.POSITIVE);
    }
}
