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
import java.util.List;
import java.util.Optional;

public class ExpressionEvaluatorResult {

    private final boolean successful;
    private final List<String> pathToWrongValue;
    private final String wrongValue;

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
        this.wrongValue = null;
        this.pathToWrongValue = null;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Optional<String> getMessage() {
        if (!successful && (null != pathToWrongValue || !pathToWrongValue.isEmpty())) {
            if (wrongValue != null) {
                return Optional.ofNullable("Value \"" + wrongValue + "\" of " + String.join(".", pathToWrongValue) + " item is wrong.");
            } else {
                return Optional.ofNullable("Item " + String.join(".", pathToWrongValue) + " is undefined or not expected.");
            }
        }
        return Optional.empty();
    }

    public void addStepToPath(String path) {
        if (successful || null == pathToWrongValue) {
            throw new UnsupportedOperationException("This instance doesn't hold additional information.");
        }
        pathToWrongValue.add(0, path);
    }
}
