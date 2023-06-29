/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.h2mvstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.drools.core.ClassObjectFilter;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.reliability.core.ReliableKieSession;
import org.drools.reliability.core.ReliableRuntimeComponentFactoryImpl;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.TestableStorageManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.Option;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(BeforeAllMethodExtension.class)
public abstract class ReliabilityTestBasics {

    private static final Logger LOG = LoggerFactory.getLogger(ReliabilityTestBasics.class);

    protected final List<KieSession> sessions = new ArrayList<>();

    private long persistedSessionId = -1;

    protected PersistedSessionOption.SafepointStrategy safepointStrategy;

    static Stream<Arguments> strategyProviderStoresOnly() {
        return Stream.of(arguments(PersistedSessionOption.PersistenceStrategy.STORES_ONLY, PersistedSessionOption.SafepointStrategy.ALWAYS));
    }

    static Stream<Arguments> strategyProviderStoresOnlyWithExplicitSafepoints() {
        return Stream.of(
                arguments(PersistedSessionOption.PersistenceStrategy.STORES_ONLY, PersistedSessionOption.SafepointStrategy.ALWAYS),
                arguments(PersistedSessionOption.PersistenceStrategy.STORES_ONLY, PersistedSessionOption.SafepointStrategy.EXPLICIT)
        );
    }

    static Stream<Arguments> strategyProviderStoresOnlyWithAllSafepoints() {
        return Stream.of(
                arguments(PersistedSessionOption.PersistenceStrategy.STORES_ONLY, PersistedSessionOption.SafepointStrategy.ALWAYS),
                arguments(PersistedSessionOption.PersistenceStrategy.STORES_ONLY, PersistedSessionOption.SafepointStrategy.EXPLICIT),
                arguments(PersistedSessionOption.PersistenceStrategy.STORES_ONLY, PersistedSessionOption.SafepointStrategy.AFTER_FIRE)
        );
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        // clean up database so that test methods can be isolated
        ((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).restartWithCleanUp();
        ReliableRuntimeComponentFactoryImpl.resetCounter();
    }

    public void failover() {
        if (safepointStrategy == PersistedSessionOption.SafepointStrategy.EXPLICIT) {
            this.sessions.stream().map(ReliableKieSession.class::cast).forEach(ReliableKieSession::safepoint);
        }
        sessions.clear();

        ((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).restart(); // restart database. storage is kept
        ReliableRuntimeComponentFactoryImpl.refreshCounterUsingStorage();
    }

    protected FactHandle insert(Object obj) {
        return insert(sessions.get(0), obj);
    }

    protected FactHandle insert(KieSession session, Object obj) {
        return session.insert(obj);
    }

    protected void update(FactHandle fh, Object obj) {
        update(sessions.get(0), fh, obj);
    }

    protected void update(KieSession session, FactHandle fh, Object obj) {
        session.update(fh, obj);
    }

    protected void delete(FactHandle fh) {
        delete(sessions.get(0), fh);
    }

    protected void delete(KieSession session, FactHandle fh) {
        session.delete(fh);
    }

    protected FactHandle insertMatchingPerson(String name, Integer age) {
        return insertMatchingPerson(sessions.get(0), name, age);
    }

    protected FactHandle insertMatchingPerson(KieSession session, String name, Integer age) {
        return session.insert(new Person(name, age));
    }

    protected void updateWithMatchingPerson(FactHandle nonMatching, Object matching) {
        updateWithMatchingPerson(sessions.get(0), nonMatching, matching);
    }

    protected void updateWithMatchingPerson(KieSession session, FactHandle nonMatching, Object matching) {
        session.update(nonMatching,matching);
    }

    protected void updateWithNonMatchingPerson(FactHandle matching, Object nonMatching) {
        updateWithNonMatchingPerson(sessions.get(0), matching, nonMatching);
    }

    protected void updateWithNonMatchingPerson(KieSession session, FactHandle matching, Object nonMatching) {
        session.update(matching, nonMatching);
    }

    protected FactHandle insertNonMatchingPerson(String name, Integer age) {
        return insertNonMatchingPerson(sessions.get(0), name, age);
    }

    protected FactHandle insertNonMatchingPerson(KieSession session, String name, Integer age) {
        return session.insert(new Person(name, age));
    }


    protected List<Object> getResults() {
        return getResults(sessions.get(0));
    }

    protected List<Object> getResults(KieSession session) {
        return (List<Object>) session.getGlobal("results");
    }

    protected void clearResults() {
        clearResults(sessions.get(0));
    }

    protected void clearResults(KieSession session) {
        ((List<Object>) session.getGlobal("results")).clear();
    }

    protected KieSession createSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, Option... options) {
        return createSession(drl, persistenceStrategy, PersistedSessionOption.SafepointStrategy.ALWAYS, options);
    }

    protected KieSession createSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, Option... options) {
        return getKieSession(drl, persistenceStrategy != null ? PersistedSessionOption.newSession().withPersistenceStrategy(persistenceStrategy).withSafepointStrategy(safepointStrategy) : null, options);
    }

