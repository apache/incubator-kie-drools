/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl.factories;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.utils.MVELUtils;

/**
 *
 */
public class TaskFactory {
 
    public static TaskImpl evalTask(Reader reader, Map<String, Object> vars) {
        TaskImpl task = null;
        try {
            task = (TaskImpl) MVELUtils.eval(MVELUtils.toString(reader), vars);
           

        } catch (IOException ex) {
            Logger.getLogger(TaskFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return task;
    }

    public static TaskImpl evalTask(String taskString, Map<String, Object> vars) {
        TaskImpl task = (TaskImpl) MVELUtils.eval(taskString, vars);
        
        return task;
    }

    public static TaskImpl evalTask(Reader reader) {
        return evalTask(reader, null);
    }

}
