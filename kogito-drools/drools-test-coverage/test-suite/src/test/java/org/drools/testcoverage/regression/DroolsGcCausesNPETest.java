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

import org.assertj.core.api.Assertions;
import org.drools.compiler.TurtleTestCategory;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Reproducer for BZ 1181584, by Mike Wilson.
 */
public class DroolsGcCausesNPETest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsGcCausesNPETest.class);

    private static final String KIE_BASE_NAME = "defaultBase";
    private static final String DRL_FILE_NAME = "DroolsGcCausesNPE.drl";

    private static final KieServices SERVICES = KieServices.Factory.get();

    private static final ReleaseId RELEASE_ID = SERVICES.newReleaseId(
            "org.drools.testcoverage", "drools-gc-causes-npe-example", "1.0");

    private KieSession session;
    private SessionPseudoClock clock;
    private FactType eventFactType;

    @BeforeClass
    public static void beforeClass() throws Exception {

        final KieModuleModel module = SERVICES.newKieModuleModel();
        final KieBaseModel base = module.newKieBaseModel(KIE_BASE_NAME);
        base.setEventProcessingMode(EventProcessingOption.STREAM);

        final KieFileSystem fs = SERVICES.newKieFileSystem();
        fs.generateAndWritePomXML(RELEASE_ID);
        fs.write(SERVICES.getResources()
                .newClassPathResource(DRL_FILE_NAME, DroolsGcCausesNPETest.class));
        fs.writeKModuleXML(module.toXML());

        final KieBuilder builder = SERVICES.newKieBuilder(fs);
        final List<Message> errors = builder.buildAll().getResults()
                .getMessages(Message.Level.ERROR);

        Assertions.assertThat(errors).as("Unexpected errors building drl: " + errors).isEmpty();

        SERVICES.getRepository().addKieModule(builder.getKieModule());
    }

    @Before
    public void setUp() throws Exception {
        final KieSessionConfiguration conf = SERVICES.newKieSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));
        conf.setProperty("type", "stateful");
        final KieContainer container = SERVICES.newKieContainer(RELEASE_ID);
        session = container.getKieBase(KIE_BASE_NAME).newKieSession(conf,
                SERVICES.newEnvironment());
        clock = session.getSessionClock();
        eventFactType = session.getKieBase().getFactType(this.getClass().getPackage().getName(), "Event");
    }

    /**
     * The original test method reproducing NPE during event GC.
     * BZ 1181584
     */
    @Test
    @Category(TurtleTestCategory.class)
    public void testMoreTimesRepeated() throws Exception {
        final Random r = new Random(1);
        int i = 0;
        try {
            for (; i < 100000; i++) {
                insertAndAdvanceTime(i, r.nextInt(4000));
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
    public void test() throws Exception {
        insertAndAdvanceTime(1, 4000);
    }

    private void insertAndAdvanceTime(final long id, final long millis) throws IllegalAccessException, InstantiationException {
        insert(createEvent(id));
        advanceTime(millis);
    }

    private Object createEvent(final long id) throws IllegalAccessException, InstantiationException {
        final Object event = eventFactType.newInstance();
        eventFactType.set(event, "id", id);
        return event;
    }

    private void advanceTime(final long millis) {
        clock.advanceTime(millis, TimeUnit.MILLISECONDS);
        session.fireAllRules();
    }

    private void insert(final Object event) {
        session.insert(event);
        session.fireAllRules();
    }

    private void logActiveFacts() {
        LOGGER.warn("facts: ");
        session.getFactHandles().stream().map(Object::toString).forEach(LOGGER::warn);
    }
}
