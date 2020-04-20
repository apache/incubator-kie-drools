/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process;

import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.core.node.Trigger;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.impl.AbstractProcess;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METADATA_ACTION;
import static org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * ProcessGenerationTest iterates over all the process files in the project except the
 * ones listed in process-generation-test.skip.txt
 * <p>
 * For each process the test will:
 * <ul>
 *     <li>Parse the XML and generate an instance of the process: Expected</li>
 *     <li>Generate the code from this process and instantiate a process definition using the generated code: Current</li>
 *     <li>Iterate over all the process fields and metadata and assert that current and expected are equivalent</li>
 *     <li>Iterate over all the process' nodes fields and metadata and assert that current and expected are equivalent</li>
 * </ul>
 * <p>
 * Exceptions:
 * <ul>
 *     <li>Version is not set by default in Expected</li>
 *     <li>The node name has a default value for current when not set</li>
 *     <li>Node constraints are ignored</li>
 *     <li>OnEntry/OnExit actions are not yet implemented</li>
 *     <li>Timer Actions are generated differently</li>
 * </ul>
 */
public class ProcessGenerationTest extends AbstractCodegenTest {

    private static final Collection<String> IGNORED_PROCESS_META = Arrays.asList("Definitions", "BPMN.Connections", "ItemDefinitions");
    private static final Path BASE_PATH = Paths.get("src/test/resources");

    static Stream<String> processesProvider() throws IOException {
        Set<String> ignoredFiles = Files.lines(BASE_PATH.resolve("org/kie/kogito/codegen/process/process-generation-test.skip.txt"))
                .collect(Collectors.toSet());
        return Files.find(BASE_PATH, 10, ((path, basicFileAttributes) -> basicFileAttributes.isRegularFile()
                && (ProcessCodegen.SUPPORTED_BPMN_EXTENSIONS.stream().anyMatch(ext -> path.getFileName().toString().endsWith(ext))
                || ProcessCodegen.SUPPORTED_SW_EXTENSIONS.keySet().stream().anyMatch(ext -> path.getFileName().toString().endsWith(ext)))))
                .map(BASE_PATH::relativize)
                .map(Path::toString)
                .filter(p -> !ignoredFiles.contains(p));
    }

    @ParameterizedTest
    @MethodSource("processesProvider")
    public void testProcessGeneration(String processFile) throws Exception {
        List<org.kie.api.definition.process.Process> processes = ProcessCodegen.parseProcesses(Stream.of(processFile)
                .map(resource -> new File(BASE_PATH.toString(), resource))
                .collect(Collectors.toList()));
        RuleFlowProcess expected = (RuleFlowProcess) processes.get(0);

        Application app = generateCodeProcessesOnly(processFile);
        AbstractProcess<? extends Model> process = (AbstractProcess<? extends Model>) app.processes().processById(expected.getId());
        RuleFlowProcess current = (RuleFlowProcess) process.legacyProcess();

        assertNotNull(current);
        assertEquals(expected.getId(), current.getId(), "Id");
        assertEquals(expected.getName(), current.getName(), "Name");
        assertEquals(expected.getPackageName(), current.getPackageName(), "PackageName");
        assertEquals(expected.getVisibility(), current.getVisibility(), "Visibility");
        assertEquals(expected.getType(), current.getType(), "Type");
        assertEquals(expected.isAutoComplete(), current.isAutoComplete(), "AutoComplete");
        assertEquals(expected.isDynamic(), current.isDynamic(), "Dynamic");
        if (expected.getVersion() != null) {
            assertEquals(expected.getVersion(), current.getVersion());
        } else {
            assertEquals("1.0", current.getVersion());
        }
        assertEquals(expected.getImports(), current.getImports(), "Imports");
        assertEquals(expected.getFunctionImports(), current.getFunctionImports(), "FunctionImports");
        assertMetadata(expected.getMetaData(), current.getMetaData(), IGNORED_PROCESS_META);

        assertNodes(expected.getNodes(), current.getNodes());
    }

    @Test
    public void testInvalidProcess() throws Exception {
        try {
            testProcessGeneration("messageevent/EventNodeMalformed.bpmn2");
            fail("Expected ProcessCodegenException");
        } catch (ProcessCodegenException e) {
            assertNotNull(e);
        }
    }

