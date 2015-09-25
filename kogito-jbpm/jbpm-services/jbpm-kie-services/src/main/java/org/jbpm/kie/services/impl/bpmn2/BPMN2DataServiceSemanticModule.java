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
import org.jbpm.bpmn2.xml.BPMNSemanticModule;

public class BPMN2DataServiceSemanticModule extends BPMNSemanticModule {

    // also used by the BPMN2DataServiceExtensionSemanticModule
	static ThreadLocal<ProcessDescRepoHelper> helper = new ThreadLocal<ProcessDescRepoHelper>() {

        /**
         * we override get() instead of initialValue() because we do a helper.set(null) in the dispose.
         * Multiple processes can be processed in the same thread so that the next time get() is called
         * for the 1rst time after set(null), we then need to provide a new {@link ProcessDescRepoHelper} instance.
         */
        @Override
        public ProcessDescRepoHelper get() {
            ProcessDescRepoHelper localHelper = super.get();
            if( localHelper == null ) {
                localHelper = new ProcessDescRepoHelper();
                super.set(localHelper);
            }
            return localHelper;
        }
	};

	private ProcessDescriptionRepository repo = new ProcessDescriptionRepository();

    private Map<String, Handler> dataServiceHandlers = new HashMap<String, Handler>();
    private Map<Class<?>, Handler> dataServiceHandlersByClass = new HashMap<Class<?>, Handler>();

    private static final ThreadLocal<Boolean> usedByThisThread = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    private HumanTaskGetInformationHandler taskHandler = null;
    private ProcessGetInformationHandler processHandler = null;
    private ProcessGetInputHandler processInputHandler = null;
    private GetReusableSubProcessesHandler reusableSubprocessHandler = null;
    private DataServiceItemDefinitionHandler itemDefinitionHandler = null;
    private AbstractTaskGetInformationHandler abstractTaskHandler = null;
    private ProcessGetBusinessRuleHandler businessRuleTaskHandler = null;

    public BPMN2DataServiceSemanticModule() {
        super();
        taskHandler = new HumanTaskGetInformationHandler(this);
        processHandler = new ProcessGetInformationHandler(this);
        processInputHandler = new ProcessGetInputHandler(this);
        reusableSubprocessHandler = new GetReusableSubProcessesHandler(this);
        itemDefinitionHandler = new DataServiceItemDefinitionHandler(this);
        abstractTaskHandler = new AbstractTaskGetInformationHandler(this);
        businessRuleTaskHandler = new ProcessGetBusinessRuleHandler(this);
        init();
    }

    public void setTaskHandler(HumanTaskGetInformationHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    public void setProcessHandler(ProcessGetInformationHandler processHandler) {
        this.processHandler = processHandler;
    }

    public void setProcessInputHandler(ProcessGetInputHandler processInputHandler) {
        this.processInputHandler = processInputHandler;
    }

    public void setReusableSubprocessHandler(GetReusableSubProcessesHandler reusableSubprocessHandler) {
        this.reusableSubprocessHandler = reusableSubprocessHandler;
    }
    public void setItemDefinitionHandler(DataServiceItemDefinitionHandler itemDefinitionHandler) {
        this.itemDefinitionHandler = itemDefinitionHandler;
    }

    public void setAbstractTaskHandler(AbstractTaskGetInformationHandler abstractTaskHandler) {
        this.abstractTaskHandler = abstractTaskHandler;
    }

    public void init() {
        addDataServicesHandler("userTask", taskHandler);
        addDataServicesHandler("process", processHandler);
        addDataServicesHandler("property", processInputHandler);
        addDataServicesHandler("itemDefinition", itemDefinitionHandler);
        addDataServicesHandler("callActivity", reusableSubprocessHandler);
        addDataServicesHandler("task", abstractTaskHandler);
        addDataServicesHandler("businessRuleTask", businessRuleTaskHandler);
    }

    public static ProcessDescRepoHelper getRepoHelper() {
        return helper.get();
    }

	public ProcessDescriptionRepository getRepo() {
		return repo;
	}

	public void setRepo(ProcessDescriptionRepository repo) {
		this.repo = repo;
	}

	public static void dispose() {
		helper.set(null);
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
