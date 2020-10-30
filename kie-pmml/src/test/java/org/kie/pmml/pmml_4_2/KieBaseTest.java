/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.pmml_4_2;

import org.junit.Test;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.io.ResourceType;
import org.kie.internal.services.KieAssemblersImpl;
import org.kie.pmml.assembler.PMMLAssemblerService;

import static org.junit.Assert.assertNotNull;

public class KieBaseTest {

    @Test
    public void testKieBaseCompilation() {
        KieAssemblersImpl kieAssemblers = (KieAssemblersImpl) ServiceRegistry.getService(KieAssemblers.class);
        PMMLAssemblerService assembler = (PMMLAssemblerService)kieAssemblers.getAssemblers().get(ResourceType.PMML);
        assertNotNull(assembler);
    }
}
