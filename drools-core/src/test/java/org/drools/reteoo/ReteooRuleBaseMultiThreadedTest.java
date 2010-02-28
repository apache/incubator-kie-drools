package org.drools.reteoo;

import junit.framework.Assert;
import org.drools.DroolsTestCase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;

/**
 * Test case to ensure that the ReteooRuleBase is thread safe. Specifically to test for
 * deadlocks when modifying the rulebase while creating new sessions.
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
public class ReteooRuleBaseMultiThreadedTest extends DroolsTestCase {

    ReteooRuleBase ruleBase;
    Rule rule;
    org.drools.rule.Package pkg;

    public void setUp() {
        this.ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();

        pkg = new org.drools.rule.Package("org.droos.test");
        pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(Thread.currentThread().getContextClassLoader()));

        JavaDialectRuntimeData data = new JavaDialectRuntimeData();
        data.onAdd(pkg.getDialectRuntimeRegistry(), ruleBase.getRootClassLoader());
        pkg.getDialectRuntimeRegistry().setDialectData("java", data);

        // we need to add one rule to the package because the previous deadlock was encountered
        // while removing rules from a package when said package is removed from the rulebase
        rule = new Rule("Test");
        rule.setDialect("java");
        rule.setConsequence(new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) throws Exception {

            }
        });
        pkg.addRule(rule);

        ruleBase.addPackage(pkg);
    }
    
    public void testDummy() {}

    public void FIXME_testNewSessionWhileModifyingRuleBase() throws InterruptedException {
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

        Assert.assertEquals("Threads are deadlocked! See previous stacks for more detail", false, deadlockDetected);

        // check to see if either had an exception also
        if (creator.isInError()) {
            creator.getError().printStackTrace();
        }
        Assert.assertEquals("Exception in creator thread", false, creator.isInError());

        if (modifier.isInError()) {
            modifier.getError().printStackTrace();
        }
        Assert.assertEquals("Exception in modifier thread", false, modifier.isInError());
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
            ruleBase.removePackage(pkg.getName());
            ruleBase.addPackage(pkg);
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
            StatefulSession session = null;

            try {
                session = ruleBase.newStatefulSession();
            } finally {
                if (session != null) {
                    session.dispose();
                }
            }
        }
    }
}