package org.drools.benchmark.process;

//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
//import org.drools.persistence.jpa.JPAKnowledgeService;
//import org.drools.runtime.Environment;
//import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;

//import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PerformanceTest {

	public static void main(String[] args) throws Exception {
		// Normal process execution
		System.out.println("********************************");
		System.out.println("*** Normal process execution ***");
		System.out.println("********************************");
		System.out.println();
		System.out.println("Building knowledge base ... ");
		KnowledgeBase kbase = readKnowledgeBase();
		System.out.println("Initializing session ... ");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		// start a new process instance
		for (int i = 0; i < 100; i++) {
			ksession.startProcess("com.sample.empty", null);
		}
		System.out.println("Starting ... ");
		
		Thread.sleep(1000);
		long start = System.nanoTime();
		for (int i = 0; i < 10000; i++) {
			ksession.startProcess("com.sample.empty", null);
		}
		long end = System.nanoTime() - start;
		System.out.println("Total time = " + (end / 1000000) + "ms");
		ksession.dispose();
	}

//	public static void main(String[] args) throws Exception {
//		// Persistent process execution
//		System.out.println("************************************");
//		System.out.println("*** Persistent process execution ***");
//		System.out.println("************************************");
//		System.out.println();
//		System.out.println("Initializing database");
//		PoolingDataSource ds1 = new PoolingDataSource();
//        ds1.setUniqueName("jdbc/testDS1");
//        ds1.setClassName("org.h2.jdbcx.JdbcDataSource");
//        ds1.setMaxPoolSize(3);
//        ds1.setAllowLocalTransactions(true);
//        ds1.getDriverProperties().put("user", "sa");
//        ds1.getDriverProperties().put("password", "sasa");
//        ds1.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");
//        ds1.init();
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.drools.persistence.jpa");
//
//		System.out.println("Building knowledge base ... ");
//		KnowledgeBase kbase = readKnowledgeBase();
//		System.out.println("Initializing session ... ");
//		Environment env = KnowledgeBaseFactory.newEnvironment();
//        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
//        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
//		// start a new process instance
//		for (int i = 0; i < 100; i++) {
//			ksession.startProcess("com.sample.empty", null);
//		}
//		System.out.println("Starting ... ");
//		
//		Thread.sleep(1000);
//		long start = System.nanoTime();
//		for (int i = 0; i < 10000; i++) {
//			ksession.startProcess("com.sample.empty", null);
//		}
//		long end = System.nanoTime() - start;
//		System.out.println("Total time = " + (end / 1000000) + "ms");
//		ksession.dispose();
//	}
	
	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("empty.rf", PerformanceTest.class), ResourceType.DRF);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}
	
}
