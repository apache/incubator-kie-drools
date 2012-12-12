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

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.jbpm.task.service.TaskServerHandler;
import org.jbpm.task.service.TaskService;
import org.kie.SystemEventListener;

public class MinaTaskServerHandler extends IoHandlerAdapter {
	
    private TaskServerHandler handler;

    public MinaTaskServerHandler(TaskService service, SystemEventListener systemEventListener) {
        this.handler = new TaskServerHandler(service, systemEventListener);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	handler.exceptionCaught(new MinaSessionWriter(session), cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        handler.messageReceived(new MinaSessionWriter(session), message);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
//    	handler.sessionIdle(session, status);
    }
}