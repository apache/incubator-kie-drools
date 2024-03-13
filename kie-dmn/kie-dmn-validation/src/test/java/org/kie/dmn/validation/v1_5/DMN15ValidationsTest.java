/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation.v1_5;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.drools.io.ClassPathResource;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DMN15ValidationsTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMN15ValidationsTest.class);

    static final DMNValidator validator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));
    static final DMNValidator.ValidatorBuilder validatorBuilder = validator.validateUsing(DMNValidator.Validation.VALIDATE_SCHEMA, DMNValidator.Validation.VALIDATE_MODEL);

    @Test
    public void unnamedImportValidation() {
        Resource importedModelResource = new ClassPathResource("Imported_Model_Unamed.dmn",
                                                                this.getClass() );
        printString(importedModelResource);
        Resource importingModelResource = new ClassPathResource("Importing_EmptyNamed_Model.dmn",
                                                                this.getClass() );
        printString(importingModelResource);
        List<DMNMessage> dmnMessages = validatorBuilder.theseModels(importedModelResource, importingModelResource);
        assertNotNull(dmnMessages);
        dmnMessages.forEach(dmnMessage -> LOG.error(dmnMessage.toString()));
        assertTrue(dmnMessages.isEmpty());
    }

    private void printString(Resource toPRint) {
        try (InputStream is = toPRint.getInputStream()) {
            String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            LOG.error(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
