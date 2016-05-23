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

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

/**
 * Tests KIE package compilation when there is a XSD resource (BZ 1120972).
 */
public class XSDResourceTest {

    /**
     * Verifies that a XSD resource on the classpath does not break KIE package compilation.
     */
    @Test
    public void testXSDResourceNotBreakingCompilation() {
        final KieContainer kcontainer = KieServices.Factory.get().getKieClasspathContainer();

        Assertions.assertThat(kcontainer.getKieBase("kbaseXsdResource"))
                .as("Created KieBase with XSD should not be null").isNotNull();
    }
}