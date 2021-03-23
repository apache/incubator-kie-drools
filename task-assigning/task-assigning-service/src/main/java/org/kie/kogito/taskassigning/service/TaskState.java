/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.service;

public enum TaskState {
    READY("Ready"),
    RESERVED("Reserved"),
    ABORTED("Aborted"),
    SKIPPED("Skipped"),
    COMPLETED("Completed");

    private String value;

    TaskState(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value();
    }

    public static boolean isTerminal(String status) {
        return ABORTED.value().equals(status) || SKIPPED.value().equals(status) || COMPLETED.value().equals(status);
    }

}
