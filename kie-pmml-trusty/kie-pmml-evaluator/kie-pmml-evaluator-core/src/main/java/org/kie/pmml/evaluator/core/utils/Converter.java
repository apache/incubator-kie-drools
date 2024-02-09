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
package org.kie.pmml.evaluator.core.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.pmml.ParameterInfo;

/**
 * Class used to convert data to/from different formats
 */
public class Converter {

    private Converter() {
        // Avoid instantiation
    }

    /**
     * <b>Extract</b> the objects from the <code>ParameterInfo</code> values of the given map.
     *
     * @param parameterMap
     * @return
     */
    public static Map<String, Object> getUnwrappedParametersMap(Map<String, ParameterInfo> parameterMap) {
        Map<String, Object> toReturn = new HashMap<>();
        for (Map.Entry<String, ParameterInfo> entry : parameterMap.entrySet()) {
            toReturn.put(entry.getKey(), entry.getValue().getValue());
        }
        return toReturn;
    }

    /**
     * <b>Extract</b> the objects from the <code>ParameterInfo</code> of the given collection.
     *
     * @param parameterInfos
     * @return
     */
    public static Map<String, Object> getUnwrappedParametersMap(Collection<ParameterInfo> parameterInfos) {
        Map<String, Object> toReturn = new HashMap<>();
        for (ParameterInfo parameterInfo : parameterInfos) {
            toReturn.put(parameterInfo.getName(), parameterInfo.getValue());
        }
        return toReturn;
    }
}
