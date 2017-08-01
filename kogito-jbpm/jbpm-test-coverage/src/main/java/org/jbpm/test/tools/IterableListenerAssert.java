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

package org.jbpm.test.tools;

import static org.jbpm.test.listener.IterableProcessEventListener.AFTER_VARIABLE;
import static org.jbpm.test.listener.IterableProcessEventListener.BEFORE_COMPLETED;
import static org.jbpm.test.listener.IterableProcessEventListener.BEFORE_LEFT;
import static org.jbpm.test.listener.IterableProcessEventListener.BEFORE_STARTED;
import static org.jbpm.test.listener.IterableProcessEventListener.BEFORE_TRIGGERED;
import static org.jbpm.test.listener.IterableProcessEventListener.BEFORE_VARIABLE;


import org.assertj.core.api.Assertions;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.jbpm.test.listener.IterableProcessEventListener.TrackedEvent;
import org.jbpm.test.listener.IterableProcessEventListener.CachedProcessCompletedEvent;
import org.jbpm.test.listener.IterableProcessEventListener.CachedProcessNodeLeftEvent;
import org.jbpm.test.listener.IterableProcessEventListener.CachedProcessNodeTriggeredEvent;
import org.jbpm.test.listener.IterableProcessEventListener.CachedProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

/**
 * Group of asserts to be used together with IterableProcessEventListener. It
 * helps track the path of the process.
 *
 *
 */
public class IterableListenerAssert {

    /**
     * Asserts that the node with the given name was triggered and left with no
     * action in between
     *
     * @param it       listener that listened to the process
     * @param nodeName name of the node that is expected to pass
     */
    public static void assertNextNode(IterableProcessEventListener it, String nodeName) {
        assertTriggered(it, nodeName);
        assertLeft(it, nodeName);
    }

    /**
     * Asserts that the process was started
     *
     * @param it        listener that listened to the process
     * @param processId id of the process
     */
    public static void assertProcessStarted(IterableProcessEventListener it, String processId) {
        TrackedEvent event = getEvent(it);
        Assertions.assertThat(event.getMethod()).isEqualTo(BEFORE_STARTED);
        CachedProcessStartedEvent orig = event.getEvent();
        Assertions.assertThat(orig.getProcessId()).isEqualTo(processId);
    }

    /**
     * Asserts that the process was completed
     *
     * @param it        listener that listened to the process
     * @param processId id of the process
     */
    public static void assertProcessCompleted(IterableProcessEventListener it, String processId) {
        TrackedEvent event = getEvent(it);
        Assertions.assertThat(event.getMethod()).isEqualTo(BEFORE_COMPLETED);
        CachedProcessCompletedEvent orig = event.getEvent();
        Assertions.assertThat(orig.getProcessId()).isEqualTo(processId);
    }

    /**
     * Asserts that the node with the given name was triggered
     *
     * @param it       listener that listened to the process
     * @param nodeName name of the node that is expected to be triggered
     */
    public static void assertTriggered(IterableProcessEventListener it, String nodeName) {
        TrackedEvent event = getEvent(it);
        Assertions.assertThat(event.getMethod()).isEqualTo(BEFORE_TRIGGERED);
        CachedProcessNodeTriggeredEvent orig = event.getEvent();
        Assertions.assertThat(orig.getNodeName()).isEqualTo(nodeName);
    }

    /**
     * Asserts that the node with the given name was left
     *
     * @param it       listener that listened to the process
     * @param nodeName name of the node that is expected to be left
     */
    public static void assertLeft(IterableProcessEventListener it, String nodeName) {
        TrackedEvent event = getEvent(it);
        Assertions.assertThat(event.getMethod()).isEqualTo(BEFORE_LEFT);
        CachedProcessNodeLeftEvent orig = event.getEvent();
        Assertions.assertThat(orig.getNodeName()).isEqualTo(nodeName);
    }

    /**
     * Asserts that the variable was changed
     *
     * @param it       listener that listened to the process
     * @param name     name of the variable
     * @param oldValue expected old value or null
     * @param newValue expected new value or null
     */
    public static void assertChangedVariable(IterableProcessEventListener it, String name, Object oldValue,
                                             Object newValue) {
        TrackedEvent event = getEvent(it);
        Assertions.assertThat(event.getMethod()).isEqualTo(BEFORE_VARIABLE);
        ProcessVariableChangedEvent orig = event.getEvent();
        Assertions.assertThat(orig.getVariableId()).isEqualTo(name);
        assertChangedVariableValues(orig, oldValue, newValue);

        event = it.next();
        Assertions.assertThat(event.getMethod()).isEqualTo(AFTER_VARIABLE);
        orig = event.getEvent();
        Assertions.assertThat(orig.getVariableId()).isEqualTo(name);
        assertChangedVariableValues(orig, oldValue, newValue);
    }

    private static void assertChangedVariableValues(ProcessVariableChangedEvent event, Object oldValue,
                                                    Object newValue) {
        if (oldValue == null) {
            Assertions.assertThat(event.getOldValue()).isNull();
        } else {
            Assertions.assertThat(event.getOldValue()).isEqualTo(oldValue);
        }
        if (newValue == null) {
            Assertions.assertThat(event.getNewValue()).isNull();
        } else {
            Assertions.assertThat(event.getNewValue()).isEqualTo(newValue);
        }
    }

    /**
     * Asserts that the variable was changed within multipleInstances
     * subprocess. Variable has prefix identifying this MI subprocess.
     *
     * @param it       listener that listened to the process
     * @param name     name of the variable
     * @param oldValue expected old value or null
     * @param newValue expected new value or null
     */
    public static void assertChangedMultipleInstancesVariable(IterableProcessEventListener it, String name,
                                                              Object oldValue, Object newValue) {
        TrackedEvent event = getEvent(it);
        Assertions.assertThat(event.getMethod()).isEqualTo(BEFORE_VARIABLE);
        ProcessVariableChangedEvent orig = event.getEvent();
        Assertions.assertThat(orig.getVariableId().endsWith(":" + name)).isTrue();
        assertChangedVariableValues(orig, oldValue, newValue);

        event = it.next();
        Assertions.assertThat(event.getMethod()).isEqualTo(AFTER_VARIABLE);
        orig = event.getEvent();
        Assertions.assertThat(orig.getVariableId().endsWith(":" + name)).isTrue();
        assertChangedVariableValues(orig, oldValue, newValue);
    }

    /**
     * When multiple variables are initialized order might be different on
     * different JVMs.
     *
     * @param it        listener that listened to the process
     * @param variables names of changed variables
     */
    public static void assertMultipleVariablesChanged(IterableProcessEventListener it, String... variables) {
        for (int i = 0; i < variables.length; i++) {
            TrackedEvent event = getEvent(it);
            Assertions.assertThat(event.getMethod()).isEqualTo(BEFORE_VARIABLE);
            ProcessVariableChangedEvent orig = event.getEvent();
            boolean found = false;
            String name = null;

            for (String str : variables) {
                found = orig.getVariableId().equals(str);
                name = str;
                if (found) {
                    break;
                }
            }
            Assertions.assertThat(found).isTrue();

            event = it.next();
            Assertions.assertThat(event.getMethod()).isEqualTo(AFTER_VARIABLE);
            orig = event.getEvent();
            Assertions.assertThat(orig.getVariableId().equals(name)).isTrue();
        }

    }

    private static TrackedEvent getEvent(IterableProcessEventListener it) {
        Assertions.assertThat(it.hasNext()).isTrue();
        return it.next();
    }
}
