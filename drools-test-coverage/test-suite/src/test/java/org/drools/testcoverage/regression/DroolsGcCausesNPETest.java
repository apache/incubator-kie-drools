/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.regression;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.drools.core.time.SessionPseudoClock;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reproducer for BZ 1181584, by Mike Wilson.
 */
public class DroolsGcCausesNPETest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsGcCausesNPETest.class);

    private static final String KIE_BASE_NAME = "defaultBase";
    private static final String DRL_FILE_NAME = "DroolsGcCausesNPE.drl";

    private static final KieServices services = KieServices.Factory.get();

    private static final ReleaseId RELEASE_ID = services.newReleaseId(
            TestConstants.PACKAGE_TESTCOVERAGE, "drools-gc-causes-npe-example", "1.0");

    private KieSession session;
    private SessionPseudoClock clock;
    private FactType eventFactType;

    @BeforeClass
    public static void beforeClass() throws Exception {

        final KieModuleModel module = services.newKieModuleModel();
        final KieBaseModel base = module.newKieBaseModel(KIE_BASE_NAME);
        base.setEventProcessingMode(EventProcessingOption.STREAM);

        final Resource fileSystemResource =
                services.getResources().newClassPathResource(DRL_FILE_NAME, DroolsGcCausesNPETest.class);
        final KieFileSystem fileSystem = KieBaseUtil.writeKieModuleWithResourceToFileSystem(module, RELEASE_ID,
                fileSystemResource);

        final KieBuilder builder = KieBaseUtil.getKieBuilderFromKieFileSystem(fileSystem, true);

        services.getRepository().addKieModule(builder.getKieModule());
    }

    @Before
    public void setUp() throws Exception {
        final KieSessionConfiguration conf =
                KieSessionUtil.getKieSessionConfigurationWithClock(ClockTypeOption.get("pseudo"), getSessionProperties());

        session = KieBaseUtil.getKieBaseFromReleaseIdByName(RELEASE_ID, KIE_BASE_NAME).newKieSession(conf,
                KieServices.Factory.get().newEnvironment());
        clock = session.getSessionClock();
        eventFactType = session.getKieBase().getFactType(this.getClass().getPackage().getName(), "Event");
    }

    /**
     * The original test method reproducing NPE during event GC.
     */
    @Test
    public void testBZ1181584() throws Exception {
        final Random r = new Random(1);

        int i = 0;

        try {
            for (; i < 1000000; i++) {
                insertAndFire(createEvent(1));
                advanceTimeAndFire(r.nextInt(4000));
            }
        } catch (NullPointerException e) {
            LOGGER.warn("failed at i = " + i);
            LOGGER.warn("fact count: " + session.getFactCount());
            logActiveFacts();
            Assertions.fail("NPE thrown - consider reopening BZ 1181584", e);
        }
    }

    /**
     * Deterministic variant of the previous test method that reliably illustrates BZ 1274696.
     */
    @Test
    public void testBZ1274696() throws Exception {
        insertAndFire(createEvent(1));
        advanceTimeAndFire(4000);
    }

    private Object createEvent(long id) throws InstantiationException,
            IllegalAccessException {
        final Object event = eventFactType.newInstance();
        eventFactType.set(event, "id", id);
        return event;

    }

    private void advanceTimeAndFire(long millis) {
        clock.advanceTime(millis, TimeUnit.MILLISECONDS);
        session.fireAllRules();
    }

    private void insertAndFire(Object event) {
        session.insert(event);
        session.fireAllRules();
    }

    private void logActiveFacts() {
        LOGGER.warn("facts: ");
        for (FactHandle handle : session.getFactHandles()) {
            LOGGER.warn(handle.toString());
        }
    }

    private Properties getSessionProperties() {
        final Properties sessionProperties = new Properties();
        sessionProperties.put("type", "stateful");
        return sessionProperties;
    }

}
