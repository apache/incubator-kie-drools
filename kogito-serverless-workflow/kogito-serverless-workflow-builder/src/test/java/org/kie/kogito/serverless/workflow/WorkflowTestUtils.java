/*
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
package org.kie.kogito.serverless.workflow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.Split;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class WorkflowTestUtils {

    private WorkflowTestUtils() {
    }

    public static final Path resourceDirectory = Paths.get("src",
            "test",
            "resources");
    public static final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    public static Path getResourcePath(String file) {
        return Paths.get(absolutePath + File.separator + file);
    }

    public static InputStream getInputStreamFromPath(Path path) throws Exception {
        return Files.newInputStream(path);
    }

    public static String readWorkflowFile(String location) {
        return readFileAsString(classpathResourceReader(location));
    }

    public static Reader classpathResourceReader(String location) {
        return new InputStreamReader(WorkflowTestUtils.class.getResourceAsStream(location));
    }

    public static String readFileAsString(Reader reader) {
        try {
            StringBuilder fileData = new StringBuilder(1000);
            char[] buf = new char[1024];
            int numRead;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf,
                        0,
                        numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            return fileData.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertProcessMainParams(RuleFlowProcess process, String id, String name, String version, String pkg, String visibility) {
        assertThat(process.getId()).isEqualTo(id);
        assertThat(process.getName()).isEqualTo(name);
        assertThat(process.getVersion()).isEqualTo(version);
        assertThat(process.getPackageName()).isEqualTo(pkg);
        assertThat(process.getVisibility()).isEqualTo(visibility);
    }

    public static void assertHasName(Node node, String expectedName) {
        assertThat(node.getName())
                .withFailMessage("Node: (%s, %s) is expected to have name: %s", node.getId(), node.getName(), expectedName)
                .isEqualTo(expectedName);
    }

    public static void assertExclusiveSplit(Split splitNode, String name, int constrainsSize) {
        assertHasName(splitNode, name);
        assertThat(splitNode.getConstraints()).hasSize(constrainsSize);
        assertThat(splitNode.getType()).isEqualTo(Split.TYPE_XOR);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Node> T assertClassAndGetNode(NodeContainer nodeContainer, int nodeIndex, Class<T> expectedNodeClass) {
        Node node = nodeContainer.getNodes()[nodeIndex];
        assertThat(nodeContainer.getNodes())
                .withFailMessage("Required nodeIndex: %s is out of range, the nodeContainer.nodes has size: %s.", nodeIndex, nodeContainer.getNodes().length)
                .hasSizeGreaterThan(nodeIndex);
        assertThat(node)
                .withFailMessage("Node at nodeIndex: %s must be of class: %s, but is: %s.",
                        nodeIndex, expectedNodeClass.getName(), node.getClass().getName())
                .isInstanceOf(expectedNodeClass);
        return (T) node;
    }

    public static void assertIsConnected(Node startNode, Node endNode) {
        assertThat(startNode.getOutgoingConnections())
                .withFailMessage("Node: (%s, %s), has no outgoing connections.",
                        startNode.getId(), startNode.getName())
                .hasSizeGreaterThan(0);
        for (List<Connection> connections : startNode.getOutgoingConnections().values()) {
            for (Connection connection : connections) {
                if (connection.getTo() == endNode) {
                    return;
                }
            }
        }
        fail("Node: (%s, %s), is not connected with Node: (%s, %s).",
                startNode.getId(), startNode.getName(), endNode.getId(), endNode.getName());
    }

    public static void assertHasNodesSize(CompositeContextNode compositeContextNode, int expectedSize) {
        assertThat(compositeContextNode.getNodes())
                .withFailMessage("Node: (%s, %s), is expected to have %s nodes, but has %s.",
                        compositeContextNode.getId(), compositeContextNode.getName(), expectedSize, compositeContextNode.getNodes().length)
                .hasSize(expectedSize);
    }

    public static void assertHasNodesSize(RuleFlowProcess process, int expectedSize) {
        assertThat(process.getNodes())
                .withFailMessage("Process: (%s, %s), is expected to have %s nodes, but has %s.",
                        process.getId(), process.getName(), expectedSize, process.getNodes().length)
                .hasSize(expectedSize);
    }

    public static void assertConstraintIsDefault(Split splitNode, String constraintName) {
        Constraint constraint = splitNode.getConstraints().values().stream()
                .flatMap(Collection::stream)
                .filter(c -> constraintName.equals(c.getName()))
                .findFirst().orElse(null);
        assertThat(constraint)
                .withFailMessage("No constraint with name: %s was found for the splitNode: %s",
                        constraintName, splitNode.getId())
                .isNotNull();
        assertThat(Objects.requireNonNull(constraint).isDefault())
                .withFailMessage("Constraint with name: %s is not marked as default", constraintName)
                .isTrue();
    }
}