    private static void assertNodes(Node[] expected, Node[] current) {
        assertEquals(expected.length, current.length);
        Stream.of(expected).forEach(eNode -> {
            Optional<Node> cNode = Stream.of(current).filter(c -> c.getId() == eNode.getId()).findFirst();
            assertTrue(cNode.isPresent(), "Missing node " + eNode.getName());
            assertNode(eNode, cNode.get());
        });
    }

    private static final BiConsumer<Node, Node> nodeAsserter = (expected, current) -> {
        assertEquals(expected.getId(), current.getId());
        if (expected.getName() != null) {
            assertEquals(expected.getName(), current.getName());
        } else {
            assertNotNull(current.getName(), current.getClass().getName());
        }
        assertConnections(expected.getIncomingConnections(), current.getIncomingConnections());
        assertConnections(expected.getOutgoingConnections(), current.getOutgoingConnections());
        assertConstraints((NodeImpl) expected, (NodeImpl) current);
    };

    private static final BiConsumer<Node, Node> extendedNodeAsserter = (eNode, cNode) -> {
        assertTrue(ExtendedNodeImpl.class.isAssignableFrom(eNode.getClass()));
        assertTrue(ExtendedNodeImpl.class.isAssignableFrom(cNode.getClass()));
        ExtendedNodeImpl expected = (ExtendedNodeImpl) eNode;
        ExtendedNodeImpl current = (ExtendedNodeImpl) cNode;
        assertActions(eNode, expected, current);
    };

    // onEntry and onExit actions are not yet supported - KOGITO-1709
    private static void assertActions(Node eNode, ExtendedNodeImpl expected, ExtendedNodeImpl current) {
        for (String actionType : expected.getActionTypes()) {
            List<DroolsAction> expectedActions = expected.getActions(actionType);
            if (eNode instanceof EndNode && expected.getMetaData("TriggerRef") != null) {
                // Generated lambda to publish event for the given variable
                if (expectedActions == null) {
                    expectedActions = new ArrayList<>();
                }
                expectedActions.add(new DroolsAction());
            }
            try {
                if (expected.getActions(actionType) == null) {
                    assertNull(current.getActions(actionType));
                } else {
                    assertNotNull(current.getActions(actionType));
                    // onEntry and onExit actions are not yet supported
//                    assertEquals(expected.getActions(actionType).size(), current.getActions(actionType).size());
//                    assertActions(expected.getActions(actionType), current.getActions(actionType));
                }
            } catch (Throwable e) {
                fail("Actions are not equal for type: " + actionType, e);
            }
        }
    }

    private static final BiConsumer<Node, Node> startNodeAsserter = (eNode, cNode) -> {
        assertEquals(StartNode.class, eNode.getClass());
        assertEquals(StartNode.class, cNode.getClass());
        StartNode expected = (StartNode) eNode;
        StartNode current = (StartNode) cNode;
        assertEquals(expected.isInterrupting(), current.isInterrupting(), "Interrupting");
        assertTriggers(expected.getTriggers(), current.getTriggers());
    };

    private static final BiConsumer<Node, Node> endNodeAsserter = (eNode, cNode) -> {
        assertEquals(EndNode.class, eNode.getClass());
        assertEquals(EndNode.class, cNode.getClass());
        EndNode expected = (EndNode) eNode;
        EndNode current = (EndNode) cNode;
        assertEquals(expected.isTerminate(), current.isTerminate(), "Terminate");
    };

    private static final BiConsumer<Node, Node> stateBasedNodeAsserter = (eNode, cNode) -> {
        assertTrue(StateBasedNode.class.isAssignableFrom(eNode.getClass()));
        assertTrue(StateBasedNode.class.isAssignableFrom(cNode.getClass()));
        StateBasedNode expected = (StateBasedNode) eNode;
        StateBasedNode current = (StateBasedNode) cNode;
        assertEquals(expected.getBoundaryEvents(), current.getBoundaryEvents(), "BoundaryEvents");
        assertTimers(expected.getTimers(), current.getTimers());
    };

