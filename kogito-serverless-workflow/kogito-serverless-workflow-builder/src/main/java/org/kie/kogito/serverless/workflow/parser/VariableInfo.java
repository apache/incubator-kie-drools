/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.parser;

import java.util.Arrays;

public class VariableInfo {
    private final String inputVar;
    private final String outputVar;
    private final String collectVar;
    private final String[] extraVariables;

    public VariableInfo(String inputVar, String outputVar, String collectVar, String[] extraVariables) {
        this.inputVar = inputVar;
        this.outputVar = outputVar;
        this.collectVar = collectVar;
        this.extraVariables = extraVariables;
    }

    @Override
    public String toString() {
        return "VariableInfo [inputVar=" + inputVar + ", outputVar=" + outputVar + ", collectVar=" + collectVar +
                ", extraVariables=" + Arrays.toString(extraVariables) + "]";
    }

    public String getInputVar() {
        return inputVar;
    }

    public String getOutputVar() {
        return outputVar;
    }

    public String getCollectVar() {
        return collectVar;
    }

    public String[] getExtraVariables() {
        return extraVariables;
    }
}
