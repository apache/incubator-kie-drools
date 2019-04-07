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

package org.jbpm.kie.services.impl;

import java.util.HashMap;

import org.drools.core.event.ProcessStartedEventImpl;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IdentityProviderAwareProcessListenerTest {

    @Mock
    Environment environment;

    @Mock
    KieSession kSession;

    @InjectMocks
    IdentityProviderAwareProcessListener listener;

    @Before
    public void setup() {
        when(kSession.getEnvironment()).thenReturn(environment);
    }

    @Test
    public void testSetUser() {
        final IdentityProvider identityProvider = mock(IdentityProvider.class);
        final String userId = "userId";
        when(identityProvider.getName()).thenReturn(userId);
        when(environment.get("IdentityProvider")).thenReturn(identityProvider);
        final WorkflowProcessInstance processInstance = mock(WorkflowProcessInstance.class);
        final HashMap<String, Object> metaData = new HashMap<>();
        when(processInstance.getMetaData()).thenReturn(metaData);
        final ProcessStartedEvent event = new ProcessStartedEventImpl(processInstance, mock(KieRuntime.class ));

        listener.beforeProcessStarted(event);

        assertEquals(userId, metaData.get("OwnerId"));
        verify(processInstance).setVariable("initiator", userId);
    }

    @Test
    public void testUserNotSet() {
        final WorkflowProcessInstance processInstance = mock(WorkflowProcessInstance.class);
        final HashMap<String, Object> metaData = new HashMap<>();
        when(processInstance.getMetaData()).thenReturn(metaData);
        final ProcessStartedEvent event = new ProcessStartedEventImpl(processInstance, mock(KieRuntime.class));

        listener.beforeProcessStarted(event);

        assertTrue(metaData.isEmpty());
        verify(processInstance, never()).setVariable(anyString(), anyString());
    }

}