    private static final BiConsumer<Node, Node> workItemNodeAsserter = (eNode, cNode) -> {
        assertTrue(WorkItemNode.class.isAssignableFrom(eNode.getClass()));
        assertTrue(WorkItemNode.class.isAssignableFrom(cNode.getClass()));
        WorkItemNode expected = (WorkItemNode) eNode;
        WorkItemNode current = (WorkItemNode) cNode;
        assertEquals(expected.isWaitForCompletion(), current.isWaitForCompletion(), "WaitForCompletion");
        assertEquals(expected.getInMappings().size(), current.getInMappings().size(), "inMappings");
        expected.getInMappings().forEach((k, v) -> assertEquals(v, current.getInMapping(k), "inMapping " + k));
        assertEquals(expected.getOutMappings().size(), current.getOutMappings().size(), "outMappings");
        expected.getOutMappings().forEach((k, v) -> assertEquals(v, current.getOutMapping(k), "outMapping " + k));

    };

    private static final BiConsumer<Node, Node> humanTaskNodeAsserter = (eNode, cNode) -> {
        assertEquals(HumanTaskNode.class, eNode.getClass());
        assertEquals(HumanTaskNode.class, cNode.getClass());
        HumanTaskNode expected = (HumanTaskNode) eNode;
        HumanTaskNode current = (HumanTaskNode) cNode;
        assertEquals(expected.getSwimlane(), current.getSwimlane(), "Swimlane");
    };

    private static final BiConsumer<Node, Node> eventNodeAsserter = (eNode, cNode) -> {
        assertTrue(EventNode.class.isAssignableFrom(eNode.getClass()));
        assertTrue(EventNode.class.isAssignableFrom(cNode.getClass()));
        EventNode expected = (EventNode) eNode;
        EventNode current = (EventNode) cNode;
        assertEquals(expected.getScope(), current.getScope(), "Scope");
        assertEquals(expected.getType(), current.getType(), "Type");
        assertEquals(expected.getVariableName(), current.getVariableName(), "VariableName");
        assertEquals(expected.getEventFilters().size(), current.getEventFilters().size(), "EventFilters");
    };

    private static final BiConsumer<Node, Node> boundaryEventNodeAsserter = (eNode, cNode) -> {
        assertEquals(BoundaryEventNode.class, eNode.getClass());
        assertEquals(BoundaryEventNode.class, cNode.getClass());
        BoundaryEventNode expected = (BoundaryEventNode) eNode;
        BoundaryEventNode current = (BoundaryEventNode) cNode;
        assertEquals(expected.getAttachedToNodeId(), current.getAttachedToNodeId(), "AttachedToNodeId");
    };

    private static final BiConsumer<Node, Node> splitNodeAsserter = (eNode, cNode) -> {
        assertEquals(Split.class, eNode.getClass());
        assertEquals(Split.class, cNode.getClass());
        Split expected = (Split) eNode;
        Split current = (Split) cNode;
        assertEquals(expected.getType(), current.getType(), "Type");
    };

    private static final BiConsumer<Node, Node> joinNodeAsserter = (eNode, cNode) -> {
        assertEquals(Join.class, eNode.getClass());
        assertEquals(Join.class, cNode.getClass());
        Join expected = (Join) eNode;
        Join current = (Join) cNode;
        assertEquals(expected.getType(), current.getType(), "Type");
        assertEquals(expected.getN(), current.getN(), "N");
    };

    private static final BiConsumer<Node, Node> actionNodeAsserter = (eNode, cNode) -> {
        assertEquals(ActionNode.class, eNode.getClass());
        assertEquals(ActionNode.class, cNode.getClass());
        ActionNode expected = (ActionNode) eNode;
        ActionNode current = (ActionNode) cNode;
        if (expected.getAction() != null) {
            assertNotNull(current.getAction());
            assertEquals(expected.getAction().getName(), current.getAction().getName(), METADATA_ACTION);
        }
    };

    private static final BiConsumer<Node, Node> milestoneNodeAsserter = (eNode, cNode) -> {
        assertEquals(MilestoneNode.class, eNode.getClass());
        assertEquals(MilestoneNode.class, cNode.getClass());
        MilestoneNode expected = (MilestoneNode) eNode;
        MilestoneNode current = (MilestoneNode) cNode;
        assertEquals(expected.getConstraint(), current.getConstraint(), "Constraint");
        assertEquals(expected.getMatchVariable(), current.getMatchVariable(), "MatchVariable");
    };

