/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.prediction.config;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.body.BodyDeclaration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.context.SpringBootKogitoBuildContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictionConfigGeneratorTest {

    private final static String PACKAGE_NAME = "PACKAGENAME";

    @Test
    void compilationUnitWithCDI() {
        PredictionConfigGenerator predictionConfigGenerator = new PredictionConfigGenerator(new QuarkusKogitoBuildContext(s -> false), PACKAGE_NAME);
        Optional<GeneratedFile> retrievedOpt = predictionConfigGenerator.generate();
        assertNotNull(retrievedOpt);
        assertTrue(retrievedOpt.isPresent());
        String retrieved = new String(retrievedOpt.get().contents());
        String expected = "@javax.inject.Singleton";
        assertTrue(retrieved.contains(expected));
        expected = "@javax.inject.Inject";
        assertTrue(retrieved.contains(expected));
        String unexpected = "@org.springframework.stereotype.Component";
        assertFalse(retrieved.contains(unexpected));
        unexpected = "@org.springframework.beans.factory.annotation.Autowired";
        assertFalse(retrieved.contains(unexpected));
    }

    @Test
    void compilationUnitWithSpring() {
        PredictionConfigGenerator predictionConfigGenerator = new PredictionConfigGenerator(new SpringBootKogitoBuildContext(s -> false), PACKAGE_NAME);
        Optional<GeneratedFile> retrievedOpt = predictionConfigGenerator.generate();
        assertNotNull(retrievedOpt);
        assertTrue(retrievedOpt.isPresent());
        String retrieved = new String(retrievedOpt.get().contents());
        String expected = "@org.springframework.stereotype.Component";
        assertTrue(retrieved.contains(expected));
        expected = "@org.springframework.beans.factory.annotation.Autowired";
        assertTrue(retrieved.contains(expected));
        String unexpected = "@javax.inject.Singleton";
        assertFalse(retrieved.contains(unexpected));
        unexpected = "@javax.inject.Inject";
        assertFalse(retrieved.contains(unexpected));
    }

    @Test
    void members() {
        PredictionConfigGenerator predictionConfigGenerator = new PredictionConfigGenerator(new QuarkusKogitoBuildContext(s -> false), PACKAGE_NAME);
        List<BodyDeclaration<?>> retrieved = predictionConfigGenerator.members();
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
    }
}