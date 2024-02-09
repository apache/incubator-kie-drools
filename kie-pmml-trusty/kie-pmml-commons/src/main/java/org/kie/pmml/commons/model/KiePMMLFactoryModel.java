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
package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

/**
 * This is the model used to store sources for <code>KiePMMLModelFactory</code> classes;
 * <code>KiePMMLModelFactory</code>, in turns, are used to retrieve <code>List&lt;KiePMMLModel&gt;</code>s
 * from kjar inside <code>PMMLAssemblerService</code>
 */
public class KiePMMLFactoryModel extends KiePMMLModel implements HasSourcesMap {

    private static final long serialVersionUID = 1654176510018808424L;
    private final String kmodulePackageName;
    protected Map<String, String> sourcesMap;

    public KiePMMLFactoryModel(String fileName, String name, String kmodulePackageName, Map<String, String> sourcesMap) {
        super(fileName, name, Collections.emptyList());
        this.sourcesMap = sourcesMap;
        this.kmodulePackageName = kmodulePackageName;
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext pmmlContext) {
        throw new KiePMMLException("KiePMMLFactoryModel is not meant to be used for actual evaluation");
    }

    @Override
    public Map<String, String> getSourcesMap() {
        return Collections.unmodifiableMap(sourcesMap);
    }

    @Override
    public void addSourceMap(String key, String value) {
        sourcesMap.put(key, value);
    }

    @Override
    public String getKModulePackageName() {
        return kmodulePackageName;
    }
}
