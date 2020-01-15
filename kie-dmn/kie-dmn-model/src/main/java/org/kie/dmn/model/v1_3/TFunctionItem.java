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

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.InformationItem;

public class TFunctionItem extends TDMNElement implements FunctionItem {

    protected List<InformationItem> parameters;
    protected QName outputTypeRef;

    @Override
    public List<InformationItem> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<InformationItem>();
        }
        return this.parameters;
    }

    @Override
    public QName getOutputTypeRef() {
        return outputTypeRef;
    }

    @Override
    public void setOutputTypeRef(QName value) {
        this.outputTypeRef = value;
    }

}
