package org.drools.audit;

import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactoryService;

public class KnowledgeRuntimeLoggerProviderImpl implements KnowledgeRuntimeLoggerFactoryService {

	public KnowledgeRuntimeLogger newFileLogger(KnowledgeRuntimeEventManager session, String fileName) {
		WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
		if (fileName != null) {
			logger.setFileName(fileName);
		}
		return new KnowledgeRuntimeFileLoggerWrapper(logger);
	}

	public KnowledgeRuntimeLogger newThreadedFileLogger(KnowledgeRuntimeEventManager session, String fileName, int interval) {
		ThreadedWorkingMemoryFileLogger logger = new ThreadedWorkingMemoryFileLogger(session);
		if (fileName != null) {
			logger.setFileName(fileName);
		}
		logger.start(interval);
		return new KnowledgeRuntimeThreadedFileLoggerWrapper(logger);
	}

	public KnowledgeRuntimeLogger newConsoleLogger(KnowledgeRuntimeEventManager session) {
		WorkingMemoryConsoleLogger logger = new WorkingMemoryConsoleLogger(session);
		return new KnowledgeRuntimeConsoleLoggerWrapper(logger);
	}
	
	private class KnowledgeRuntimeFileLoggerWrapper implements KnowledgeRuntimeLogger {

		private WorkingMemoryFileLogger logger;
		
		public KnowledgeRuntimeFileLoggerWrapper(WorkingMemoryFileLogger logger) {
			this.logger = logger;
		}
		
		public void close() {
			logger.writeToDisk();
		}

	}

	private class KnowledgeRuntimeThreadedFileLoggerWrapper implements KnowledgeRuntimeLogger {

		private ThreadedWorkingMemoryFileLogger logger;
		
		public KnowledgeRuntimeThreadedFileLoggerWrapper(ThreadedWorkingMemoryFileLogger logger) {
			this.logger = logger;
		}
		
		public void close() {
			logger.stop();
			logger.writeToDisk();
		}

	}

	private class KnowledgeRuntimeConsoleLoggerWrapper implements KnowledgeRuntimeLogger {

		// private WorkingMemoryConsoleLogger logger;
		
		public KnowledgeRuntimeConsoleLoggerWrapper(WorkingMemoryConsoleLogger logger) {
			// this.logger = logger;
		}
		
		public void close() {
			// Do nothing
		}

	}

}
