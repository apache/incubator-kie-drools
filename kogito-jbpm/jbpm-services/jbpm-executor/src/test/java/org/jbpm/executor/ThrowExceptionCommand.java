/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.jbpm.executor;

import javax.inject.Named;

import org.jbpm.executor.api.Command;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salaboy
 */
@Named(value="ThrowExceptionCmd")
public class ThrowExceptionCommand implements Command{
    
    private static final Logger logger = LoggerFactory.getLogger(ThrowExceptionCommand.class);

    public ExecutionResults execute(CommandContext ctx) {
        logger.debug("Hi This is the Exception command!");
        throw new RuntimeException("Test Exception!");        
    }
    
}
