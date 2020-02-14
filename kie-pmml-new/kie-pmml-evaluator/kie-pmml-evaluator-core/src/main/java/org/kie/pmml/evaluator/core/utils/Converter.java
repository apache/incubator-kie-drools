/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.evaluator.core.utils;

import java.util.Map;
import java.util.stream.Collectors;

import org.kie.api.pmml.ParameterInfo;

/**
 * Class used to convert data to/from different formats
 */
public class Converter {

    /**
     * <b>Extract</b> the objects from the <code>ParameterInfo</code> values of the given map.
     * @param parameterMap
     * @return
     */
    public static Map<String, Object> getUnwrappedParametersMap(Map<String, ParameterInfo> parameterMap) {
        return parameterMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> e.getValue().getValue()));
    }
}
