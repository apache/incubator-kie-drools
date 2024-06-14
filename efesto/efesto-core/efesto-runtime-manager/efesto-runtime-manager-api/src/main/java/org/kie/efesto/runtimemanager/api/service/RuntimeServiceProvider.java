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
package org.kie.efesto.runtimemanager.api.service;

import java.util.List;

/**
 * This is the communication layer abstraction API, to be invoked internally by the framework.
 * The default implementation uses the KieServices found, by SPI, in the JVM where the  Efesto instance is running.
 */
public interface RuntimeServiceProvider {

    /**
     * Return all the <code>KieRuntimeService</code>s exposed by the actual implementation
     * @return
     */
    List<KieRuntimeService> getKieRuntimeServices();

}
