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

/**
 * This DTO holds info related to an expression evaluation.
 * - successful: field represents status of involved evaluation (sucessful or failed);
 * - pathToWrongValue: a list which contains the steps to describe the wrong value. In case of nested object or collections,
 *                     can require multiple steps (eg. Author.books). In case of a list, conversion is to report the Item
 *                     number (eg. Author.books.Item(2).isAvailable)
 * - wrongValue: The actual wrong value
 * Instantiated objects can be accessed only to retrieve the success status and to generate an error message, if evaluation
 * failed, based on wrongVaue and its path.
 */
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
        this.pathToWrongValue = new ArrayList<>(pathToWrongValue);
        this.wrongValue = wrongValue;
    }

    public ExpressionEvaluatorResult(boolean successful) {
        this.successful = successful;
        this.pathToWrongValue = new ArrayList<>();
        this.wrongValue = null;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Optional<String> getErrorMessage() {
        if (!successful && !pathToWrongValue.isEmpty()) {
            if (wrongValue != null) {
                return Optional.ofNullable("Value \"" + wrongValue + "\" of " + String.join(".", pathToWrongValue) + " item is wrong.");
            } else {
                return Optional.ofNullable("Item " + String.join(".", pathToWrongValue) + " is undefined or not expected.");
            }
        }
        return Optional.empty();
    }

    public void addStepToPath(String path) {
        pathToWrongValue.add(0, path);
    }

    public void addListItemStepToPath(int elementNumber) {
        pathToWrongValue.add(0, "Item(" + elementNumber + ")");
    }

    public void setWrongValue(String wrongValue) {
        this.wrongValue = wrongValue;
    }
}
