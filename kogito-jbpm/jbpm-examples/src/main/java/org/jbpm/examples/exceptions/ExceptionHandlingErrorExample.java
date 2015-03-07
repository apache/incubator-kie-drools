package org.jbpm.examples.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.jbpm.examples.exceptions.service.ExceptionService;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;

public class ExceptionHandlingErrorExample {

    public static final void main(String[] args) {
        runExample();
    }

    public static ProcessInstance runExample() {
        // load up the knowledge base
        KieSession ksession = createKieSession();

        String eventType = "Error-code";
        SignallingTaskHandlerDecorator signallingTaskWrapper 
            = new SignallingTaskHandlerDecorator(ServiceTaskHandler.class, eventType);
        signallingTaskWrapper.setWorkItemExceptionParameterName(ExceptionService.exceptionParameterName);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", signallingTaskWrapper);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serviceInputItem", "Input to Original Service");
        ProcessInstance processInstance = ksession.startProcess("ProcessWithExceptionHandlingError", params);
        
        return processInstance;
    }

    private static KieSession createKieSession() {
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder()
            .addAsset(KieServices.Factory.get().getResources()
        		.newClassPathResource("exceptions/ExceptionHandlingWithError.bpmn2"), ResourceType.BPMN2)
            .get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment).getRuntimeEngine(null).getKieSession();
    }

}
