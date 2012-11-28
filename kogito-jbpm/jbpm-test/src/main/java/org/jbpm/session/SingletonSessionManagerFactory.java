package org.jbpm.session;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kie.KnowledgeBase;

public class SingletonSessionManagerFactory implements SessionManagerFactory {

	private EntityManagerFactory emf;
	private SessionEnvironment sessionEnvironment;
	
	public SingletonSessionManagerFactory(KnowledgeBase kbase) {
		// TODO: make persistenceUnitName configurable
		// TODO inject emf or em
		// Make sure this is easy to use in spring
		emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		StatefulKnowledgeSessionFactory factory = new StatefulKnowledgeSessionFactory();
		factory.setEntityManagerFactory(emf);
		factory.setKnowledgeBase(kbase);
		// TODO add support for reloading ksession when id is known
		this.sessionEnvironment = factory.createStatefulKnowledgeSession();
	}
	
	public SessionManager getSessionManager() {
		return new SingletonSessionManager(sessionEnvironment);
	}

	// TODO: this should not throw an Exception
	public void dispose() throws Exception {
		sessionEnvironment.dispose();
		emf.close();
	}

}
