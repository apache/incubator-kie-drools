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
package org.kie.pmml.commons.utils;

public class KiePMMLModelUtils {

    private KiePMMLModelUtils() {
    }

    /**
     * Method to be used by <b>every</b> KiePMML implementation to retrieve the <b>package</b> name
     * out of the model name
     * @param modelName
     * @return
     */
    public static String getSanitizedPackageName(String modelName) {
        return modelName.replace(" ", "").replace("_", "").toLowerCase();
    }

    /**
     * Convert the given <code>String</code> in a valid class name (i.e. no dots, no spaces, first letter upper case)
     * @param input
     * @return
     */
    public static String getSanitizedClassName(String input) {
        String upperCasedInput = input.substring(0, 1).toUpperCase() + input.substring(1);
        return upperCasedInput.replace(".", "").replace("-", "").replace("_", "").replace(" ", "");
    }
}
