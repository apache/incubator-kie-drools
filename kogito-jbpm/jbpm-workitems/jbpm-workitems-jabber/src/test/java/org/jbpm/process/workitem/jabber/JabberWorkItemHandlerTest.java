/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.jabber;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JabberWorkItemHandlerTest {

    @Mock
    ConnectionConfiguration connectionConf;

    @Mock
    XMPPConnection xmppConnection;

    @Mock
    ChatManager chatManager;

    @Mock
    Chat chat;

    @Test
    public void testSendMessage() throws Exception {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        doNothing().when(xmppConnection).connect();
        doNothing().when(xmppConnection).login(anyString(),
                                               anyString());
        doNothing().when(xmppConnection).sendPacket(any(Presence.class));
        doNothing().when(xmppConnection).disconnect();
        when(xmppConnection.getChatManager()).thenReturn(chatManager);
        when(chatManager.createChat(anyString(),
                                    anyObject())).thenReturn(chat);

        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("User",
                              "myuser");
        workItem.setParameter("Password",
                              "mypassword");
        workItem.setParameter("Server",
                              "myserver");
        workItem.setParameter("Port",
                              "123");
        workItem.setParameter("Service",
                              "myservice");
        workItem.setParameter("Text",
                              "hello world");
        workItem.setParameter("To",
                              "someperson");

        JabberWorkItemHandler handler = new JabberWorkItemHandler();
        handler.setConf(connectionConf);
        handler.setConnection(xmppConnection);

        handler.executeWorkItem(workItem,
                                manager);

        assertNotNull(manager.getResults());
        assertEquals(1,
                     manager.getResults().size());
        assertTrue(manager.getResults().containsKey(workItem.getId()));

        verify(chat).sendMessage(messageCaptor.capture());
        assertEquals("hello world",
                     messageCaptor.getValue().getBody());
    }
}
