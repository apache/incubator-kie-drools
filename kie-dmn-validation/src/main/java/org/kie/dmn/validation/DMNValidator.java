/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation;

import org.kie.dmn.feel.model.v1_1.Definitions;

import java.io.File;
import java.util.List;

public interface DMNValidator {

    /**
     * Performs validation of the DMN model against DMN specifications.
     */
    List<ValidationMsg> validateModel(Definitions dmnModel);

    /**
     * Performs validation of the xml file against the DMN's XSD Schema.
     */
    List<ValidationMsg> validateSchema(File xmlFile);

    /**
     * Release all resources associated with this DMNValidator.
     */
    void dispose();
}
