package com.myspace.demo;

import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class $Type$MessageConsumer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("MessageConsumer");

    Process<$Type$> process;

    Application application;
    
	public void consume($MessageType$ eventData) {
        try {
    	    final $Type$ model = new $Type$();
            model.set$ModelRef$(eventData);
            org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                ProcessInstance<$Type$> pi = process.createInstance(model);
                pi.start("$Trigger$");  
                
                return null;
            });
        } catch (Exception e) {
            LOGGER.error("Error when consuming message for process {}", process.id(), e);
        }
    }
	    
}