package org.kie.kogito.index.event.mapper;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.storage.merger.ProcessInstanceNodeDataEventMerger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER;
import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_EXIT;
import static org.kie.kogito.index.DateTimeUtils.toZonedDateTime;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProcessInstanceNodeDataEventMergerTest {

    private static final String HIRING_PROCESS_ID = "hiring";
    private static final String PROCESS_INSTANCE_ID = "7b8ea46e-ffe7-4fdd-8cf5-72a3f0353947";

    private static final String START_NODE_INSTANCE_ID = "35d525eb-868a-4056-91cc-dbc3804d157c";
    private static final String START = "Start";
    private static final String START_NODE_DEFINITION_ID = "_1639F738-45F3-4CD6-A80E-CCEBAA605D56";
    private static final String START_NODE_TYPE = "StartNode";

    private static final String ACTION_NODE_INSTANCE_ID = "08ee9c9c-cf67-47e5-a7e6-901a6f556c0a";
    private static final String ACTION_NODE = "New Hiring";
    private static final String ACTION_NODE_DEFINITION_ID = "_5BDBE48C-CC83-46A9-9D56-F846F8FC1045";
    private static final String ACTION_NODE_TYPE = "ActionNode";

    private static final String BOUNDARY_EVENT_NODE_INSTANCE_ID = "f22510d2-e0e5-460a-901e-494cda3dea26";
    private static final String BOUNDARY_EVENT_NODE_NODE = "BoundaryEvent";
    private static final String BOUNDARY_EVENT_NODE_NODE_DEFINITION_ID = "_116F3C54-A10E-4952-9E08-1CACE74CED0B";
    private static final String BOUNDARY_EVENT_NODE_NODE_TYPE = "BoundaryEventNode";

    private ProcessInstanceNodeDataEventMerger merger;
    private ProcessInstance pi;

    @BeforeEach
    public void setup() {
        merger = new ProcessInstanceNodeDataEventMerger();
        pi = new ProcessInstance();
        pi.setProcessId(HIRING_PROCESS_ID);
        pi.setProcessName(HIRING_PROCESS_ID);
        pi.setId(PROCESS_INSTANCE_ID);
        pi.setVersion("1.0");
        pi.setStart(ZonedDateTime.now());
        pi.setState(1);
    }

    @Test
    public void testMergeProcessInstanceNodeDataEvents() {
        ProcessInstanceNodeDataEvent startNodeEnter = buildNodeEvent(START_NODE_INSTANCE_ID, START, START_NODE_DEFINITION_ID, START_NODE_TYPE, EVENT_TYPE_ENTER);

        merger.merge(pi, startNodeEnter);

        ProcessInstanceNodeDataEvent startNodeExit = buildNodeEvent(START_NODE_INSTANCE_ID, START, START_NODE_DEFINITION_ID, START_NODE_TYPE, EVENT_TYPE_EXIT);

        merger.merge(pi, startNodeExit);

        ProcessInstanceNodeDataEvent actionNodeEnter = buildNodeEvent(ACTION_NODE_INSTANCE_ID, ACTION_NODE, ACTION_NODE_DEFINITION_ID, ACTION_NODE_TYPE, EVENT_TYPE_ENTER);

        merger.merge(pi, actionNodeEnter);

        ProcessInstanceNodeDataEvent actionNodeExit = buildNodeEvent(ACTION_NODE_INSTANCE_ID, ACTION_NODE, ACTION_NODE_DEFINITION_ID, ACTION_NODE_TYPE, EVENT_TYPE_EXIT);

        merger.merge(pi, actionNodeExit);

        assertThat(pi.getNodes()).hasSize(2);

        verifyNode(pi.getNodes().get(0), startNodeEnter.getData(), startNodeEnter.getData().getEventDate(), startNodeExit.getData().getEventDate());
        verifyNode(pi.getNodes().get(1), actionNodeEnter.getData(), actionNodeEnter.getData().getEventDate(), actionNodeExit.getData().getEventDate());
    }

    @Test
    public void testMergeBoundaryNodeExitEvent() {
        ProcessInstanceNodeDataEvent boundaryEvent =
                buildNodeEvent(BOUNDARY_EVENT_NODE_INSTANCE_ID, BOUNDARY_EVENT_NODE_NODE, BOUNDARY_EVENT_NODE_NODE_DEFINITION_ID, BOUNDARY_EVENT_NODE_NODE_TYPE, EVENT_TYPE_EXIT);

        merger.merge(pi, boundaryEvent);

        assertThat(pi.getNodes()).hasSize(1);

        verifyNode(pi.getNodes().get(0), boundaryEvent.getData(), boundaryEvent.getData().getEventDate(), boundaryEvent.getData().getEventDate());
    }

    private void verifyNode(NodeInstance nodeInstance, ProcessInstanceNodeEventBody eventBody, Date enter, Date exit) {
        assertThat(nodeInstance)
                .hasFieldOrPropertyWithValue("id", eventBody.getNodeInstanceId())
                .hasFieldOrPropertyWithValue("nodeId", eventBody.getNodeDefinitionId())
                .hasFieldOrPropertyWithValue("definitionId", eventBody.getNodeDefinitionId())
                .hasFieldOrPropertyWithValue("name", eventBody.getNodeName())
                .hasFieldOrPropertyWithValue("type", eventBody.getNodeType())
                .hasFieldOrPropertyWithValue("enter", toZonedDateTime(enter))
                .hasFieldOrPropertyWithValue("exit", toZonedDateTime(exit));
    }

    private ProcessInstanceNodeDataEvent buildNodeEvent(String nodeInstanceId, String nodeName, String nodeDefinitionId, String nodeType, Integer eventType) {
        return new ProcessInstanceNodeDataEvent(HIRING_PROCESS_ID, "", "", new HashMap<>(), ProcessInstanceNodeEventBody.create()
                .processId(HIRING_PROCESS_ID)
                .processInstanceId(PROCESS_INSTANCE_ID)
                .nodeInstanceId(nodeInstanceId)
                .nodeName(nodeName)
                .nodeDefinitionId(nodeDefinitionId)
                .nodeType(nodeType)
                .eventDate(new Date())
                .eventType(eventType)
                .build());
    }
}
