package org.jbpm.kie.services.impl;

import java.util.HashMap;

import org.drools.core.event.ProcessStartedEventImpl;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.KnowledgeRuntime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
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
        final ProcessStartedEvent event = new ProcessStartedEventImpl(processInstance, mock(KnowledgeRuntime.class));

        listener.beforeProcessStarted(event);

        assertEquals(userId, metaData.get("OwnerId"));
        verify(processInstance).setVariable("initiator", userId);
    }

    @Test
    public void testUserNotSet() {
        final WorkflowProcessInstance processInstance = mock(WorkflowProcessInstance.class);
        final HashMap<String, Object> metaData = new HashMap<>();
        when(processInstance.getMetaData()).thenReturn(metaData);
        final ProcessStartedEvent event = new ProcessStartedEventImpl(processInstance, mock(KnowledgeRuntime.class));

        listener.beforeProcessStarted(event);

        assertTrue(metaData.isEmpty());
        verify(processInstance, never()).setVariable(anyString(), anyString());
    }

}
