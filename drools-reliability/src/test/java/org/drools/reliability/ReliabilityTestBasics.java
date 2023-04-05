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

package org.drools.reliability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.drools.core.ClassObjectFilter;
import org.test.domain.Person;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.test.core.InfinispanContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(BeforeAllMethodExtension.class)
public abstract class ReliabilityTestBasics {

    private static final Logger LOG = LoggerFactory.getLogger(ReliabilityTestBasics.class);

    private InfinispanContainer container;
    protected long savedSessionId;
    protected KieSession session;

    static Stream<PersistedSessionOption.Strategy> strategyProvider() {
        return Stream.of(PersistedSessionOption.Strategy.STORES_ONLY, PersistedSessionOption.Strategy.FULL);
    }

    static Stream<PersistedSessionOption.Strategy> strategyProviderStoresOnly() {
        return Stream.of(PersistedSessionOption.Strategy.STORES_ONLY);
    }

    @BeforeEach
    public void setUp() {
        if (CacheManagerFactory.INSTANCE.getCacheManager().isRemote()) {
            LOG.info("Starting InfinispanContainer");
            container = new InfinispanContainer();
            container.start();
            LOG.info("InfinispanContainer started"); // takes about 10 seconds
            RemoteCacheManager remoteCacheManager = container.getRemoteCacheManager(CacheManagerFactory.INSTANCE.getCacheManager().provideAdditionalRemoteConfigurationBuilder());
            CacheManagerFactory.INSTANCE.getCacheManager().setRemoteCacheManager(remoteCacheManager);
        }
    }

    @AfterEach
    public void tearDown() {
        if (CacheManagerFactory.INSTANCE.getCacheManager().isRemote()) {
            CacheManagerFactory.INSTANCE.getCacheManager().close(); // close remoteCacheManager
            container.stop(); // stop remote infinispan
        } else {
            // clean up embedded Infinispan including GlobalState and FireStore so that test methods can be isolated
            CacheManagerFactory.INSTANCE.getCacheManager().restartWithCleanUp();
        }

        ReliableRuntimeComponentFactoryImpl.resetCounter();
    }

    public void failover() {
        if (CacheManagerFactory.INSTANCE.getCacheManager().isRemote()) {
            // fail-over means restarting Drools instance. Assuming remote infinispan keeps alive
            CacheManagerFactory.INSTANCE.getCacheManager().close(); // close remoteCacheManager
            // Reclaim RemoteCacheManager
            RemoteCacheManager remoteCacheManager = container.getRemoteCacheManager(CacheManagerFactory.INSTANCE.getCacheManager().provideAdditionalRemoteConfigurationBuilder());
            CacheManagerFactory.INSTANCE.getCacheManager().setRemoteCacheManager(remoteCacheManager);
        } else {
            CacheManagerFactory.INSTANCE.getCacheManager().restart(); // restart embedded infinispan cacheManager. GlobalState and FireStore are kept
        }
        ReliableRuntimeComponentFactoryImpl.resetCounter();
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

    protected KieSession createSession(String drl, PersistedSessionOption.Strategy strategy, Option... options) {
        getKieSession(drl, PersistedSessionOption.newSession(strategy), options);
        savedSessionId = session.getIdentifier();
        return session;
    }

    protected KieSession restoreSession(String drl, PersistedSessionOption.Strategy strategy, Option... options) {
        return getKieSession(drl, PersistedSessionOption.fromSession(savedSessionId, strategy), options);
    }

    protected void disposeSession() {
        session.dispose();
    }

    protected KieSession getKieSession(String drl, PersistedSessionOption option, Option... options) {
        OptionsFilter optionsFilter = new OptionsFilter(options);
        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build(optionsFilter.getKieBaseOptions());
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(option);
        Stream.of(optionsFilter.getKieSessionOption()).forEach(conf::setOption);
        session = kbase.newKieSession(conf, null);
        if (option.isNewSession()) {
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
