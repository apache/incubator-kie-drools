/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests stateful/stateless KieSession listeners registration - DROOLS-818.
 */
public class ListenersTest {

    private static final ReleaseId RELEASE_ID = KieServices.Factory.get()
            .newReleaseId("org.drools.compiler.test", "listeners-test", "1.0.0");

    private static final String PACKAGE = ListenersTest.class.getPackage().getName();
    private static final String PACKAGE_PATH = PACKAGE.replaceAll("\\.", "/");

    private static final String DRL =
            "import java.util.Collection\n"
                    + "rule R1 when\n"
                    + " String()\n"
                    + "then\n"
                    + "end\n";

    private KieServices ks = KieServices.Factory.get();
    private KieSession kieSession;
    private StatelessKieSession statelessKieSession;

    @Before
    public void init() {
        ReleaseId kieModuleId = prepareKieModule();

        final KieContainer kieContainer = ks.newKieContainer(kieModuleId);
        this.kieSession = kieContainer.newKieSession();
        this.statelessKieSession = kieContainer.newStatelessKieSession();
    }

    @After
    public void cleanup() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
        this.statelessKieSession = null;
    }

    @Test
    public void testRegisterAgendaEventListenerStateful() throws Exception {
        kieSession.insert("test");
        kieSession.fireAllRules();
        checkThatListenerFired(kieSession.getAgendaEventListeners());
    }

    @Test
    public void testRegisterRuleRuntimeEventListenerStateful() throws Exception {
        kieSession.insert("test");
        kieSession.fireAllRules();
        checkThatListenerFired(kieSession.getRuleRuntimeEventListeners());
    }

    @Test
    public void testRegisterAgendaEventListenerStateless() throws Exception {
        statelessKieSession.execute(KieServices.Factory.get().getCommands().newInsert("test"));
        checkThatListenerFired(statelessKieSession.getAgendaEventListeners());
    }

    @Test
    public void testRegisterRuleEventListenerStateless() throws Exception {
        statelessKieSession.execute(KieServices.Factory.get().getCommands().newInsert("test"));
        checkThatListenerFired(statelessKieSession.getRuleRuntimeEventListeners());
    }

    private void checkThatListenerFired(Collection listeners) {
        assertTrue("Listener not registered.", listeners.size() >= 1);
        MarkingListener listener = getMarkingListener(listeners);
        assertTrue("Expected listener to fire.", listener.hasFired());
    }

    private MarkingListener getMarkingListener(Collection listeners) {
        for (Object listener : listeners) {
            if (listener instanceof MarkingListener) {
                return (MarkingListener) listener;
            }
        }
        throw new IllegalArgumentException("Expected at least one MarkingListener in the collection");
    }

    /**
     * Inserts a new KieModule containing single KieBase and a stateful and stateless KieSessions with listeners
     * into KieRepository.
     *
     * @return created KIE module ReleaseId
     */
    private ReleaseId prepareKieModule() {
        final KieServices ks = KieServices.Factory.get();

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel baseModel = module.newKieBaseModel("defaultKBase");
        baseModel.setDefault(true);
        baseModel.addPackage("*");

        KieSessionModel sessionModel = baseModel.newKieSessionModel("defaultKSession");
        sessionModel.setDefault(true);
        sessionModel.setType(KieSessionModel.KieSessionType.STATEFUL);
        sessionModel.newListenerModel(MarkingAgendaEventListener.class.getName(), ListenerModel.Kind.AGENDA_EVENT_LISTENER);
        sessionModel.newListenerModel(MarkingRuntimeEventListener.class.getName(), ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER);

        KieSessionModel statelessSessionModel = baseModel.newKieSessionModel("defaultStatelessKSession");
        statelessSessionModel.setDefault(true);
        statelessSessionModel.setType(KieSessionModel.KieSessionType.STATELESS);
        statelessSessionModel.newListenerModel(MarkingAgendaEventListener.class.getName(), ListenerModel.Kind.AGENDA_EVENT_LISTENER);
        statelessSessionModel.newListenerModel(MarkingRuntimeEventListener.class.getName(), ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER);

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(module.toXML());
        kfs.generateAndWritePomXML(RELEASE_ID);

        kfs.write("src/main/resources/" + PACKAGE_PATH + "/test.drl",
                ResourceFactory.newByteArrayResource(DRL.getBytes()));

        KieBuilder builder = ks.newKieBuilder(kfs).buildAll();
        assertEquals("Unexpected compilation errors", 0, builder.getResults().getMessages().size());

        ks.getRepository().addKieModule(builder.getKieModule());

        return RELEASE_ID;
    }

    /**
     * Listener which just marks that it had fired.
     */
    public interface MarkingListener {

        boolean hasFired();
    }

    /**
     * A listener marking that an AgendaEvent has fired.
     */
    public static class MarkingAgendaEventListener extends DefaultAgendaEventListener implements MarkingListener {

        private final AtomicBoolean fired = new AtomicBoolean(false);

        @Override
        public void afterMatchFired(final AfterMatchFiredEvent event) {
            super.afterMatchFired(event);
            this.fired.compareAndSet(false, true);
        }

        public boolean hasFired() {
            return this.fired.get();
        }
    }

    /**
     * A listener marking that a RuleRuntimeEvent has fired.
     */
    public static class MarkingRuntimeEventListener extends DefaultRuleRuntimeEventListener implements MarkingListener {

        private final AtomicBoolean fired = new AtomicBoolean(false);

        @Override
        public void objectInserted(final ObjectInsertedEvent event) {
            super.objectInserted(event);
            this.fired.compareAndSet(false, true);
        }

        public boolean hasFired() {
            return this.fired.get();
        }
    }

}