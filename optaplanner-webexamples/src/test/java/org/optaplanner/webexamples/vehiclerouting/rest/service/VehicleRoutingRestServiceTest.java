/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.webexamples.vehiclerouting.rest.service;

import java.io.IOException;
import java.net.URL;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.webexamples.common.rest.service.AbstractClientArquillianTest;
import org.optaplanner.webexamples.vehiclerouting.rest.domain.JsonVehicleRoutingSolution;

import static org.junit.Assert.*;

public class VehicleRoutingRestServiceTest extends AbstractClientArquillianTest {

    @Test @Ignore // TODO Fix arquillian/shrinkwrap/maven/aether dependency mess https://issues.jboss.org/browse/PLANNER-445
    public void getSolutions(@ArquillianResource URL baseUrl) throws IOException {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        VehicleRoutingRestService proxy
                = ProxyFactory.create(VehicleRoutingRestService.class, baseUrl.toExternalForm() + "rest/vehiclerouting/solution");
        JsonVehicleRoutingSolution solution = proxy.getSolution();
        assertNotNull(solution);
    }

}
