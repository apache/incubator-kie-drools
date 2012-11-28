package org.jbpm.session;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kie.KnowledgeBase;

public class NewSessionSessionManagerFactory implements SessionManagerFactory {

	private EntityManagerFactory emf;
	private StatefulKnowledgeSessionFactory factory;
	
	public NewSessionSessionManagerFactory(KnowledgeBase kbase) {
		// TODO: make persistenceUnitName configurable
		// TODO inject emf or em
		// Make sure this is easy to use in spring
		emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		factory = new StatefulKnowledgeSessionFactory();
		factory.setEntityManagerFactory(emf);
		factory.setKnowledgeBase(kbase);
	}
	
	public SessionManager getSessionManager() {
		return new NewSessionSessionManager(factory);
	}

	public void dispose() throws Exception {
		emf.close();
	}

}
