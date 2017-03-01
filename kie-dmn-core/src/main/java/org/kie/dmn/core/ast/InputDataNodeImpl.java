/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.ast;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.model.v1_1.InputData;

public class InputDataNodeImpl extends DMNBaseNode
        implements InputDataNode {

    private DMNType dmnType;
    private InputData inputData;

    public InputDataNodeImpl() {
    }

    public InputDataNodeImpl(InputData inputData ) {
        this( inputData, null );
    }

    public InputDataNodeImpl(InputData inputData, DMNType dmnType ) {
        super( inputData );
        this.inputData = inputData;
        this.dmnType = dmnType;
    }

    public String getName() {
        return this.inputData.getName();
    }

    public DMNType getType() {
        return dmnType;
    }

    public void setType(DMNType dmnType) {
        this.dmnType = dmnType;
    }

    public InputData getInputData() {
        return inputData;
    }

    public void setInputData(InputData inputData) {
        this.inputData = inputData;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof InputDataNodeImpl) ) return false;

        InputDataNodeImpl that = (InputDataNodeImpl) o;

        return inputData != null ? inputData.equals( that.inputData ) : that.inputData == null;
    }

    @Override
    public int hashCode() {
        return inputData != null ? inputData.hashCode() : 0;
    }
}
