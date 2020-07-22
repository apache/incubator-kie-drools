/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.internal.utils;

import java.util.Collections;
import java.util.function.Function;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNRuntimeImpl;

import static org.junit.Assert.*;

public class DMNRuntimeBuilderTest {

    private static final KieBase KIE_BASE = new KnowledgeBaseImpl("DMN", null);
    private DMNRuntimeBuilder dmnRuntimeBuilder;

    @Before
    public void setup() {
        dmnRuntimeBuilder = DMNRuntimeBuilder.fromDefaults();
        assertNotNull(dmnRuntimeBuilder);
    }

    @Test
    public void setKieRuntimeFactoryFunction() {
        KieRuntimeFactory toReturn = KieRuntimeFactory.of(KIE_BASE);
        Function<String, KieRuntimeFactory> kieRuntimeFactoryFunction = s -> toReturn;
        final DMNRuntimeImpl retrieved = (DMNRuntimeImpl) dmnRuntimeBuilder
                .setKieRuntimeFactoryFunction(kieRuntimeFactoryFunction)
                .buildConfiguration()
                .fromResources(Collections.emptyList()).getOrElseThrow(RuntimeException::new);
        assertNotNull(retrieved);
        KieRuntimeFactory kieRuntimeFactory = retrieved.getKieRuntimeFactory("TEST");
        assertEquals(toReturn, kieRuntimeFactory);
    }
}