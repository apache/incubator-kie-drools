/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task.service.test.impl;

import static org.jbpm.task.service.test.impl.TestServerUtil.deserialize;
import static org.jbpm.task.service.test.impl.TestServerUtil.pause;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jbpm.task.service.TaskServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTestTaskServer extends TaskServer {

    private static final Logger logger = LoggerFactory.getLogger(TaskServer.class);

    private volatile AtomicBoolean running = new AtomicBoolean();
    private volatile CountDownLatch finished;
    private boolean latchGiven = false;
    
    private BlockingQueue<byte []> consumer;
    private BlockingQueue<byte []> producer;

    private TestTaskServerHandler handler;
    
    private final boolean defaultToSequentialOperation = true;
    
    BaseTestTaskServer(TestTaskServerHandler handler) { 
       this(handler, null);     
    }
    
    BaseTestTaskServer(TestTaskServerHandler handler, Boolean sequentialOperation) {
        if( sequentialOperation == null ) { 
            sequentialOperation = defaultToSequentialOperation;
        }
        
        if( sequentialOperation ) { 
            consumer = new ArrayBlockingQueue<byte []>(1, true);
            producer = new ArrayBlockingQueue<byte []>(1, true);
        }
        else { 
            consumer = new LinkedBlockingQueue<byte []>();
            producer = new LinkedBlockingQueue<byte []>();
        }
        this.handler = handler;
    }
    
    public void run() {
        try { 
            start();
            while ( running.get() ) {
                byte [] messageBytes = consumer.take();
                if( messageBytes != null && messageBytes.length > 0) { 
                    pause();
                    Object message = deserialize(messageBytes);
                    handler.messageReceived(producer, message);
                }
            }
            finished.countDown();
        } catch( Exception e ) { 
            e.printStackTrace();
            logger.warn("Server failed.", e);
            throw new RuntimeException("Test Server failed.", e);
        }
    }

    public void start() {
        
        synchronized(running) { 
            running.set(true);
            running.notifyAll();
        }
    }
    
    public void stop() {
        running.set(false);
        
        try {
            consumer.clear();
            consumer.put((new byte [] {}));
            consumer = null;
        
            producer.clear();
            long numClients = finished.getCount(); 
            for( int i = 0; i < numClients; ++i ) { 
                producer.offer((new byte [] {}));
            }
            
            if( latchGiven ) { 
                boolean done = false;
                while( !done ) { 
                    try { 
                        finished.await(2, TimeUnit.SECONDS);
                        done = true;
                    } catch( InterruptedException e ) { 
                        // do nothing
                    }
                    for( int i = 0; i < numClients; ++i ) { 
                        producer.offer((new byte [] {}));
                    }
                }
            }
            producer = null;
        } catch (InterruptedException e) {
            // We're done regardless of what happens here. 
        }
        
        handler = null;
    }
    
    public boolean isRunning() {
		return running.get();
	}
    
    AtomicBoolean getRunningNotifier() { 
        return running;
    }
    
    @SuppressWarnings("unchecked")
    BlockingQueue<byte []> [] getQueues() { 
        return (new BlockingQueue [] { consumer, producer });
    }
    
    Object [] getFlags() { 
        latchGiven = true;
        return (new Object [] { running, finished } );
    }
    
    void setNumberOfClients(final int numClients) { 
        if( finished != null ) { 
            throw new IllegalStateException("The number of clients has already been set for this server.");
        }
        finished = new CountDownLatch(numClients+1);
    }
    
}