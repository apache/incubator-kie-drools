package com.myspace.demo;

import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

public class $Type$MessageConsumer {

    Process<$Type$> process;


	public void consume($MessageType$ eventData) {
        $Type$ model = new $Type$();
        model.set$ModelRef$(eventData);

        ProcessInstance<$Type$> pi = process.createInstance(model);
        pi.start();        
    }
	    
}