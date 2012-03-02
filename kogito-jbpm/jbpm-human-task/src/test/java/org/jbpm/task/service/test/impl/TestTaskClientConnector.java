/**
 * Copyright 2010 JBoss Inc
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

import static org.jbpm.task.service.test.impl.TestServerUtil.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbpm.task.service.BaseClientHandler;
import org.jbpm.task.service.BaseHandler;
import org.jbpm.task.service.TaskClientConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestTaskClientConnector implements TaskClientConnector {

	private static final Logger logger = LoggerFactory.getLogger(TestTaskClientConnector.class);

	private volatile AtomicBoolean running;
	private volatile CountDownLatch finished;
	
	private BaseClientHandler handler;
	private String name;   
	private AtomicInteger counter;

	private BlockingQueue<byte []> producer;
	private BlockingQueue<byte []> consumer;

	public TestTaskClientConnector(String name, BaseClientHandler handler) {
		if (name == null) {
			throw new IllegalArgumentException("Name can not be null");
		}
		this.name = name;
		this.handler = handler;
		this.counter = new AtomicInteger();
	}

	public boolean connect(String address, int port) {
		return connect();
	}

	public boolean connect() {
	            
	    Thread responsesThread = new Thread(new Runnable() {

	        public void run() {
	            try {
	                while ( running.get() ) {
	                    byte [] messageBytes = consumer.take();
	                    if (messageBytes != null && messageBytes.length > 0) {
	                        pause();
	                        Object serverMessage = deserialize(messageBytes);
	                        ((TestTaskClientHandler) handler).messageReceived(producer, serverMessage);
	                    } 
	                }
	                finished.countDown();
	            } catch (Exception e) {
	                e.printStackTrace();
	                logger.info(e.getMessage());
	                throw new RuntimeException("Client Exception with class " + getClass(), e);
	            }
	        }
	        
	    });
	    
	    responsesThread.start();
	    
	    return true;
	}

	public void disconnect() throws Exception {
	    consumer.put((new byte [] {}));
	}

	public void write(Object object) {
	    try { 
	        pause();
	        byte [] messageBytes = serialize(object);
			producer.put(messageBytes);
		} catch (Exception e) {
			throw new RuntimeException("Error writing message", e);
		}
	}

	public BaseHandler getHandler() {
		return handler;
	}

	public String getName() {
		return name;
	}

	public AtomicInteger getCounter() {
		return counter;
	}
	
	public void setQueues(BlockingQueue<byte []> [] queues) { 
	   producer = queues[0];
	   consumer = queues[1];
	}
	
	public void setFlags(Object [] flags) { 
	   running = (AtomicBoolean) flags[0]; 
	   finished = (CountDownLatch) flags[1]; 
	}

}
