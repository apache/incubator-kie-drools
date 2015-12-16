/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.test.model.DroolsTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBaseFactory;

import static org.junit.Assert.assertEquals;

/**
 * Test case to ensure that the ReteooRuleBase is thread safe. Specifically to test for
 * deadlocks when modifying the rulebase while creating new sessions.
 */
public class ReteooRuleBaseMultiThreadedTest extends DroolsTestCase {

    InternalKnowledgeBase kBase;
    RuleImpl rule;
    InternalKnowledgePackage pkg;

    @Before
    public void setUp() {
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();

        pkg = new KnowledgePackageImpl("org.droos.test");
        pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(Thread.currentThread().getContextClassLoader()));

        JavaDialectRuntimeData data = new JavaDialectRuntimeData();
        data.onAdd(pkg.getDialectRuntimeRegistry(), kBase.getRootClassLoader());
        pkg.getDialectRuntimeRegistry().setDialectData("java", data);

        // we need to add one rule to the package because the previous deadlock was encountered
        // while removing rules from a package when said package is removed from the rulebase
        rule = new RuleImpl("Test");
        rule.setDialect("java");
        rule.setConsequence(new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) throws Exception {

            }
            
            public String getName() {
                return "default";
            }
        });
        pkg.addRule(rule);

        kBase.addPackage(pkg);
    }

    @Test @Ignore
    public void testNewSessionWhileModifyingRuleBase() throws InterruptedException {
        PackageModifier modifier = new PackageModifier();
        SessionCreator creator = new SessionCreator();

        creator.start();
        modifier.start();

        // 10 seconds should be more than enough time to see if the modifer and creator
        // get deadlocked
        Thread.sleep(10000);

        boolean deadlockDetected = creator.isBlocked() && modifier.isBlocked();

        if (deadlockDetected) {
            // dump both stacks to show it
            printThreadStatus(creator);
            printThreadStatus(modifier);
        }

        assertEquals("Threads are deadlocked! See previous stacks for more detail", false, deadlockDetected);

        // check to see if either had an exception also
        if (creator.isInError()) {
            creator.getError().printStackTrace();
        }
        assertEquals("Exception in creator thread", false, creator.isInError());

        if (modifier.isInError()) {
            modifier.getError().printStackTrace();
        }
        assertEquals("Exception in modifier thread", false, modifier.isInError());
    }

    private void printThreadStatus(Thread thread) {
        StackTraceElement[] frames = thread.getStackTrace();

        System.err.println(thread.getName() + ": " + thread.getState());

        for (StackTraceElement frame : frames) {
            System.err.println(frame);
        }

        System.err.println();
    }

    private abstract class BlockedThread extends Thread {
        private static final int NUMER_ATTEMPTS = 50000;
        private volatile Throwable error;

        BlockedThread(String name) {
            super(name);
            setDaemon(true);
        }

        public boolean isInError() {
            return error != null;
        }

        public Throwable getError() {
            return error;
        }

        public boolean isBlocked() {
            return getState() == State.BLOCKED;
        }

        public void run() {
            int numAttempts = 0;

            try {
                while (numAttempts < NUMER_ATTEMPTS) {
                    doOperation();

                    numAttempts++;
                }
            } catch (Throwable t) {
                error = t;
            }
        }

        abstract void doOperation();
    }

    /**
     * This thread will continually try to remove a package and add a package to
     * the rulebase
     */
    private class PackageModifier extends BlockedThread {
        private PackageModifier() {
            super("Rulebase Modifier Thread");
        }

        void doOperation() {
            kBase.removeKiePackage(pkg.getName());
            kBase.addPackage(pkg);
        }
    }

    /**
     * This thread will continually create and dispose new stateful sessions
     */
    private class SessionCreator extends BlockedThread {

        private SessionCreator() {
            super("Session Creator Thread");
        }

        void doOperation() {
            KieSession session = null;

            try {
                session = kBase.newKieSession();
            } finally {
                if (session != null) {
                    session.dispose();
                }
            }
        }
    }
}
