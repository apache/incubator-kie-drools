/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl;

import org.drools.core.audit.KnowledgeRuntimeLoggerProviderImpl;
import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.impl.adapters.KnowledgeRuntimeLoggerAdapter;
import org.drools.impl.adapters.StatefulKnowledgeSessionAdapter;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactoryService;
import org.kie.api.event.KieRuntimeEventManager;

public class KnowledgeRuntimeLoggerFactoryServiceImpl implements KnowledgeRuntimeLoggerFactoryService {

	private final org.drools.core.audit.KnowledgeRuntimeLoggerProviderImpl delegate = new KnowledgeRuntimeLoggerProviderImpl();
	
	public KnowledgeRuntimeLogger newFileLogger(
			KnowledgeRuntimeEventManager session, String fileName) {
		return adaptLogger(delegate.newFileLogger(((StatefulKnowledgeSessionAdapter) session).delegate, fileName));
	}

	@Override
	public KnowledgeRuntimeLogger newThreadedFileLogger(
			KnowledgeRuntimeEventManager session, String fileName, int interval) {
		return adaptLogger(delegate.newThreadedFileLogger(((StatefulKnowledgeSessionAdapter) session).delegate, fileName, interval));
	}

	@Override
	public KnowledgeRuntimeLogger newConsoleLogger(
			KnowledgeRuntimeEventManager session) {
		// TODO Auto-generated method stub
		return adaptLogger(delegate.newConsoleLogger(((StatefulKnowledgeSessionAdapter) session).delegate));
	}
	
	public static KnowledgeRuntimeLogger adaptLogger(org.kie.internal.logger.KnowledgeRuntimeLogger logger) {
		return new KnowledgeRuntimeLoggerAdapter(logger);
	}

}
