/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

public class IndexedScenarioAssertionError extends AssertionError {

    private final int index;
    private final String scenarioDescription;
    private final String fileName;

    public IndexedScenarioAssertionError(int index, String scenarioDescription, String fileName, Throwable cause) {
        super(cause);
        this.index = index;
        this.scenarioDescription = scenarioDescription;
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        String errorMessage = getCause() != null ? getCause().getMessage() : super.getMessage();
        StringBuilder message = new StringBuilder().append("#").append(index);
        if (scenarioDescription != null) {
            message.append(" ").append(scenarioDescription);
        }
        message.append(": ").append(errorMessage);
        if (fileName != null) {
            message.append(" (").append(fileName).append(")");
        }
        return message.toString();
    }

}
