/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor;

/**
 *
 * @author salaboy
 */
public class ExecutorMain {

    public static void main(String[] args) {
        System.out.println("Starting Executor Service ...");
        final ExecutorServiceEntryPoint executorServiceEntryPoint = ExecutorModule.getInstance().getExecutorServiceEntryPoint();
        executorServiceEntryPoint.init();
        System.out.println("Executor Service Started!");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executorServiceEntryPoint.destroy();
            }
        });
    }
}
