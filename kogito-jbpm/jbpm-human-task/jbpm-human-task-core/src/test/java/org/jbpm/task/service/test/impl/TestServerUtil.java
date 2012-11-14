package org.jbpm.task.service.test.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.kie.SystemEventListenerFactory;
import org.jbpm.task.service.TaskClientConnector;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServerUtil {

    private static final Logger logger = LoggerFactory.getLogger(TestTaskClientConnector.class);
    
    private static final Random random = new Random();
    public static int sleepMax = 1000;
    
    public static enum PauseMode { 
        Random, Constant, None;    
    }
    public static PauseMode PAUSE_MODE = PauseMode.None;
    
    public synchronized static void pause() throws Exception { 
        int sleep = sleepMax;
        switch(PAUSE_MODE) { 
        case Random: 
            sleep = random.nextInt(sleepMax);
        case Constant:
            logger.trace("Sleeping " + (((double) sleep)/1000) );
            Thread.sleep(sleep);
            break;
        case None:
        default:
            // do nothing.
        }
    }

    public static byte [] serialize(Object message) throws Exception { 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(baos);
        oout.writeObject(message);
        oout.close();
        return baos.toByteArray();
    }
    
    public static Object deserialize(byte [] messageBytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(messageBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
    
    public static TaskServer startServer(TaskService taskService) throws Exception { 
       return startServer(taskService, 1);     
    }
    
    public static TaskServer startAsyncServer(TaskService taskService) throws Exception { 
       return startServer(taskService, 1, false);     
    }
    
    public static TaskServer startServer(TaskService taskService, int numClients) throws Exception { 
        return startServer(taskService, numClients, true);
    }
    
    public static TaskServer startAsyncServer(TaskService taskService, int numClients) throws Exception { 
        return startServer(taskService, numClients, false);
    }
    
    public static TaskServer startServer(TaskService taskService, int numClients, boolean synchronous) throws Exception { 
        TestTaskServer server = new TestTaskServer(taskService, synchronous);
        server.setNumberOfClients(numClients);
        
        Thread thread = new Thread(server);
        thread.start();
        
        AtomicBoolean running = ((TestTaskServer) server).getRunningNotifier();
        synchronized(running) { 
            if( ! running.get() ) { 
                running.wait();
            }
        }
        
        return server;
    }
    
    public static TaskClientConnector createTestTaskClientConnector(String clientName, TestTaskServer testServer) { 
        TestTaskClientConnector connector 
            = new TestTaskClientConnector(clientName, 
                                          new TestTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())
                                         );
        connector.setQueues(testServer.getQueues());
        connector.setFlags(testServer.getFlags());
        return connector;
    }
    
    
    
}   