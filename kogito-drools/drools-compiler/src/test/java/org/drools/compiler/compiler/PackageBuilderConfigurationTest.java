/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.DefaultDialectOption;
import org.kie.internal.builder.conf.KBuilderSeverityOption;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackageBuilderConfigurationTest {

    private static String droolsDialectJavaCompilerOrig;
    private static String droolsDialectDefaultOrig;

    @BeforeAll
    public static void backupPropertyValues() {
        droolsDialectJavaCompilerOrig = System.getProperty(JavaDialectConfiguration.JAVA_COMPILER_PROPERTY);
        droolsDialectDefaultOrig = System.getProperty(DefaultDialectOption.PROPERTY_NAME);
    }

    @AfterAll
    public static void restorePropertyValues() {
        if (droolsDialectJavaCompilerOrig != null) {
            System.setProperty(JavaDialectConfiguration.JAVA_COMPILER_PROPERTY, droolsDialectJavaCompilerOrig);
        }
        if (droolsDialectDefaultOrig != null) {
            System.setProperty(DefaultDialectOption.PROPERTY_NAME, droolsDialectDefaultOrig);
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        System.getProperties().remove("drools.dialect.java.compiler");
        System.getProperties().remove("drools.dialect.default");
    }

    @AfterEach
    public void tearDown() throws Exception {
        System.getProperties().remove("drools.dialect.java.compiler");
        System.getProperties().remove("drools.dialect.default");
        System.getProperties().remove("drools.kbuilder.severity." + DuplicateFunction.KEY);
    }

    @Test
    public void testResultSeverity() {
        System.setProperty("drools.kbuilder.severity." + DuplicateFunction.KEY,
                           "ERROR");
        KnowledgeBuilderConfigurationImpl cfg = new KnowledgeBuilderConfigurationImpl();
        assertEquals(cfg.getOptionKeys(KBuilderSeverityOption.class).size(),
                     1);
        assertEquals(cfg.getOption(KBuilderSeverityOption.class,
                                   DuplicateFunction.KEY).getSeverity(),
                     ResultSeverity.ERROR);
    }

    @Test
    public void testResultSeverityNonExistingValueDefaultToInfo() {
        System.setProperty("drools.kbuilder.severity." + DuplicateFunction.KEY,
                           "FOO");
        KnowledgeBuilderConfigurationImpl cfg = new KnowledgeBuilderConfigurationImpl();
        assertEquals(cfg.getOptionKeys(KBuilderSeverityOption.class).size(),
                     1);
        assertEquals(cfg.getOption(KBuilderSeverityOption.class,
                                   DuplicateFunction.KEY).getSeverity(),
                     ResultSeverity.INFO);
    }
}
