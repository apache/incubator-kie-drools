package org.jbpm.persistence;

import static org.drools.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.drools.persistence.util.PersistenceUtil.setupWithPoolingDataSource;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * Class to reproduce bug with multiple threads using persistence and each
 * configures its own entity manager.
 * 
 * @author jsvitak@redhat.com
 * 
 */
public class ProcessMultiThreadPersistenceTest {

    private static Logger logger = LoggerFactory
	    .getLogger(ProcessMultiThreadPersistenceTest.class);

    private static PoolingDataSource ds;
    private static EntityManagerFactory emf;

    public static void main(String args []) {
	ProcessMultiThreadPersistenceTest test = new ProcessMultiThreadPersistenceTest();
	//test.setupPersistence();
	test.testParallel();
    }
    
    /*
    public void setupPersistence() {

	System.setProperty("db.url", "jdbc:hsqldb:file:"
		+ new File("target", "localDB").getAbsolutePath());
	System.setProperty("db.driver", "org.hsqldb.jdbcDriver");
	System.setProperty("db.dialect",
		"org.hibernate.dialect.HSQLDialect");
	System.setProperty("db.username", "");
	System.setProperty("db.password", "");
	System.setProperty("db.isolation",
		Integer.toString(Connection.TRANSACTION_READ_COMMITTED));
	System.setProperty("java.naming.factory.initial",
		"bitronix.tm.jndi.BitronixInitialContextFactory");

	logger.info("Creating pooling data source");
	ds = new PoolingDataSource();
	ds.setUniqueName("jdbc/ownds");
	ds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
	ds.setMaxPoolSize(5);
	ds.setAllowLocalTransactions(true);
	ds.getDriverProperties()
		.put("driverClassName", "org.hsqldb.jdbcDriver");
	ds.getDriverProperties().put(
		"url",
		"jdbc:hsqldb:file:"
			+ new File("target", "bitronixDB").getAbsolutePath());
	ds.init();
    }
    */

    @Test
    public void testParallel() {
	HelloWorldThread hello = new HelloWorldThread();
	UserTaskThread user = new UserTaskThread();
	hello.start();
	// user.start();
	try {
	    hello.join();
	    // user.join();
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

	    /*
	     * HashMap<String, Object> context = new HashMap<String, Object>();
	     * context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME,
	     * false); Environment env = createEnvironment(context);
	     */

	    StatefulKnowledgeSession ksession = JPAKnowledgeService
		    .newStatefulKnowledgeSession(kbase, null, getEnvironment());

	    CountDownLatch latch = new CountDownLatch(1);
	    CompleteProcessListener listener = new CompleteProcessListener(
		    latch);
	    ksession.addEventListener(listener);

	    try {
		logger.info("Starting process hello-world");
		ksession.startProcess("hello-world");
	    } catch (Throwable t) {
		t.printStackTrace();
	    }

	    try {
		latch.await();
		logger.info("Completed process hello-world");
	    } catch (Throwable t) {
		t.printStackTrace();
	    }
	}

	public synchronized void join() throws InterruptedException {
	    thread.join();
	}

	private final Environment getEnvironment() {
	    emf = Persistence
		    .createEntityManagerFactory("org.jbpm.persistence.jpa");
	    Environment env = KnowledgeBaseFactory.newEnvironment();
	    env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
	    env.set(EnvironmentName.TRANSACTION_MANAGER,
		    TransactionManagerServices.getTransactionManager());
	    return env;
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

	    /*
	     * Environment env = KnowledgeBaseFactory.newEnvironment();
	     * env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, Persistence
	     * .createEntityManagerFactory("org.drools.persistence.jpa"));
	     * env.set(EnvironmentName.TRANSACTION_MANAGER,
	     * TransactionManagerServices.getTransactionManager());
	     */

	    HashMap<String, Object> context = new HashMap<String, Object>();
	    context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME,
		    false);
	    Environment env = createEnvironment(context);

	    StatefulKnowledgeSession ksession = JPAKnowledgeService
		    .newStatefulKnowledgeSession(kbase, null, env);

	    TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
	    ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
		    workItemHandler);

	    CountDownLatch latch = new CountDownLatch(1);
	    CompleteProcessListener listener = new CompleteProcessListener(
		    latch);
	    ksession.addEventListener(listener);

	    try {
		logger.info("Starting process user-task");
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
		    logger.info("Completed work item of user-task process");
		} catch (Throwable t) {
		    t.printStackTrace();
		}
	    }

	    try {
		latch.await();
		logger.info("Completed process user-task");
	    } catch (Throwable t) {
		t.printStackTrace();
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
