/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests;

import org.assertj.core.api.Assertions;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

/**
 * Tests KIE package compilation when there is a XSD resource (BZ 1120972) - manifests only when using
 * KieClasspathContainer.
 */
public class XSDResourceTest extends CommonTestMethodBase {

    @Test
    public void testXSDResourceNotBreakingCompilation() {
        final KieContainer kcontainer = KieServices.Factory.get().getKieClasspathContainer();
        final KieBase kieBase = kcontainer.getKieBase("xsdKieBase");

        Assertions.assertThat(kieBase).as("Created KieBase with XSD should not be null").isNotNull();
    }

}
