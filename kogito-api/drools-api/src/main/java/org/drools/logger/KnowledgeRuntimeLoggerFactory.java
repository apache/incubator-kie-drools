package org.drools.logger;

import org.drools.ProviderInitializationException;
import org.drools.event.KnowledgeRuntimeEventManager;

public class KnowledgeRuntimeLoggerFactory {

	private static KnowledgeRuntimeLoggerProvider knowledgeRuntimeLoggerProvider;

	public static KnowledgeRuntimeLogger newFileLogger(KnowledgeRuntimeEventManager session, String fileName) {
		return getKnowledgeRuntimeLoggerProvider().newFileLogger(session, fileName);
	}

	public static KnowledgeRuntimeLogger newThreadedFileLogger(
			KnowledgeRuntimeEventManager session, String fileName, int interval) {
		return getKnowledgeRuntimeLoggerProvider().newThreadedFileLogger(session, fileName, interval);
	}

	public static KnowledgeRuntimeLogger newConsoleLogger(KnowledgeRuntimeEventManager session) {
		return getKnowledgeRuntimeLoggerProvider().newConsoleLogger(session);
	}

	private static synchronized void setKnowledgeRuntimeLoggerProvider(
			KnowledgeRuntimeLoggerProvider provider) {
		KnowledgeRuntimeLoggerFactory.knowledgeRuntimeLoggerProvider = provider;
	}

	private static synchronized KnowledgeRuntimeLoggerProvider getKnowledgeRuntimeLoggerProvider() {
		if (knowledgeRuntimeLoggerProvider == null) {
			loadProvider();
		}
		return knowledgeRuntimeLoggerProvider;
	}

	@SuppressWarnings("unchecked")
	private static void loadProvider() {
		try {
			Class<KnowledgeRuntimeLoggerProvider> cls = (Class<KnowledgeRuntimeLoggerProvider>)
				Class.forName("org.drools.audit.KnowledgeRuntimeLoggerProviderImpl");
			setKnowledgeRuntimeLoggerProvider(cls.newInstance());
		} catch (Exception e) {
			throw new ProviderInitializationException(
				"Provider org.drools.audit.KnowledgeRuntimeLoggerProviderImpl could not be set.", e);
		}
	}

}
