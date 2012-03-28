package org.jbpm.persistence;

import static org.drools.persistence.util.PersistenceUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to reproduce bug with multiple threads using persistence and each
 * configures its own entity manager.
 * 
 */
public class ProcessMultiThreadPersistenceTest {

    private static Logger logger = LoggerFactory
	    .getLogger(ProcessMultiThreadPersistenceTest.class);

    private static HashMap<String, Object> context;

    private static final int LOOPS = 100;

    public static void main(String args[]) {
	ProcessMultiThreadPersistenceTest test = new ProcessMultiThreadPersistenceTest();
	test.testParallel();
    }

    @Before
    public void setup() {
	context = PersistenceUtil.setupWithPoolingDataSource(
		PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME, "jdbc/jbpm-ds",
		false);
    }

    @After
    public void tearDown() {
	PersistenceUtil.cleanUp(context);
    }

    @Test
    public void testParallel() {
	HelloWorldThread hello = new HelloWorldThread();
	UserTaskThread user = new UserTaskThread();

	hello.start();
	user.start();

	try {
	    hello.join();
	    user.join();
	} catch (Throwable t) {
	    t.printStackTrace();
	}
    }

    private static class HelloWorldThread implements Runnable {

	private Thread thread;

	public void start() {
	    thread = new Thread(this);
	    thread.start();
	}

	public void run() {

	    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
		    .newKnowledgeBuilder();
	    kbuilder.add(ResourceFactory.newClassPathResource(
		    "hello-world.bpmn", getClass()), ResourceType.BPMN2);
	    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
	    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

	    Environment env = createEnvironment(context);
	    StatefulKnowledgeSession ksession = JPAKnowledgeService
		    .newStatefulKnowledgeSession(kbase, null, env);

	    for (int i = 1; i <= LOOPS; i++) {

		logger.info("Starting process hello-world, loop " + i + "/"
			+ LOOPS);

		CountDownLatch latch = new CountDownLatch(1);
		CompleteProcessListener listener = new CompleteProcessListener(
			latch);
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

    private static class UserTaskThread implements Runnable {

	private Thread thread;

	public void start() {
	    thread = new Thread(this);
	    thread.start();
	}

	public void run() {
	    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
		    .newKnowledgeBuilder();
	    kbuilder.add(ResourceFactory.newClassPathResource("user-task.bpmn",
		    getClass()), ResourceType.BPMN2);
	    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
	    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

	    Environment env = createEnvironment(context);
	    StatefulKnowledgeSession ksession = JPAKnowledgeService
		    .newStatefulKnowledgeSession(kbase, null, env);

	    TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
	    ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
		    workItemHandler);

	    for (int i = 1; i <= LOOPS; i++) {

		logger.info("Starting process user-task, loop " + i + "/"
			+ LOOPS);

		CountDownLatch latch = new CountDownLatch(1);
		CompleteProcessListener listener = new CompleteProcessListener(
			latch);
		ksession.addEventListener(listener);

		try {
		    ksession.startProcess("user-task");
		} catch (Throwable t) {
		    t.printStackTrace();
		}

		try {
		    Thread.sleep(1500);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}

		List<WorkItem> items = new ArrayList<WorkItem>();
		items = workItemHandler.getWorkItems();
		for (WorkItem item : items) {
		    try {
			ksession.getWorkItemManager().completeWorkItem(
				item.getId(), null);
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
		throw new IllegalArgumentException(
			"More than one work item active");
	    }
	}

	public List<WorkItem> getWorkItems() {
	    List<WorkItem> result = new ArrayList<WorkItem>(workItems);
	    workItems.clear();
	    return result;
	}

    }

    private static class CompleteProcessListener implements
	    ProcessEventListener {
	private CountDownLatch guard;

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
