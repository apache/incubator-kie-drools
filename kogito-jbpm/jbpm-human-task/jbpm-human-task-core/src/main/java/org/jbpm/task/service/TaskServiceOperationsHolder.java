package org.jbpm.task.service;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TaskServiceOperationsHolder {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceOperationsHolder.class);
    
    public final static Map<Operation, List<OperationCommand>> operations = initializeOperations();

    private static Map<Operation, List<OperationCommand>> initializeOperations() {
        Map<Operation, List<OperationCommand>> myOperations = null;
        
        try {
            String operationsDslFileName = "operations-dsl.mvel";
            // Search operations-dsl.mvel, if necessary using superclass if TaskService is subclassed
            InputStream is = null;
            for (Class<?> c = TaskService.class; c != null; c = c.getSuperclass()) {
                is = c.getResourceAsStream(operationsDslFileName);
                if (is != null) {
                    break;
                }
            }
            if (is == null) {
                throw new Exception("Unable to find Operations DSL file (" + operationsDslFileName + ")"); 
            }

            Map<String, Object> vars = new HashMap<String, Object>();
            Reader reader = new InputStreamReader(is);
            try {
                myOperations = (Map<Operation, List<OperationCommand>>) TaskService.eval(TaskService.toString(reader), vars);
            } catch (IOException ioe) {
                throw new Exception("Could not evaluate the Operations DSL file", ioe);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return myOperations;
    }
}