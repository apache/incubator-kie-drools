/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.assembler.DMNResource;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.core.util.DefaultDMNMessagesManager;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

/**
 * A helper class to report messages
 */
public class MessageReporter {
    private final DMNMessageManager messages;
    private final String path;
    
    public MessageReporter(DMNResource dmnResource) {
        Resource resource = dmnResource != null ? dmnResource.getResAndConfig().getResource() : null;
        this.messages = new DefaultDMNMessagesManager(resource);
        this.path = resource != null ? resource.getSourcePath() : null;
    }

    public DMNMessageManager getMessages() {
        return messages;
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message0 message) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message ), message.getType(), source ).withPath(path) );
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message1 message, Object p1) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message, p1 ), message.getType(), source ).withPath(path) );
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message2 message, Object p1, Object p2) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message, p1, p2 ), message.getType(), source ).withPath(path) );
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message3 message, Object p1, Object p2, Object p3) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message, p1, p2, p3 ), message.getType(), source ).withPath(path) );
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message4 message, Object p1, Object p2, Object p3, Object p4) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message, p1, p2, p3, p4 ), message.getType(), source ).withPath(path) );
    }
}
