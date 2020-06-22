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
package org.kie.pmml.models.drools.commons.utils;

import org.kie.pmml.commons.model.enums.DATA_TYPE;

/**
 * Static utility methods for <code>KiePMMLDroolsModel</code>s
 */
public class KiePMMLDroolsModelUtils {

    private KiePMMLDroolsModelUtils() {
        // Avoid instantiation
    }

    /**
     * Return an <code>Object</code> correctly formatted to be put in drl (e.g. if the <b>targetType</b>
     * is <code>DATA_TYPE.STRING</code> returns the <b>quoted</b> rawValue.
     * <p>
     * If <b>rawValue</b> is <code>null</code>, returns <code>null</code>
     * @param rawValue
     * @param targetType
     * @return
     */
    public static Object getCorrectlyFormattedResult(Object rawValue, DATA_TYPE targetType) {
        if (rawValue == null) {
            return null;
        }
        Object toReturn = targetType.getActualValue(rawValue);
        if (DATA_TYPE.STRING.equals(targetType)) {
            toReturn = "\"" + toReturn + "\"";
        }
        return toReturn;
    }
}
