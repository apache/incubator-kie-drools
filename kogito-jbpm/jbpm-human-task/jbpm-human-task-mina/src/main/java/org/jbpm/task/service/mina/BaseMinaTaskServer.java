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

package org.jbpm.task.service.mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.jbpm.task.service.TaskServer;

public abstract class BaseMinaTaskServer extends TaskServer {
    private final int port;

    IoHandlerAdapter  handler;

    IoAcceptor        acceptor;

    volatile boolean  running;
    
    //the local interface to be used. Default is loopback. To bind all
    // interfaces, use 0.0.0.0
    String localInterface;

    public BaseMinaTaskServer(IoHandlerAdapter handler,
                          int port) {
        this(handler, port, "127.0.0.1");
    }
    
	public BaseMinaTaskServer(IoHandlerAdapter handler, int port,
	        String localInterface) {
		this.handler = handler;
		this.port = port;
		this.localInterface = localInterface;
	}

    public void run() {
        try {
            start();
            while ( running ) {
                Thread.sleep( 100 );
            }
        } catch ( Exception e ) {
        	if (e instanceof java.net.BindException) {
        		throw new RuntimeException("Could not start human task server, address already in use, is it possible that another instance of the task server is already running?");
        	}
            throw new RuntimeException( "Server Exception with class " + getClass() + " using port " + port,
                                        e );
        }
    }

    public void start() throws IOException {
        
        acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast( "logger",
                                           new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec",
                                           new ProtocolCodecFilter( new ObjectSerializationCodecFactory() ) );

        acceptor.setHandler( handler );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        ((SocketSessionConfig) acceptor.getSessionConfig()).setSoLinger(0);
        acceptor.bind( new InetSocketAddress( localInterface, port ) );
        running = true;
    }
    
    public IoAcceptor getIoAcceptor() {
        return acceptor;
    }

    public void stop() {
    	running = false;
        acceptor.dispose();
    }
    
    public boolean isRunning() {
		return running;
	}
    
}