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

package org.jbpm.client;

import org.apache.mina.core.session.IoSession;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.BaseMinaHandler;

public class KnowledgeBaseMinaHandler extends BaseMinaHandler {

	private TaskClient client;

    public TaskClient getClient() {
        return client;
    }

    public void setClient(TaskClient client) {
        this.client = client;
    }

    @Override
    public void exceptionCaught(IoSession session,
                                Throwable cause) throws Exception {
        //cause.printStackTrace();
        if ( !session.isConnected() ) {
            client.connect();
        }
    }

    @Override
    public void messageReceived(IoSession session,
                                Object message) throws Exception {
        KnowledgeBaseCommand cmd = (KnowledgeBaseCommand) message;
        switch ( cmd.getName() ) {
//            case  : {
//                Task task = (Task) cmd.getArguments().get( 0 );
//                AddPackageResponseHandler responseHandler = (AddPackageResponseHandler) responseHandlers.remove( cmd.getId() );
//                if ( responseHandler != null ) {
//                    responseHandler.execute( task );
//                }
//                break;
//            }
            default : {
                
            }            
        }
    }

}