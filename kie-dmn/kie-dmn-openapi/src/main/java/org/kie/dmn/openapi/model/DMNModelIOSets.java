/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.openapi.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.ast.DecisionServiceNodeImpl;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.InputData;

public class DMNModelIOSets {

    private final DMNModel model;
    private CompositeTypeImpl inputSet;
    private CompositeTypeImpl outputSet;
    private Map<String, String> inputDoc = new HashMap<>();
    private Map<String, String> outputDoc = new HashMap<>();
    private final Map<String, DSIOSets> dsIOSets = new LinkedHashMap<>();
    protected static final String TEMP = "<temp>";

    public DMNModelIOSets(DMNModel model) {
        this.model = model;
        buildInputSet();
        buildOutputSet();
        buildIODoc();
        for (DecisionServiceNode ds : fromThisModel(model.getDecisionServices())) {
            dsIOSets.put(ds.getName(), new DSIOSets(model, ds));
        }
    }

    private void buildOutputSet() {
        CompositeTypeImpl is = new CompositeTypeImpl(model.getNamespace(), TEMP, model.getDefinitions().getId() + "OutputSet");
        for (DecisionNode dn : fromThisModel(model.getDecisions())) {
            DMNType idnType = dn.getResultType();
            is.addField(dn.getName(), idnType);
        }
        for (InputDataNode idn : fromThisModel(model.getInputs())) {
            DMNType idnType = idn.getType();
            is.addField(idn.getName(), idnType);
        }
        this.outputSet = is;
    }

    private void buildInputSet() {
        CompositeTypeImpl is = new CompositeTypeImpl(model.getNamespace(), TEMP, model.getDefinitions().getId() + "InputSet");
        for (InputDataNode idn : fromThisModel(model.getInputs())) {
            DMNType idnType = idn.getType();
            is.addField(idn.getName(), idnType);
        }
        this.inputSet = is;
    }

    private <T extends DMNNode> Collection<T> fromThisModel(Collection<T> ins) {
        return ins.stream().filter(e -> e.getModelNamespace().equals(this.model.getNamespace())).collect(Collectors.toList());
    }

    private void buildIODoc() {
        for (DRGElement drge : model.getDefinitions().getDrgElement()) {
            if (drge instanceof InputData) {
                inputDoc.put(drge.getName(), drge.getDescription());
                outputDoc.put(drge.getName(), drge.getDescription());
            } else if (drge instanceof Decision) {
                outputDoc.put(drge.getName(), drge.getDescription());
            }
        }
    }

    public Collection<DSIOSets> getDSIOSets() {
        return this.dsIOSets.values();
    }

    public DSIOSets lookupDSIOSetsByName(String name) {
        return this.dsIOSets.get(name);
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

    public Map<String, String> getInputDoc() {
        return inputDoc;
    }

    public Map<String, String> getOutputDoc() {
        return outputDoc;
    }

    public static class DSIOSets {

        private final DMNModel model;
        private final DecisionServiceNode ds;
        private CompositeTypeImpl inputSet;
        private BaseDMNTypeImpl outputSet;
        private Map<String, String> inputDoc = new HashMap<>();
        private Map<String, String> outputDoc = new HashMap<>();

        public DSIOSets(DMNModel model, DecisionServiceNode ds) {
            this.model = model;
            this.ds = ds;
            buildInputSet();
            buildOutputSet();
        }

        private void buildOutputSet() {
            if (ds.getDecisionService().getOutputDecision().size() == 1) {
                String id = DMNCompilerImpl.getId(ds.getDecisionService().getOutputDecision().get(0));
                DecisionNode outputDecision = model.getDecisionById(id);
                this.outputSet = new SimpleTypeImpl(ds.getModelNamespace(), TEMP, ds.getId() + "DSOutputSet", false, null,  null, outputDecision != null ? outputDecision.getResultType() : ds.getResultType(), null);
                if (outputDecision != null) {
                    outputDoc.put(outputDecision.getName(), outputDecision.getDecision().getDescription());
                }
            } else {
                CompositeTypeImpl os = new CompositeTypeImpl(ds.getModelNamespace(), TEMP, ds.getId() + "DSOutputSet");
                for (DMNElementReference er : ds.getDecisionService().getOutputDecision()) {
                    String id = DMNCompilerImpl.getId(er);
                    DecisionNode outputDecision = model.getDecisionById(id);
                    if (outputDecision != null) {
                        os.addField(outputDecision.getName(), outputDecision.getResultType());
                        outputDoc.put(outputDecision.getName(), outputDecision.getDecision().getDescription());
                    } else {
                        this.outputSet = new SimpleTypeImpl(ds.getModelNamespace(), TEMP, ds.getId() + "DSOutputSet", false, null, null, ds.getResultType(), null);
                        return; // since cannot lookup correctly, just assign the model-defined DS variable type.
                    }
                }
                this.outputSet = os;
            }
        }

        private void buildInputSet() {
            CompositeTypeImpl is = new CompositeTypeImpl(ds.getModelNamespace(), TEMP, ds.getId() + "DSInputSet");
            DecisionServiceNodeImpl dsNodeImpl = (DecisionServiceNodeImpl) ds;
            for (DMNNode node : dsNodeImpl.getInputParameters().values()) {
                if (node instanceof InputDataNode) {
                    InputDataNode idn = (InputDataNode) node;
                    DMNType idnType = idn.getType();
                    is.addField(idn.getName(), idnType);
                    inputDoc.put(idn.getName(), ((InputDataNodeImpl) idn).getInputData().getDescription());
                } else if (node instanceof DecisionNode) {
                    DecisionNode dn = (DecisionNode) node;
                    DMNType idnType = dn.getResultType();
                    is.addField(dn.getName(), idnType);
                    inputDoc.put(dn.getName(), dn.getDecision().getDescription());
                }
            }
            this.inputSet = is;
        }

        public DecisionServiceNode getDS() {
            return ds;
        }

        public DMNType getDSInputSet() {
            return inputSet;
        }

        public DMNType getDSOutputSet() {
            return outputSet;
        }

        public void setDSInputSetName(String name) {
            this.inputSet.setName(name);
        }

        public void setDSOutputSetName(String name) {
            if (outputSet.getName().equals(TEMP)) {
                outputSet.setName(name);
            }
        }

        public Map<String, String> getInputDoc() {
            return inputDoc;
        }

        public Map<String, String> getOutputDoc() {
            return outputDoc;
        }
    }

}
