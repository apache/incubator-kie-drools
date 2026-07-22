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
package org.kie.kogito.codegen.decision;

import org.kie.dmn.api.core.DMNModel;

public class CodegenUtils {

    private CodegenUtils() {
    }

    public static String getDefinitionsFileFromModel(DMNModel dmnModel) {
        String modelName = geNameForDefinitionsFile(dmnModel);
        return modelName.replace(" ", "_").replace(".dmn", ".json");
    }

    static String geNameForDefinitionsFile(DMNModel dmnModel) {
        if (dmnModel.getResource() != null && dmnModel.getResource().getSourcePath() != null) {
            String resourcePath = dmnModel.getResource().getSourcePath().replace('\\', '/');
            return resourcePath.contains("/") ? resourcePath.substring(resourcePath.lastIndexOf('/') + 1) : resourcePath;
        } else {
            return dmnModel.getName() + ".dmn";
        }

    }

}