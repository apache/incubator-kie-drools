/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.openapi.model;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.impl.CompositeTypeImpl;

public class DMNModelIOSets {

    private final DMNModel model;
    private CompositeTypeImpl inputSet;
    private CompositeTypeImpl outputSet;

    public DMNModelIOSets(DMNModel model) {
        this.model = model;
        buildInputSet();
        buildOutputSet();
    }

    private void buildOutputSet() {
        CompositeTypeImpl is = new CompositeTypeImpl(model.getNamespace(), "<temp>", model.getDefinitions().getId() + "OutputSet");
        for (DecisionNode dn : model.getDecisions()) {
            DMNType idnType = dn.getResultType();
            is.addField(dn.getName(), idnType);
        }
        for (InputDataNode idn : model.getInputs()) {
            DMNType idnType = idn.getType();
            is.addField(idn.getName(), idnType);
        }
        this.outputSet = is;
    }

    private void buildInputSet() {
        CompositeTypeImpl is = new CompositeTypeImpl(model.getNamespace(), "<temp>", model.getDefinitions().getId() + "InputSet");
        for (InputDataNode idn : model.getInputs()) {
            DMNType idnType = idn.getType();
            is.addField(idn.getName(), idnType);
        }
        this.inputSet = is;
    }

    public DMNModel getModel() {
        return model;
    }

    public DMNType getInputSet() {
        return inputSet;
    }

    public DMNType getOutputSet() {
        return outputSet;
    }

    public void setInputSetName(String name) {
        this.inputSet.setName(name);
    }

    public void setOutputSetName(String name) {
        this.outputSet.setName(name);
    }

}
