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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.ResourceType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ServiceDiscoveryImplTest {

    @Test
    public void testServiceAndChildServiceInSameKieConf() {
        ServiceDiscoveryImpl serviceDiscovery = new ServiceDiscoveryImpl();
        ClassLoader cl = ServiceDiscoveryImplTest.class.getClassLoader();
        serviceDiscovery.registerConfs( cl, getUrl( cl, "META-INF/kie.conf.test0" ) );

        Map<String, List<Object>> services = serviceDiscovery.getServices();
        assertTrue(services.size() == 1);

        Object service = services.get("org.kie.api.internal.assembler.KieAssemblers").get(0);
        assertNotNull(service);
        assertTrue(service instanceof MockAssemblersImpl);

        Map<ResourceType, KieAssemblerService> childServices = ((MockAssemblersImpl) service).getAssemblers();
        assertTrue(childServices.size() == 1);
        assertNotNull(childServices.get(ResourceType.DRL));
        assertTrue(childServices.get(ResourceType.DRL) instanceof MockChildAssemblerService);
    }

    @Test
    public void testDuplicatedServiceShouldFail() {
        ServiceDiscoveryImpl serviceDiscovery = new ServiceDiscoveryImpl();
        ClassLoader cl = ServiceDiscoveryImplTest.class.getClassLoader();

        try {
            serviceDiscovery.registerConfs( cl, getUrl( cl, "META-INF/kie.conf.test1" ) );
            serviceDiscovery.registerConfs( cl, getUrl( cl, "META-INF/kie.conf.test2" ) );
            serviceDiscovery.getServices();
            fail();
        } catch(Exception e) {
            // expected
        }
    }

    @Test
    public void testLoadServiceWithHighestPriority() {
        ServiceDiscoveryImpl serviceDiscovery = new ServiceDiscoveryImpl();
        ClassLoader cl = ServiceDiscoveryImplTest.class.getClassLoader();

        serviceDiscovery.registerConfs( cl, getUrl( cl, "META-INF/kie.conf.test3" ) );
        serviceDiscovery.registerConfs( cl, getUrl( cl, "META-INF/kie.conf.test1" ) );
        Map<String, List<Object>> services = serviceDiscovery.getServices();

        List<Object> service = services.get("org.kie.api.internal.assembler.KieAssemblers");
        assertNotNull(service);
        assertEquals(2, service.size());
        assertTrue(service.get(0) instanceof AnotherMockAssemblersImpl);
        assertTrue(service.get(1) instanceof MockAssemblersImpl);
    }

    protected URL getUrl( ClassLoader cl, String resourceName ) {
        try {
            return cl.getResources( resourceName ).nextElement();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }
}