/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.jbpm.executor.api;

/**
 *
 * @author salaboy
 */
public interface Command {
    public ExecutionResults execute(CommandContext ctx) throws Exception;
}
