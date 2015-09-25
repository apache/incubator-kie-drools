/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.kie.services.impl.bpmn2;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;

public class BPMN2DataServiceExtensionSemanticModule extends BPMNExtensionsSemanticModule {

	private static ThreadLocal<ProcessDescRepoHelper> helper = BPMN2DataServiceSemanticModule.helper;
	private ProcessDescriptionRepository repo = null;

    private Map<String, Handler> dataServiceHandlers = new HashMap<String, Handler>();
    private Map<Class<?>, Handler> dataServiceHandlersByClass = new HashMap<Class<?>, Handler>();

    private static final ThreadLocal<Boolean> usedByThisThread = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    private ProcessGetImportHandler processImportHandler = null;
    private ProcessGetGlobalHandler processGlobalHandler = null;

    public BPMN2DataServiceExtensionSemanticModule(BPMN2DataServiceSemanticModule module) {
        super();
        this.repo = module.getRepo();
        processImportHandler = new ProcessGetImportHandler(this);
        processGlobalHandler = new ProcessGetGlobalHandler(this);
        init();
    }

    public void init() {
        addDataServicesHandler("import", processImportHandler);
        addDataServicesHandler("global", processGlobalHandler);
    }

	public ProcessDescRepoHelper getRepoHelper() {
		return helper.get();
	}

	public static void setRepoHelper(ProcessDescRepoHelper repoHelper) {
		helper.set(repoHelper);
	}

	public ProcessDescriptionRepository getRepo() {
		return repo;
	}

	public void setRepo(ProcessDescriptionRepository repo) {
		this.repo = repo;
	}

    public static void setUseByThisThread(boolean use) {
        usedByThisThread.set(use);
    }

    public void addDataServicesHandler( String name, Handler handler ) {
        dataServiceHandlers.put(name, handler);
        if( handler != null && handler.generateNodeFor() != null ) {
            dataServiceHandlersByClass.put(handler.generateNodeFor(), handler);
        }
    }

    @Override
    public Handler getHandler( String name ) {
        Handler handler = null;
        if( usedByThisThread.get() ) {
            handler = dataServiceHandlers.get(name);
        }
        if( handler == null ) {
            handler = handlers.get(name);
        }
        return handler;
    }

    @Override
    public Handler getHandlerByClass( Class<?> clazz ) {
        boolean getDataServicesHandler = usedByThisThread.get();
        Handler handler = null;
        while( clazz != null ) {
            if( getDataServicesHandler ) {
                handler = dataServiceHandlersByClass.get(clazz);
            }
            if( handler == null ) {
                handler = handlersByClass.get(clazz);
            }
            if( handler != null ) {
                return handler;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
