/*
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
package org.kie.dmn.core.pmml;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
import org.kie.dmn.model.api.DMNElement;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPMMLInvocationEvaluator implements DMNExpressionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPMMLInvocationEvaluator.class);

    protected final String dmnNS;
    protected final DMNElement node;
    protected final List<FormalParameter> parameters = new ArrayList<>();
    protected final ModelLocalUriId pmmlModelLocalUriID;
    protected final String model;


    public AbstractPMMLInvocationEvaluator(String dmnNS, DMNElement node, ModelLocalUriId pmmlModelLocalUriID, String model) {
        this.dmnNS = dmnNS;
        this.node = node;
        this.pmmlModelLocalUriID = pmmlModelLocalUriID;
        this.model = model;
    }

    protected static Object getValueForPMMLInput(DMNResult r, String name) {
        Object pValue = r.getContext().get(name);
        if (pValue instanceof BigDecimal) {
            return ((BigDecimal) pValue).doubleValue();
        }
        return pValue;
    }

    public DMNType getParameterType(String name) {
        for (FormalParameter fp : parameters) {
            if (fp.name.equals(name)) {
                return fp.type;
            }
        }
        return null;
    }

    public List<List<String>> getParameterNames() {
        return Collections.singletonList(parameters.stream().map(p -> p.name).collect(Collectors.toList()));
    }

    public List<List<DMNType>> getParameterTypes() {
        return Collections.singletonList(parameters.stream().map(p -> p.type).collect(Collectors.toList()));
    }

    public void addParameter(String name, DMNType dmnType) {
        this.parameters.add(new FormalParameter(name, dmnType));
    }

}