    private static final BiConsumer<Node, Node> compositeNodeAsserter = (eNode, cNode) -> {
        assertTrue(CompositeNode.class.isAssignableFrom(eNode.getClass()));
        assertTrue(CompositeNode.class.isAssignableFrom(cNode.getClass()));
        CompositeNode expected = (CompositeNode) eNode;
        CompositeNode current = (CompositeNode) cNode;
        assertNodes(expected.getNodes(), current.getNodes());
    };

    private static final Map<Class<? extends Node>, BiConsumer<Node, Node>> nodeAsserters = new HashMap<>();

    static {
        nodeAsserters.put(NodeImpl.class, nodeAsserter);
        nodeAsserters.put(ExtendedNodeImpl.class, extendedNodeAsserter);
        nodeAsserters.put(StartNode.class, startNodeAsserter);
        nodeAsserters.put(EndNode.class, endNodeAsserter);
        nodeAsserters.put(Split.class, splitNodeAsserter);
        nodeAsserters.put(Join.class, joinNodeAsserter);
        nodeAsserters.put(StateBasedNode.class, stateBasedNodeAsserter);
        nodeAsserters.put(WorkItemNode.class, workItemNodeAsserter);
        nodeAsserters.put(HumanTaskNode.class, humanTaskNodeAsserter);
        nodeAsserters.put(EventNode.class, eventNodeAsserter);
        nodeAsserters.put(BoundaryEventNode.class, boundaryEventNodeAsserter);
        nodeAsserters.put(ActionNode.class, actionNodeAsserter);
        nodeAsserters.put(MilestoneNode.class, milestoneNodeAsserter);
        nodeAsserters.put(CompositeNode.class, compositeNodeAsserter);
    }

    private static void assertNode(Node expected, Node current) {
        nodeAsserters.keySet()
                .stream()
                .filter(clazz -> clazz.isAssignableFrom(expected.getClass()))
                .forEach(clazz -> {
                    try {
                        nodeAsserters.get(clazz).accept(expected, current);
                    } catch (Throwable e) {
                        fail(String.format("[%s] nodes with name [%s] are not equal", expected.getClass().getSimpleName(), current.getName()), e);
                    }
                });
    }

    private static void assertMetadata(Map<String, Object> expected, Map<String, Object> current, Collection<String> ignoredKeys) {
        if (expected == null) {
            assertNull(current);
            return;
        }
        assertNotNull(current);
        assertEquals(expected.keySet()
                .stream()
                .filter(k -> ignoredKeys == null || !ignoredKeys.contains(k))
                .count(), current.size());
        expected.keySet()
                .stream()
                .filter(k -> ignoredKeys == null || !ignoredKeys.contains(k))
                .forEach(k -> assertEquals(expected.get(k), current.get(k), "Metadata " + k));
    }

    private static void assertConnections(Map<String, List<Connection>> expectedConnections, Map<String, List<Connection>> currentConnections) {
        assertEquals(expectedConnections.size(), currentConnections.size());
        expectedConnections.forEach((type, expectedByType) -> {
            assertTrue(currentConnections.containsKey(type), "Node does not have connections of type: " + type);
            List<Connection> currentByType = currentConnections.get(type);
            expectedByType.forEach(expected -> {
                Optional<Connection> current = currentByType
                        .stream()
                        .filter(c -> expected.getMetaData().isEmpty() || expected.getMetaData().get("UniqueId").equals(c.getMetaData().get("UniqueId")))
                        .findFirst();
                assertTrue(current.isPresent(), "Connection is present for " + expected.getMetaData().get("UniqueId"));
                assertEquals(expected.getFromType(), current.get().getFromType(), "FromType");
                assertEquals(expected.getFrom().getId(), current.get().getFrom().getId(), "From.Id");
                assertEquals(expected.getToType(), current.get().getToType(), "ToType");
                assertEquals(expected.getTo().getId(), current.get().getTo().getId(), "To.Id");
            });
        });
    }

    private static void assertTriggers(List<Trigger> expected, List<Trigger> current) {
        try {
            if (expected == null) {
                assertNull(current);
                return;
            }
            assertNotNull(current);
            assertEquals(expected.size(), current.size());
            for (int i = 0; i < expected.size(); i++) {
                Trigger e = expected.get(i);
                Trigger c = current.get(i);
                e.getInMappings().forEach((k, v) -> assertEquals(v, c.getInMapping(k), "InMapping for " + k));
                assertDataAssociations(e.getInAssociations(), c.getInAssociations());
            }
        } catch (Throwable e) {
            fail("Triggers are not equal", e);
        }
    }

