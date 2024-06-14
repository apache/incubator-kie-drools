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
package org.kie.efesto.runtimemanager.core.service;

import java.util.List;

import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeServiceProvider;

import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getLocalDiscoveredKieRuntimeServices;

/**
 * This is the default, JVM-local, implementation of the communication layer abstraction API, to be invoked internally by the framework.
 */
public class LocalRuntimeServiceProviderImpl implements RuntimeServiceProvider {

    @Override
    public List<KieRuntimeService> getKieRuntimeServices() {
        return getLocalDiscoveredKieRuntimeServices();
    }

}
