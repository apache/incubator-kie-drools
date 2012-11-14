package org.jbpm.bpmn2.concurrency;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.kie.io.ResourceFactory;
import org.jbpm.persistence.util.LoggingPrintStream;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.bpmn2.objects.Status;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test costs time and resources, please only run locally for the time being.
 */
@Ignore
public class MultipleProcessesPerThreadTest {
    
    private static final int LOOPS = 1000;
    
    private static Logger logger = LoggerFactory.getLogger(MultipleProcessesPerThreadTest.class);
    static { 
        System.setOut(new LoggingPrintStream(System.out));
    }
    
    protected static StatefulKnowledgeSession createStatefulKnowledgeSession(KnowledgeBase kbase) { 
        return kbase.newStatefulKnowledgeSession();
    }
    
    @Test
    public void doMultipleProcessesInMultipleThreads() {
        
        HelloWorldProcessThread hello = new HelloWorldProcessThread();
        UserTaskProcessThread user = new UserTaskProcessThread();

        hello.start();
        user.start();

        try {
            hello.join();
            user.join();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        assertTrue( "Hello World process thread did not complete successfully", hello.status == Status.SUCCESS );
        assertTrue( "User Task process thread did not complete successfully", user.status == Status.SUCCESS );
    }

    private static class HelloWorldProcessThread implements Runnable {

        private Thread thread;
        volatile Status status;
        private volatile CountDownLatch latch;

        public void start() {
            thread = new Thread(this);
            thread.start();
        }

        public void run() {
            this.status = Status.SUCCESS;
            StatefulKnowledgeSession ksession = null;
            
            try { 
                KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
                kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultiThreadServiceProcess-Timer.bpmn", getClass()), ResourceType.BPMN2);
                KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
                kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

                ksession = createStatefulKnowledgeSession(kbase);
            } catch(Exception e) { 
                e.printStackTrace();
                logger.error("Unable to set up knowlede base or session.", e);
                this.status = Status.FAIL;
            }

            for (int i = 1; i <= LOOPS; i++) {
                logger.debug("Starting hello world process, loop " + i + "/" + LOOPS);

                latch = new CountDownLatch(1);
                CompleteProcessListener listener = new CompleteProcessListener(latch);
                ksession.addEventListener(listener);

                try {
                    ksession.startProcess("hello-world");
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                try {
                    latch.await();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            
        }

        public synchronized void join() throws InterruptedException {
            thread.join();
        }
    }

    private static class UserTaskProcessThread implements Runnable {

        private Thread thread;
        volatile Status status;
        private volatile CountDownLatch latch;

        public void start() {
            thread = new Thread(this);
            thread.start();
        }

        public void run() {
            this.status = Status.SUCCESS;
            StatefulKnowledgeSession ksession = null;
            
            try { 
                KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
                kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultiThreadServiceProcess-Task.bpmn", getClass()), ResourceType.BPMN2);
                KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
                kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

                ksession = createStatefulKnowledgeSession(kbase);
            } catch(Exception e) { 
                e.printStackTrace();
                logger.error("Unable to set up knowlede base or session.", e);
                this.status = Status.FAIL;
            }
            
            TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

            for (int i = 1; i <= LOOPS; i++) {
                logger.debug("Starting user task process, loop " + i + "/" + LOOPS);

                latch = new CountDownLatch(1);
                CompleteProcessListener listener = new CompleteProcessListener(latch);
                ksession.addEventListener(listener);

                try {
                    ksession.startProcess("user-task");
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<WorkItem> items = new ArrayList<WorkItem>();
                items = workItemHandler.getWorkItems();
                for (WorkItem item : items) {
                    try {
                        ksession.getWorkItemManager().completeWorkItem(item.getId(), null);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }

                try {
                    latch.await();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            

        }

        public synchronized void join() throws InterruptedException {
            thread.join();
        }

    }

    public static class TestWorkItemHandler implements WorkItemHandler {

        private List<WorkItem> workItems = new ArrayList<WorkItem>();

        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItems.add(workItem);
        }

        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }

        public WorkItem getWorkItem() {
            if (workItems.size() == 0) {
                return null;
            }
            if (workItems.size() == 1) {
                WorkItem result = workItems.get(0);
                this.workItems.clear();
                return result;
            } else {
                throw new IllegalArgumentException("More than one work item active");
            }
        }

        public List<WorkItem> getWorkItems() {
            List<WorkItem> result = new ArrayList<WorkItem>(workItems);
            workItems.clear();
            return result;
        }

    }

    private static class CompleteProcessListener implements ProcessEventListener {
        private volatile CountDownLatch guard;

        public CompleteProcessListener(CountDownLatch guard) {
            this.guard = guard;
        }

        public void beforeProcessStarted(ProcessStartedEvent event) {
        }

        public void afterProcessStarted(ProcessStartedEvent event) {

        }

        public void beforeProcessCompleted(ProcessCompletedEvent event) {

        }

        public void afterProcessCompleted(ProcessCompletedEvent event) {
            guard.countDown();
        }

        public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {

        }

        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {

        }

        public void beforeNodeLeft(ProcessNodeLeftEvent event) {

        }

        public void afterNodeLeft(ProcessNodeLeftEvent event) {

        }

        public void beforeVariableChanged(ProcessVariableChangedEvent event) {

        }

        public void afterVariableChanged(ProcessVariableChangedEvent event) {

        }

    }
}
