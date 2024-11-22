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
package org.drools.testcoverage.common;

import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.util.*;
import org.junit.jupiter.api.AfterEach;
import org.kie.api.io.Resource;

public abstract class KieSessionTest {

    protected Session session;
    protected TrackingAgendaEventListener firedRules;


    public void createKieSession(KieBaseTestConfiguration kieBaseTestConfiguration,
            KieSessionTestConfiguration kieSessionTestConfiguration) {
        Resource[] resources = createResources();
        if (resources == null) {
            session = KieSessionUtil.getKieSessionFromKieBaseModel(TestConstants.PACKAGE_REGRESSION,
                      kieBaseTestConfiguration, kieSessionTestConfiguration);
        } else {
            session = KieSessionUtil.getKieSessionFromKieBaseModel(TestConstants.PACKAGE_REGRESSION,
                    kieBaseTestConfiguration, kieSessionTestConfiguration, resources);
        }
        firedRules = new TrackingAgendaEventListener();
        session.addEventListener(firedRules);
    }

    @AfterEach
    public void disposeKieSession() {
        if (session != null) {
            session.dispose();
        }
    }

    protected abstract Resource[] createResources();
}
