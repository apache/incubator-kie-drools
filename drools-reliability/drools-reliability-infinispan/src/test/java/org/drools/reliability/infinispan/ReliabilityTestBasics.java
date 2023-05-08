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

package org.drools.reliability.infinispan;

import org.drools.core.ClassObjectFilter;
import org.drools.reliability.core.ReliableKieSession;
import org.drools.reliability.core.ReliableRuntimeComponentFactoryImpl;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.TestableStorageManager;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.test.core.InfinispanContainer;
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
import org.test.domain.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MARSHALLER;
import static org.drools.util.Config.getConfig;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(BeforeAllMethodExtension.class)
public abstract class ReliabilityTestBasics {

    private static final Logger LOG = LoggerFactory.getLogger(ReliabilityTestBasics.class);

    private InfinispanContainer container;
    protected long savedSessionId;
    protected KieSession session;
    protected PersistedSessionOption.SafepointStrategy safepointStrategy;

    static Stream<PersistedSessionOption.PersistenceStrategy> strategyProvider() {
        return Stream.of(PersistedSessionOption.PersistenceStrategy.STORES_ONLY, PersistedSessionOption.PersistenceStrategy.FULL);
    }

    static Stream<Arguments> strategyProviderStoresOnly() {
        return Stream.of(arguments(PersistedSessionOption.PersistenceStrategy.STORES_ONLY));
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

    static boolean isProtoStream() {
        return "PROTOSTREAM".equalsIgnoreCase(getConfig(INFINISPAN_STORAGE_MARSHALLER));
    }
    static Stream<PersistedSessionOption.Strategy> strategyProviderFull() {
        return Stream.of(PersistedSessionOption.Strategy.FULL);
    }

    @BeforeEach
    public void setUp() {
        if (((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).isRemote()) {
            LOG.info("Starting InfinispanContainer");
            container = new InfinispanContainer();
            container.start();
            LOG.info("InfinispanContainer started"); // takes about 10 seconds
            InfinispanStorageManager cacheManager = (InfinispanStorageManager) StorageManagerFactory.get().getStorageManager();
            RemoteCacheManager remoteCacheManager = container.getRemoteCacheManager(cacheManager.provideAdditionalRemoteConfigurationBuilder());
            cacheManager.setRemoteCacheManager(remoteCacheManager);
        }
    }

    @AfterEach
    public void tearDown() {
        if (((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).isRemote()) {
            StorageManagerFactory.get().getStorageManager().removeAllSessionStorages();
            StorageManagerFactory.get().getStorageManager().close(); // close remoteCacheManager
            container.stop(); // stop remote infinispan
        } else {
            // clean up embedded Infinispan including GlobalState and FireStore so that test methods can be isolated
            ((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).restartWithCleanUp();
        }
        ReliableRuntimeComponentFactoryImpl.resetCounter();
    }

    public void failover() {
        if (safepointStrategy == PersistedSessionOption.SafepointStrategy.EXPLICIT) {
            ((ReliableKieSession) session).safepoint();
        }

        if (((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).isRemote()) {
            // fail-over means restarting Drools instance. Assuming remote infinispan keeps alive
            StorageManagerFactory.get().getStorageManager().close(); // close remoteCacheManager
            // Reclaim RemoteCacheManager
            InfinispanStorageManager cacheManager = (InfinispanStorageManager) StorageManagerFactory.get().getStorageManager();
            RemoteCacheManager remoteCacheManager = container.getRemoteCacheManager(cacheManager.provideAdditionalRemoteConfigurationBuilder());
            cacheManager.setRemoteCacheManager(remoteCacheManager);
        } else {
            ((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).restart(); // restart embedded infinispan cacheManager. GlobalState and FireStore are kept
        }
        ReliableRuntimeComponentFactoryImpl.refreshCounterUsingStorage();
    }

    protected FactHandle insertString(String str) {
        return session.insert(str);
    }

    protected FactHandle insertInteger(Integer number) {
        return session.insert(number);
    }

    protected FactHandle insertMatchingPerson(String name, Integer age) {
        return session.insert(new Person(name, age));
    }

    protected void updateWithMatchingPerson(FactHandle nonMatching, Object matching){
        session.update(nonMatching,matching);
    }

    protected  void updateWithNonMatchingPerson(FactHandle matching, Object nonMatching){
        session.update(matching, nonMatching);
    }

    protected FactHandle insertNonMatchingPerson(String name, Integer age) {
        return session.insert(new Person(name, age));
    }

    protected List<Object> getResults() {
        return (List<Object>) session.getGlobal("results");
    }

    protected void clearResults() {
        ((List<Object>) session.getGlobal("results")).clear();
    }

    protected KieSession createSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, Option... options) {
        return createSession(drl, persistenceStrategy, PersistedSessionOption.SafepointStrategy.ALWAYS, options);
    }

    protected KieSession createSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, Option... options) {
        getKieSession(drl, persistenceStrategy != null ? PersistedSessionOption.newSession().withPersistenceStrategy(persistenceStrategy).withSafepointStrategy(safepointStrategy) : null, options);
        savedSessionId = session.getIdentifier();
        return session;
    }

    protected KieSession restoreSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, Option... options) {
        return restoreSession(drl, persistenceStrategy, PersistedSessionOption.SafepointStrategy.ALWAYS, options);
    }

    protected KieSession restoreSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, Option... options) {
        return getKieSession(drl, PersistedSessionOption.fromSession(savedSessionId).withPersistenceStrategy(persistenceStrategy).withSafepointStrategy(safepointStrategy), options);
    }

    protected void disposeSession() {
        session.dispose();
    }

    protected KieSession getKieSession(String drl, PersistedSessionOption persistedSessionOption, Option... options) {
        OptionsFilter optionsFilter = new OptionsFilter(options);
        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build(optionsFilter.getKieBaseOptions());
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        if (persistedSessionOption != null) {
            conf.setOption(persistedSessionOption);
            safepointStrategy = persistedSessionOption.getSafepointStrategy();
        }
        Stream.of(optionsFilter.getKieSessionOption()).forEach(conf::setOption);
        session = kbase.newKieSession(conf, null);
        if (persistedSessionOption == null || persistedSessionOption.isNewSession()) {
            List<Object> results = new ArrayList<>();
            session.setGlobal("results", results);
        }
        return session;
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
