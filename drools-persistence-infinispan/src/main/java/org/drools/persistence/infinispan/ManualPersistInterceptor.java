package org.drools.persistence.infinispan;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.AbstractInterceptor;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.DisposeCommand;
import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.SessionMarshallingHelper;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.info.SessionInfo;
import org.kie.api.command.Command;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.command.Context;
import org.kie.internal.runtime.KnowledgeContext;

public class ManualPersistInterceptor extends AbstractInterceptor {

	private final SingleSessionCommandService interceptedService;
	
	public ManualPersistInterceptor(SingleSessionCommandService decorated) {
		this.interceptedService = decorated;
	}
	
	public <T> T execute(Command<T> command) {
		T result = executeNext(command);
		try {
	    	java.lang.reflect.Field sessionInfoField = SingleSessionCommandService.class.getDeclaredField("sessionInfo");
	    	sessionInfoField.setAccessible(true);
	    	Object sessionInfo = sessionInfoField.get(interceptedService);
	    	java.lang.reflect.Field jpmField = SingleSessionCommandService.class.getDeclaredField("jpm");
	    	jpmField.setAccessible(true);
	    	Object jpm = jpmField.get(interceptedService);
	    	java.lang.reflect.Field ksessionField = SingleSessionCommandService.class.getDeclaredField("ksession");
	    	ksessionField.setAccessible(true);
	    	Object ksession = ksessionField.get(interceptedService);
	    	if (!(command instanceof DisposeCommand)) {
	    		executeNext(new PersistCommand(command, sessionInfo, jpm, ksession));
	    	}
		} catch (Exception e) {
			throw new RuntimeException("Couldn't force persistence of session info", e);
		}
		return result;
	}

	public static class PersistCommand<T> implements GenericCommand<T> {
		
		private final Command<T> command;
		private final SessionInfo sessionInfo;
		private final PersistenceContext persistenceContext;
		private final KieSession ksession;
		
		public PersistCommand(Command<T> command, Object sessionInfo, Object jpm, Object ksession) {
			this.command = command;
			this.sessionInfo = (SessionInfo) sessionInfo;
			this.persistenceContext = ((PersistenceContextManager) jpm).getApplicationScopedPersistenceContext();
			this.ksession = (KieSession) ksession;
		}
		
		@Override
		public T execute(Context context) {
			sessionInfo.setJPASessionMashallingHelper(new SessionMarshallingHelper(ksession, ksession.getSessionConfiguration()));
			sessionInfo.update();
			persistenceContext.persist(sessionInfo);
			return null;
		}
	}

	public CommandService getInterceptedService() {
		return interceptedService;
	}
	
}
