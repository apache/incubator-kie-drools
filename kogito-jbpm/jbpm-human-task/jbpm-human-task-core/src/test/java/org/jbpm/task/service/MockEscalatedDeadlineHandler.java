package org.jbpm.task.service;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.task.Content;
import org.jbpm.task.Deadline;
import org.jbpm.task.Task;

public class MockEscalatedDeadlineHandler implements EscalatedDeadlineHandler {

    List<Item> list = new ArrayList<Item>();
    org.jbpm.task.service.TaskService taskService;

    public void executeEscalatedDeadline(Task task, Deadline deadline, Content content,
            org.jbpm.task.service.TaskService taskService) {
        synchronized(list) { 
            list.add(new Item(task, deadline, content, taskService));
            list.notifyAll();
        }
    }

    public List<Item> getList() {
        return this.list;
    }

    public static class Item {

        Task task;
        Deadline deadline;

        public Item(Task task, Deadline deadline, Content content, org.jbpm.task.service.TaskService taskService) {
            this.deadline = deadline;
            this.task = task;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Deadline getDeadline() {
            return deadline;
        }

        public void setDeadline(Deadline deadline) {
            this.deadline = deadline;
        }
    }

    public synchronized void wait(int totalSize, int totalWaitInMillis) throws Exception {
        long startInMillis = System.currentTimeMillis();
        int size = 0;
        
        int tries = 0;
        while (true) {
            synchronized (list) {
                size = list.size();
                if( list.size() < totalSize ) { 
                    list.wait(totalWaitInMillis);
                }
            }

            long waitInMillis = (System.currentTimeMillis() - startInMillis);
            if (waitInMillis >= (totalWaitInMillis)) {
               if( size >= totalSize ) { 
                   break;
               }
            }
            else { 
                Thread.sleep(totalWaitInMillis-waitInMillis);
            }
        }
    }
    
}
