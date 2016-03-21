/*
 * Copyright 2016 Red Hat Inc.
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
package org.drools.persistence.kie.persistence.session;

import org.drools.compiler.Person;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.OPTIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.PESSIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class PersistentSessionForallTest {

    private static final Person OWNER = new Person("cat lady"); // "cat lady" instead of "dog lady" makes the forall not match, and also triggers NPE
    private static final Pet DOG = new Pet(Pet.PetType.dog, OWNER);

    private KieSession kieSession;

    private Map<String, Object> context;
    private Environment env;
    private boolean locking;

    @Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] {
                { OPTIMISTIC_LOCKING },
                { PESSIMISTIC_LOCKING }
                };
        return Arrays.asList(locking);
    };

    public PersistentSessionForallTest(String locking) {
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }

    @Before
    public void setUp() throws Exception {
        setupPersistence();
        createKieSession();
    }

    private void setupPersistence() {
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
        if( locking ) {
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
    }

    private void createKieSession() {
        KieServices ks = KieServices.Factory.get();

        Resource drl = ks.getResources().newClassPathResource("forall.drl", PersistentSessionForallTest.class);
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/forall.drl", drl );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        kieSession = ks.getStoreServices().newKieSession( kbase, null, env );
    }

    @After
    public void tearDown() throws Exception {
        cleanUpKieSession();
        DroolsPersistenceUtil.cleanUp(context);
    }

    private void cleanUpKieSession() {
        if (kieSession != null) {
            kieSession.destroy();
        }
    }

    /**
     * Tests marshalling of persistent KieSession with forall.
     */
    @Test
    public void testNotMatchedCombination() {
        TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        kieSession.addEventListener(listener);

        kieSession.insert(DOG);
        kieSession.fireAllRules();

        assertTrue(listener.isRuleFired("Forall2"));
    }

    public static class Pet implements Serializable {

        private static final long serialVersionUID = -3519777750853629395L;

        public enum PetType {
            dog, cat
        }

        private PetType type;
        private int age;
        private Person owner;

        public Pet(PetType type) {
            this.type = type;
            age = 0;
        }

        public Pet(PetType type, Person owner) {
            this(type);
            this.owner = owner;
        }

        public PetType getType() {
            return type;
        }

        public void setType(PetType type) {
            this.type = type;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Person getOwner() {
            return owner;
        }

        public void setOwner(Person owner) {
            this.owner = owner;
        }
    }

    /**
     * Listener tracking number of rules fired.
     */
    public static class TrackingAgendaEventListener extends DefaultAgendaEventListener {

        private Map<String, Integer> rulesFired = new HashMap<String, Integer>();

        @Override
        public void afterMatchFired(AfterMatchFiredEvent event) {
            String rule = event.getMatch().getRule().getName();
            if (isRuleFired(rule)) {
                rulesFired.put(rule, rulesFired.get(rule) + 1);
            } else {
                rulesFired.put(rule, 1);
            }
        }

        /**
         * Return true if the rule was fired at least once
         *
         * @param rule - name of the rule
         * @return true if the rule was fired
         */
        public boolean isRuleFired(String rule) {
            return rulesFired.containsKey(rule);
        }

        /**
         * Returns number saying how many times the rule was fired
         *
         * @param rule - name of the rule
         * @return number how many times rule was fired, 0 if rule wasn't fired
         */
        public int ruleFiredCount(String rule) {
            if (isRuleFired(rule)) {
                return rulesFired.get(rule);
            } else {
                return 0;
            }
        }

        public void clear() {
            rulesFired.clear();
        }
    }
}
