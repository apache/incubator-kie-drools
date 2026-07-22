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
package org.kie.kogito.codegen.prediction.config;

import org.drools.codegen.common.GeneratedFile;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.SpringBootKogitoBuildContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictionConfigGeneratorTest {

    private final static String PACKAGE_NAME = "PACKAGENAME";

    @Test
    void compilationUnitWithCDI() {
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder().withPackageName(PACKAGE_NAME).build();
        PredictionConfigGenerator predictionConfigGenerator = new PredictionConfigGenerator(context);
        GeneratedFile retrieved = predictionConfigGenerator.generate();
        assertNotNull(retrieved);
        String retrievedContent = new String(retrieved.contents());
        String expected = "@jakarta.inject.Singleton";
        assertTrue(retrievedContent.contains(expected));
        expected = "@jakarta.inject.Inject";
        assertTrue(retrievedContent.contains(expected));
        String unexpected = "@org.springframework.stereotype.Component";
        assertFalse(retrievedContent.contains(unexpected));
        unexpected = "@org.springframework.beans.factory.annotation.Autowired";
        assertFalse(retrievedContent.contains(unexpected));
    }

    @Test
    void compilationUnitWithSpring() {
        KogitoBuildContext context = SpringBootKogitoBuildContext.builder().withPackageName(PACKAGE_NAME).build();
        PredictionConfigGenerator predictionConfigGenerator = new PredictionConfigGenerator(context);
        GeneratedFile retrieved = predictionConfigGenerator.generate();
        assertNotNull(retrieved);
        String retrievedContent = new String(retrieved.contents());
        String expected = "@org.springframework.stereotype.Component";
        assertTrue(retrievedContent.contains(expected));
        expected = "@org.springframework.beans.factory.annotation.Autowired";
        assertTrue(retrievedContent.contains(expected));
        String unexpected = "@jakarta.inject.Singleton";
        assertFalse(retrievedContent.contains(unexpected));
        unexpected = "@jakarta.inject.Inject";
        assertFalse(retrievedContent.contains(unexpected));
    }
}