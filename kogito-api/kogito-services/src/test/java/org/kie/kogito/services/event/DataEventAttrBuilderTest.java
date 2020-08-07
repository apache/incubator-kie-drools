package org.kie.kogito.services.event;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.event.AbstractDataEvent;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataEventAttrBuilderTest {

    @Test
    void verifyEventSourceBeingGenerated() {
        final String processId = "the_cool_project";
        final String instanceId = UUID.randomUUID().toString();
        ProcessInstance pi = mock(ProcessInstance.class);
        when(pi.getProcessId()).thenReturn(processId);
        when(pi.getId()).thenReturn(instanceId);
        final String source = DataEventAttrBuilder.toSource(pi);
        assertThat(source).isNotBlank().contains(processId).contains(instanceId);
    }

    @Test
    void verifyEventTypeBeingGenerated() {
        final String channelName = "github";
        final String processId = "the_cool_project";
        final String type = DataEventAttrBuilder.toType(channelName, processId);
        assertThat(type).isNotBlank().contains(processId).contains(channelName).startsWith(AbstractDataEvent.TYPE_PREFIX);
    }

    @Test
    void verifyEventTypeBeingGeneratedWithProcessInstance() {
        final String channelName = "github";
        final String processId = "COOL_PROJECT";
        ProcessInstance pi = mock(ProcessInstance.class);
        when(pi.getProcessId()).thenReturn(processId);
        final String type = DataEventAttrBuilder.toType(channelName, pi);
        assertThat(type)
                .isNotBlank()
                .doesNotContain(processId)
                .contains(processId.toLowerCase())
                .contains(channelName)
                .startsWith(AbstractDataEvent.TYPE_PREFIX);
    }
}