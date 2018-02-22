/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.internal.utils;

import java.util.Map;

import org.junit.Test;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.ResourceType;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServiceDiscoveryImplTest {

    @Test
    public void testServiceAndChildServiceInSameKieConf() {
        final ServiceDiscoveryImpl serviceDiscovery = ServiceDiscoveryImpl.getInstance();
        final Map<String, Object> services = serviceDiscovery.getServices();
        assertTrue(services.size() == 1);

        final Object service = services.get("org.kie.api.internal.assembler.KieAssemblers");
        assertNotNull(service);
        assertTrue(service instanceof MockAssemblersImpl);

        final Map<ResourceType, KieAssemblerService> childServices = ((MockAssemblersImpl) service).getAssemblers();
        assertTrue(childServices.size() == 1);
        assertNotNull(childServices.get(ResourceType.DRL));
        assertTrue(childServices.get(ResourceType.DRL) instanceof MockChildAssemblerService);
    }
}