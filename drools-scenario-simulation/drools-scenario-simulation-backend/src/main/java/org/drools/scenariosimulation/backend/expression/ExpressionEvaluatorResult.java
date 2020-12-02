/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.scenariosimulation.backend.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.drools.scenariosimulation.backend.runner.model.ValueWrapper.errorWithMessage;

public class ExpressionEvaluatorResult {

    private boolean successful;
    private List<String> pathToWrongValue;
    private String wrongValue;

    public static ExpressionEvaluatorResult ofSuccessful() {
        return new ExpressionEvaluatorResult(true);
    }

    public static ExpressionEvaluatorResult ofFailed() {
        return new ExpressionEvaluatorResult(false);
    }

    public static ExpressionEvaluatorResult ofFailed(String wrongValue, List<String> pathToWrongValue) {
        return new ExpressionEvaluatorResult(false, wrongValue, pathToWrongValue);
    }

    public ExpressionEvaluatorResult(boolean successful, String wrongValue, List<String> pathToWrongValue) {
        this.successful = successful;
        this.wrongValue = wrongValue;
        this.pathToWrongValue = new ArrayList<>(pathToWrongValue);
    }

    public ExpressionEvaluatorResult(boolean successful) {
        this.successful = successful;
        pathToWrongValue = new ArrayList<>();
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public List<String> getPathToWrongValue() {
        return Collections.unmodifiableList(pathToWrongValue);
    }

    public void setPathToWrongValue(List<String> pathToWrongValue) {
        this.pathToWrongValue = pathToWrongValue;
    }

    public Optional<String> getWrongValue() {
        return Optional.ofNullable(wrongValue);
    }

    public void setWrongValue(String wrongValue) {
        this.wrongValue = wrongValue;
    }

    public Optional<String> getMessage() {
        if (!isSuccessful() && (pathToWrongValue != null || !pathToWrongValue.isEmpty())) {
            if (wrongValue != null) {
                return Optional.ofNullable("Value \"" + wrongValue + "\" of " + String.join(".", pathToWrongValue) + " item is wrong");
            } else {
                return Optional.ofNullable("Item " + String.join(".", pathToWrongValue) + " is undefined or not expected");
            }
        }
        return Optional.empty();
    }

    public void addStepToPath(String path) {
        pathToWrongValue.add(0, path);
    }
}