    protected KieSession restoreSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, Option... options) {
        return restoreSession(drl, persistenceStrategy, PersistedSessionOption.SafepointStrategy.ALWAYS, options);
    }

    protected KieSession restoreSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, Option... options) {
        return restoreSession(persistedSessionId, drl, persistenceStrategy, safepointStrategy, options);
    }

    protected KieSession restoreSession(Long sessionId, String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, Option... options) {
        return getKieSession(drl, PersistedSessionOption.fromSession(sessionId).withPersistenceStrategy(persistenceStrategy).withSafepointStrategy(safepointStrategy), options);
    }

    protected int fireAllRules() {
        return fireAllRules(sessions.get(0));
    }

    protected int fireAllRules(KieSession session) {
        return session.fireAllRules();
    }

    protected void disposeSession() {
        disposeSession(sessions.get(0));
    }

    protected void disposeSession(KieSession session) {
        session.dispose();
    }

    protected Collection<FactHandle> getFactHandles() {
        return getFactHandles(sessions.get(0));
    }

    protected Collection<FactHandle> getFactHandles(KieSession session) {
        return session.getFactHandles();
    }

    protected void safepoint() {
        safepoint(sessions.get(0));
    }

    protected void safepoint(KieSession session) {
        ((ReliableKieSession) session).safepoint();
    }

    protected long getSessionIdentifier() {
        return sessions.get(0).getIdentifier();
    }

    protected KieSession getKieSession(String drl, PersistedSessionOption persistedSessionOption, Option... options) {
        OptionsFilter optionsFilter = new OptionsFilter(options);
        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build(ExecutableModelProject.class, optionsFilter.getKieBaseOptions());
        return getKieSessionFromKieBase(kbase, persistedSessionOption, optionsFilter);
    }

    private KieSession getKieSessionFromKieBase(KieBase kbase, PersistedSessionOption persistedSessionOption, OptionsFilter optionsFilter) {
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        if (persistedSessionOption != null) {
            conf.setOption(persistedSessionOption);
            safepointStrategy = persistedSessionOption.getSafepointStrategy();
        }
        Stream.of(optionsFilter.getKieSessionOption()).forEach(conf::setOption);
        KieSession session = kbase.newKieSession(conf, null);
        sessions.add(session);
        if (persistedSessionOption == null || persistedSessionOption.isNewSession()) {
            List<Object> results = new ArrayList<>();
            session.setGlobal("results", results);
            persistedSessionId = session.getIdentifier();
        }
        return session;
    }

    protected Optional<Person> getPersonByName(String name) {
        return getPersonByName(sessions.get(0), name);
    }

    protected Optional<Person> getPersonByName(KieSession kieSession, String name) {
        return kieSession.getObjects(new ClassObjectFilter(Person.class))
                .stream()
                .map(Person.class::cast)
                .filter(p -> p.getName().equals(name) ).findFirst();
    }

    private static class OptionsFilter {
        private final Option[] options;

        OptionsFilter(Option[] options) {
            this.options = options;
        }

        KieBaseOption[] getKieBaseOptions() {
            return options == null ? new KieBaseOption[0] : Stream.of(options).filter(KieBaseOption.class::isInstance).toArray(KieBaseOption[]::new);
        }

        KieSessionOption[] getKieSessionOption() {
            return options == null ? new KieSessionOption[0] : Stream.of(options).filter(KieSessionOption.class::isInstance).toArray(KieSessionOption[]::new);
        }
    }
}
