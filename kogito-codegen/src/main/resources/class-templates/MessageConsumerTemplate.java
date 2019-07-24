package com.myspace.demo;

import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

public class $Type$MessageConsumer {

    Process<$Type$> process;

    Application application;
    
	public void consume($MessageType$ eventData) {
        final $Type$ model = new $Type$();
        model.set$ModelRef$(eventData);
        org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            ProcessInstance<$Type$> pi = process.createInstance(model);
            pi.start();  
            
            return null;
        });
    }
	    
}