/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_3;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DecisionService;

public class TDecisionService extends TInvocable implements DecisionService {

    protected List<DMNElementReference> outputDecision;
    protected List<DMNElementReference> encapsulatedDecision;
    protected List<DMNElementReference> inputDecision;
    protected List<DMNElementReference> inputData;

    @Override
    public List<DMNElementReference> getOutputDecision() {
        if (outputDecision == null) {
            outputDecision = new ArrayList<DMNElementReference>();
        }
        return this.outputDecision;
    }

    @Override
    public List<DMNElementReference> getEncapsulatedDecision() {
        if (encapsulatedDecision == null) {
            encapsulatedDecision = new ArrayList<DMNElementReference>();
        }
        return this.encapsulatedDecision;
    }

    @Override
    public List<DMNElementReference> getInputDecision() {
        if (inputDecision == null) {
            inputDecision = new ArrayList<DMNElementReference>();
        }
        return this.inputDecision;
    }

    @Override
    public List<DMNElementReference> getInputData() {
        if (inputData == null) {
            inputData = new ArrayList<DMNElementReference>();
        }
        return this.inputData;
    }

}
