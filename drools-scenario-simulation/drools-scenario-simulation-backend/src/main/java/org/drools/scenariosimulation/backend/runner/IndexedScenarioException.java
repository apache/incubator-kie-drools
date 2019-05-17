/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.runner;

public class IndexedScenarioException extends ScenarioException {

    private final int index;

    private String fileName;

    public IndexedScenarioException(int index, String message) {
        super(message);
        this.index = index;
    }

    public IndexedScenarioException(int index, String message, Throwable cause) {
        super(message, cause);
        this.index = index;
    }

    public IndexedScenarioException(int index, Throwable cause) {
        super(cause);
        this.index = index;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        String errorMessage = getCause() != null ? getCause().getMessage() : super.getMessage();
        StringBuilder message = new StringBuilder().append("#").append(index).append(": ").append(errorMessage);
        if (getFileName() != null) {
            message.append("(").append(getFileName()).append(")");
        }
        return message.toString();
    }
}
