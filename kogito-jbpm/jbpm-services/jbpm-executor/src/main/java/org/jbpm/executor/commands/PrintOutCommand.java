/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.jbpm.executor.commands;

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
@Named(value="PrintOutCmd")
public class PrintOutCommand implements Command{
    
    private static final Logger logger = LoggerFactory.getLogger(PrintOutCommand.class);

    public ExecutionResults execute(CommandContext ctx) {
        logger.info("Hi This is the first command!");
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }
    
}
