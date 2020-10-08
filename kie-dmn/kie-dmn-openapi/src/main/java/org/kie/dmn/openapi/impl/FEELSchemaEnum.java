/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.openapi.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.runtime.UnaryTestImpl;

public class FEELSchemaEnum {

    public static void parseAllowedValuesIntoSchema(Schema schema, List<DMNUnaryTest> list) {
        try {
            FEEL SimpleFEEL = FEEL.newInstance();
            List<Object> expectLiterals = list.stream().map(UnaryTestImpl.class::cast)
                                                       .map(UnaryTestImpl::toString)
                                                       .map(SimpleFEEL::evaluate)
                                                       .collect(Collectors.toList());
            expectLiterals.forEach(o -> {
                if (!(o instanceof String || o instanceof Number || o instanceof Boolean)) {
                    throw new UnsupportedOperationException("OAS enumeration only checked for specifc types.");
                }
            });
            schema.enumeration(expectLiterals);
        } catch (Exception e) {
            schema.description(schema.getDescription() + "\n" + list);
        }
    }

    private FEELSchemaEnum() {
        // deliberate intention not to allow instantiation of this class.
    }
}
