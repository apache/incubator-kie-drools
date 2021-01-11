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
package org.drools.scenariosimulation.backend.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This DTO holds info related to an expression evaluation.
 * - successful: field represents status of involved evaluation (successful or failed);
 * - pathToWrongValue: a list which contains the steps to describe the wrong value. In case of nested object or collections,
 *                     can require multiple steps (eg. Author.books). In case of a list, conversion is to report the Item
 *                     number (eg. Author.books.Item(2).isAvailable)
 * - wrongValue: The actual wrong value
 * Instantiated objects can be accessed only to retrieve the success status and to generate an error message, if evaluation
 * failed, based on wrongValue and its path.
 */
public class ExpressionEvaluatorResult {

    private boolean successful;
    private List<String> pathToWrongValue;
    private String wrongValue;

    public static ExpressionEvaluatorResult of(boolean successful) {
        return new ExpressionEvaluatorResult(successful);
    }

    public static ExpressionEvaluatorResult ofSuccessful() {
        return new ExpressionEvaluatorResult(true);
    }

    public static ExpressionEvaluatorResult ofFailed() {
        return new ExpressionEvaluatorResult(false);
    }

    public static ExpressionEvaluatorResult ofFailed(String wrongValue, String stepToWrongValue) {
        return new ExpressionEvaluatorResult(false, wrongValue, stepToWrongValue);
    }

    private ExpressionEvaluatorResult(boolean successful, String wrongValue, String stepToWrongValue) {
        this.successful = successful;
        this.pathToWrongValue = new ArrayList<>();
        pathToWrongValue.add(stepToWrongValue);
        this.wrongValue = wrongValue;
    }

    private ExpressionEvaluatorResult(boolean successful) {
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
                return Optional.ofNullable(generateHTMLMessageWithWrongValue());
            } else {
                return Optional.ofNullable(generateHTMLMessageWithoutWrongValue());
            }
        }
        return Optional.empty();
    }

    public void addStepToPath(String step) {
        pathToWrongValue.add(0, step);
    }

    public void addListItemStepToPath(int elementNumber) {
        pathToWrongValue.add(0, "Item " + elementNumber + " of the List");
    }

    public void addMapItemStepToPath(String key) {
        pathToWrongValue.add(0, "Item \"" + key + "\" of the Map");
    }

    public void setWrongValue(String wrongValue) {
        this.wrongValue = wrongValue;
    }

    protected String generateHTMLMessageWithWrongValue() {
        return "Value <span class=error-message-wrong-value>\"" + wrongValue + "\"</span>" +
                " is wrong following path: " +
                "<em>" + String.join("\n", pathToWrongValue) + "</em>";
    }

    protected String generateHTMLMessageWithoutWrongValue() {
        return "Following path is wrong: " +
                "<em>" + String.join("\n", pathToWrongValue) + "</em>";
    }
}
