/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.operator.impl.solver.model;

public final class OptaPlannerSolverStatus {
    private String errorMessage;
    private String inputMessageAddress;
    private String outputMessageAddress;

    public OptaPlannerSolverStatus() {
        // required by Jackson
    }

    private OptaPlannerSolverStatus(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static OptaPlannerSolverStatus success() {
        return new OptaPlannerSolverStatus(null);
    }

    public static OptaPlannerSolverStatus error(Exception exception) {
        return new OptaPlannerSolverStatus(exception.getMessage());
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getInputMessageAddress() {
        return inputMessageAddress;
    }

    public void setInputMessageAddress(String inputMessageAddress) {
        this.inputMessageAddress = inputMessageAddress;
    }

    public String getOutputMessageAddress() {
        return outputMessageAddress;
    }

    public void setOutputMessageAddress(String outputMessageAddress) {
        this.outputMessageAddress = outputMessageAddress;
    }
}
