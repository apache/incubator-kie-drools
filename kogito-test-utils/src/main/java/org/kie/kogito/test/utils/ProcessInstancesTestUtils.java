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
package org.kie.kogito.test.utils;

import java.util.stream.Stream;

import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessInstancesTestUtils {

    public static <T> ProcessInstance<T> getFirstReadOnly(ProcessInstances<T> processInstances) {
        try (Stream<ProcessInstance<T>> stream = processInstances.stream(ProcessInstanceReadMode.READ_ONLY)) {
            return stream.findFirst().orElseThrow();
        }
    }

    public static <T> ProcessInstance<T> getFirst(ProcessInstances<T> processInstances) {
        try (Stream<ProcessInstance<T>> stream = processInstances.stream(ProcessInstanceReadMode.MUTABLE)) {
            return stream.findFirst().orElseThrow();
        }
    }

    public static <T> void abortFirst(ProcessInstances<T> processInstances) {
        try (Stream<ProcessInstance<T>> stream = processInstances.stream(ProcessInstanceReadMode.MUTABLE)) {
            stream.findFirst().get().abort();
        }
    }

    public static <T> void abort(ProcessInstances<T> processInstances) {
        try (Stream<ProcessInstance<T>> stream = processInstances.stream(ProcessInstanceReadMode.MUTABLE)) {
            stream.forEach(ProcessInstance::abort);
        }
    }

    public static <T> void assertEmpty(ProcessInstances<T> processInstances) {
        try (Stream<ProcessInstance<T>> stream = processInstances.stream()) {
            assertThat(stream).isEmpty();
        }
    }

    public static <T> void assertOne(ProcessInstances<T> processInstances) {
        try (Stream<ProcessInstance<T>> stream = processInstances.stream()) {
            assertThat(stream).hasSize(1);
        }
    }

    public static <T> void assertOne(ProcessInstances<T> processInstances, ProcessInstanceReadMode mode) {
        assertSize(processInstances, mode, 1);
    }

    public static <T> void assertSize(ProcessInstances<T> processInstances, ProcessInstanceReadMode mode, int size) {
        try (Stream<ProcessInstance<T>> stream = processInstances.stream(mode)) {
            assertThat(stream).hasSize(size);
        }
    }
}
