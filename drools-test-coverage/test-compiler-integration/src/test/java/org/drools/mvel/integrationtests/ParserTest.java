/**
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
package org.drools.mvel.integrationtests;

import java.io.InputStreamReader;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.ParserError;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.memorycompiler.JavaConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ParserTest {

    // not for exec-model
    
    @Test
    public void testErrorLineNumbers() throws Exception {
        // this test aims to test semantic errors
        // parser errors are another test case
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("errors_in_rule.drl", getClass()), ResourceType.DRL);

        final KnowledgeBuilderError[] errors = kbuilder.getErrors().toArray(new KnowledgeBuilderError[0]);
        assertThat(errors.length).isEqualTo(3);

        final DescrBuildError stiltonError = (DescrBuildError) errors[0];
        assertThat(stiltonError.getMessage().contains("Stilton")).isTrue();
        assertThat(stiltonError.getDescr()).isNotNull();
        assertThat(stiltonError.getLine() != -1).isTrue();

        // check that its getting it from the ruleDescr
        assertThat(stiltonError.getDescr().getLine()).isEqualTo(stiltonError.getLine());
        // check the absolute error line number (there are more).
        assertThat(stiltonError.getLine()).isEqualTo(30);

        final DescrBuildError poisonError = (DescrBuildError) errors[1];
        assertThat(poisonError.getMessage().contains("Poison")).isTrue();
        assertThat(poisonError.getLine()).isEqualTo(32);

        final KnowledgeBuilderConfigurationImpl cfg = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        final JavaConfiguration javaConf = (JavaConfiguration) cfg.getDialectConfiguration("java");
        switch (javaConf.getCompiler()) {
            case NATIVE:
                assertThat(errors[2].getMessage().contains("illegal")).isTrue();
                break;
            case ECLIPSE:
                assertThat(errors[2].getMessage().contains("add")).isTrue();
                break;
            default:
                fail("Unknown compiler used");
        }

        // now check the RHS, not being too specific yet, as long as it has the
        // rules line number, not zero
        final DescrBuildError rhsError = (DescrBuildError) errors[2];
        assertThat(rhsError.getLine() >= 23 && rhsError.getLine() <= 32).isTrue();
    }

    @Test
    public void testErrorsParser() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        assertThat(parser.getErrors().size()).isEqualTo(0);
        parser.parse(new InputStreamReader(getClass().getResourceAsStream("errors_parser_multiple.drl")));
        assertThat(parser.hasErrors()).isTrue();
        assertThat(parser.getErrors().size() > 0).isTrue();
        assertThat(parser.getErrors().get(0) instanceof ParserError).isTrue();
        final ParserError first = ((ParserError) parser.getErrors().get(0));
        assertThat(first.getMessage() != null).isTrue();
        assertThat(first.getMessage().equals("")).isFalse();
    }

}
