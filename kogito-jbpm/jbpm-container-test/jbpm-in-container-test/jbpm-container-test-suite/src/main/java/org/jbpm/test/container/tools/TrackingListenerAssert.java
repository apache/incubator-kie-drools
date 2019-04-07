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

package org.jbpm.test.container.tools;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.test.container.listeners.TrackingProcessEventListener;

/**
 * Asserts usable to find out if/how many times were nodes in process visited.
 * Works with TrackingProcessEventListener. If correct order of visited nodes is
 * known use <link>IterableListenerAssert</link>.
 */
public class TrackingListenerAssert {

    public static void assertTriggeredAndLeft(TrackingProcessEventListener listener, String nodeName) {
        assertTriggered(listener, nodeName);
        assertLeft(listener, nodeName);
    }

    public static void assertTriggered(TrackingProcessEventListener listener, String nodeName) {
        Assertions.assertThat(listener.wasNodeTriggered(nodeName)).isTrue();
    }

    public static void assertLeft(TrackingProcessEventListener listener, String nodeName) {
        Assertions.assertThat(listener.wasNodeLeft(nodeName)).isTrue();
    }

    public static void assertProcessStarted(TrackingProcessEventListener listener, String processId) {
        Assertions.assertThat(listener.wasProcessStarted(processId)).isTrue();
    }

    public static void assertProcessCompleted(TrackingProcessEventListener listener, String processId) {
        Assertions.assertThat(listener.wasProcessCompleted(processId)).isTrue();
    }

    public static void assertChangedVariable(TrackingProcessEventListener listener, String variableId) {
        Assertions.assertThat(listener.wasVariableChanged(variableId)).isTrue();
    }

    /**
     * Asserts that the node with the given name was triggered <i>count</i>
     * times
     * 
     * @param listener
     *            process event listener
     * @param nodeName
     *            name of the node which is tested
     * @param count
     *            how many times is expected the node had been triggered
     */
    public static void assertTriggered(TrackingProcessEventListener listener, String nodeName, int count) {
        Assertions.assertThat(containsNode(listener.getNodesTriggered(), nodeName)).isEqualTo(count);
    }

    /**
     * Asserts that the node with the given name was left <i>count</i> times
     * 
     * @param listener
     *            process event listener
     * @param nodeName
     *            name of the node which is tested
     * @param count
     *            how many times is expected the node had been left
     */
    public static void assertLeft(TrackingProcessEventListener listener, String nodeName, int count) {
        Assertions.assertThat(containsNode(listener.getNodesLeft(), nodeName)).isEqualTo(count);
    }

    /**
     * @return number of node's occurrences in given list
     */
    private static int containsNode(List<String> nodes, String nodeName) {
        int count = 0;
        for (String node : nodes) {
            if (node.equals(nodeName))
                count++;
        }
        return count;
    }
}
