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

package org.drools.compiler.integrationtests;

import java.io.InputStreamReader;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.io.ResourceFactory;

public class ParserTest extends CommonTestMethodBase {

    @Test
    public void testErrorLineNumbers() throws Exception {
        // this test aims to test semantic errors
        // parser errors are another test case
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("errors_in_rule.drl", getClass()), ResourceType.DRL);

        final KnowledgeBuilderError[] errors = kbuilder.getErrors().toArray(new KnowledgeBuilderError[0]);
        assertEquals(3, errors.length);

        final DescrBuildError stiltonError = (DescrBuildError) errors[0];
        assertTrue(stiltonError.getMessage().contains("Stilton"));
        assertNotNull(stiltonError.getDescr());
        assertTrue(stiltonError.getLine() != -1);

        // check that its getting it from the ruleDescr
        assertEquals(stiltonError.getLine(), stiltonError.getDescr().getLine());
        // check the absolute error line number (there are more).
        assertEquals(26, stiltonError.getLine());

        final DescrBuildError poisonError = (DescrBuildError) errors[1];
        assertTrue(poisonError.getMessage().contains("Poison"));
        assertEquals(28, poisonError.getLine());

        final KnowledgeBuilderConfigurationImpl cfg = new KnowledgeBuilderConfigurationImpl();
        final JavaDialectConfiguration javaConf = (JavaDialectConfiguration) cfg.getDialectConfiguration("java");
        switch (javaConf.getCompiler()) {
            case JavaDialectConfiguration.NATIVE:
                assertTrue(errors[2].getMessage().contains("illegal"));
                break;
            case JavaDialectConfiguration.ECLIPSE:
                assertTrue(errors[2].getMessage().contains("add"));
                break;
            case JavaDialectConfiguration.JANINO:
                assertTrue(errors[2].getMessage().contains("Unexpected"));
                break;
            default:
                fail("Unknown compiler used");
        }

        // now check the RHS, not being too specific yet, as long as it has the
        // rules line number, not zero
        final DescrBuildError rhsError = (DescrBuildError) errors[2];
        assertTrue(rhsError.getLine() >= 23 && rhsError.getLine() <= 32);
    }

    @Test
    public void testErrorsParser() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        assertEquals(0, parser.getErrors().size());
        parser.parse(new InputStreamReader(getClass().getResourceAsStream("errors_parser_multiple.drl")));
        assertTrue(parser.hasErrors());
        assertTrue(parser.getErrors().size() > 0);
        assertTrue(parser.getErrors().get(0) instanceof ParserError);
        final ParserError first = ((ParserError) parser.getErrors().get(0));
        assertTrue(first.getMessage() != null);
        assertFalse(first.getMessage().equals(""));
    }

}
