/*
 * Copyright (c) 2022. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.nomvel;

import org.drools.testcoverage.common.model.Person;
import org.junit.Test;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.compiler.integrationtests.nomvel.TestUtil.getKieContainer;

public class VerifyTest {

    @Test
    public void verify_kieContainer_executableModel() throws Exception {
        String drl =
                "import " + Person.class.getCanonicalName() + " \n" +
                     "rule R1\n" +
                     "when\n" +
                     " Person()\n" +
                     "then\n" +
                     "end";

        final KieContainer kieContainer = getKieContainer(null, drl);

        Results results = kieContainer.verify();
        assertThat(results.getMessages().size()).isEqualTo(0);

        Results resultsWithKieBaseName = kieContainer.verify("defaultKieBase");
        assertThat(resultsWithKieBaseName.getMessages().size()).isEqualTo(0);
    }
}
