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
package org.kie.dmn.core.pmml;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.Import;

public class DMNPMMLModelInfo extends PMMLModelInfo {

    private final Map<String, DMNType> inputFields;
    private final Map<String, DMNType> outputFields;

    public DMNPMMLModelInfo(String name, String className, Map<String, DMNType> inputFields, Collection<String> targetFields, Map<String, DMNType> outputFields) {
        super(name, className, inputFields.keySet(), targetFields, outputFields.keySet());
        this.inputFields = Collections.unmodifiableMap(new HashMap<>(inputFields));
        this.outputFields = Collections.unmodifiableMap(new HashMap<>(outputFields));
    }

    public static DMNPMMLModelInfo from(PMMLModelInfo info, DMNModelImpl model, Import i) {
        Map<String, DMNType> inputFields = new HashMap<>();
        for (String name : info.inputFieldNames) {
            DMNType lookupType = model.getTypeRegistry().resolveType(i.getNamespace(), name);
            if (lookupType == null) {
                lookupType = model.getTypeRegistry().unknown();
            }
            inputFields.put(name, lookupType);
        }
        Map<String, DMNType> outputFields = new HashMap<>();
        if (info.outputFieldNames.size() > 1 && info.name != null && !info.name.isEmpty()) {
            outputFields.put(info.name, model.getTypeRegistry().resolveType(i.getNamespace(), info.name));
        } else {
            for (String name : info.outputFieldNames) {
                DMNType lookupType = model.getTypeRegistry().resolveType(i.getNamespace(), name);
                if (lookupType == null) {
                    lookupType = model.getTypeRegistry().unknown();
                }
                outputFields.put(name, lookupType);
            }
        }
        return new DMNPMMLModelInfo(info.name, info.className, inputFields, info.targetFieldNames, outputFields);
    }

    public Map<String, DMNType> getInputFields() {
        return inputFields;
    }

    public Map<String, DMNType> getOutputFields() {
        return outputFields;
    }
}