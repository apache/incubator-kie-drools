/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common;

import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.util.*;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.io.Resource;

@RunWith(Parameterized.class)
public abstract class KieSessionTest {

    protected final KieBaseTestConfiguration kieBaseTestConfiguration;
    protected final KieSessionTestConfiguration kieSessionTestConfiguration;

    protected Session session;
    protected TrackingAgendaEventListener firedRules;

    public KieSessionTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                          final KieSessionTestConfiguration kieSessionTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
        this.kieSessionTestConfiguration = kieSessionTestConfiguration;
    }

    @Before
    public void createKieSession() {
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

    @After
    public void disposeKieSession() {
        if (session != null) {
            session.dispose();
        }
    }

    protected abstract Resource[] createResources();
}