    private static void assertDataAssociations(List<DataAssociation> expected, List<DataAssociation> current) {
        if (expected == null) {
            assertNull(current);
            return;
        }
        if (expected.isEmpty()) {
            assertEquals(1, current.size());
            assertEquals(1, current.get(0).getSources().size());
            assertEquals("", current.get(0).getSources().get(0));
        } else {
            assertEquals(expected.size(), current.size());
            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i).getSources(), current.get(i).getSources(), "Sources");
                assertEquals(expected.get(i).getTarget(), current.get(i).getTarget(), "Target");
                assertEquals(expected.get(i).getTransformation(), current.get(i).getTransformation(), "Transformation");
                assertAssignments(expected.get(i).getAssignments(), current.get(i).getAssignments());
            }
        }
    }

    private static void assertAssignments(List<Assignment> expected, List<Assignment> current) {
        if (expected == null) {
            assertNull(current);
            return;
        }
        assertEquals(expected.size(), current.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getFrom(), current.get(i).getFrom(), "From");
            assertEquals(expected.get(i).getDialect(), current.get(i).getDialect(), "Dialect");
            assertEquals(expected.get(i).getTo(), current.get(i).getTo(), "To");
        }
    }

    private static void assertTimers(Map<Timer, DroolsAction> expected, Map<Timer, DroolsAction> current) {
        if (expected == null) {
            assertNull(current);
            return;
        }
        assertNotNull(current);
        assertEquals(expected.size(), current.size(), "Size");
        expected.forEach((expectedTimer, expectedAction) -> {
            Optional<Timer> currentTimer = current.keySet().stream().filter(c -> c.getId() == expectedTimer.getId()).findFirst();
            assertTrue(currentTimer.isPresent());
            assertEquals(expectedTimer.getPeriod(), currentTimer.get().getPeriod(), "Period");
            assertEquals(expectedTimer.getDate(), currentTimer.get().getDate(), "Date");
            assertEquals(expectedTimer.getDelay(), currentTimer.get().getDelay(), "Delay");
            assertEquals(expectedTimer.getTimeType(), currentTimer.get().getTimeType(), "TimeType");
            DroolsAction currentAction = current.get(currentTimer.get());
            if (expectedAction == null) {
                assertNull(currentAction);
                return;
            }
            assertNotNull(currentAction);
            assertEquals(expectedAction.getName(), currentAction.getName(), "DroolsAction name");
            if (expectedAction.getMetaData(METADATA_ACTION) == null) {
                assertNull(currentAction.getMetaData(METADATA_ACTION));
            } else {
                assertNotNull(currentAction.getMetaData(METADATA_ACTION));
            }
        });
    }

    private static void assertConstraints(NodeImpl eNode, NodeImpl cNode) {
        if (eNode instanceof Split && ((Split) eNode).getType() != Split.TYPE_OR && ((Split) eNode).getType() != Split.TYPE_XOR) {
            return;
        }
        if (eNode.getConstraints() == null) {
            assertNull(cNode.getConstraints());
            return;
        }
        Map<ConnectionRef, Constraint> expected = eNode.getConstraints();
        Map<ConnectionRef, Constraint> current = cNode.getConstraints();
        assertEquals(expected.size(), current.size());
        expected.forEach((conn, constraint) -> {
            Optional<Map.Entry<ConnectionRef, Constraint>> currentEntry = current.entrySet()
                    .stream()
                    .filter(e -> e.getKey().getConnectionId().equals(conn.getConnectionId()))
                    .findFirst();
            assertTrue(currentEntry.isPresent());
            ConnectionRef currentConn = currentEntry.get().getKey();
            assertEquals(conn.getNodeId(), currentConn.getNodeId());
            assertEquals(conn.getToType(), currentConn.getToType());
            Constraint currentConstraint = currentEntry.get().getValue();
            if (constraint == null) {
                assertNull(currentConstraint);
            } else {
                assertNotNull(currentConstraint);
                assertEquals(constraint.getPriority(), currentConstraint.getPriority());
                assertEquals(constraint.getDialect(), currentConstraint.getDialect());
                assertEquals(conn.getConnectionId(), currentConstraint.getName());
                assertEquals(CONNECTION_DEFAULT_TYPE, currentConstraint.getType());
            }
        });
    }
}
