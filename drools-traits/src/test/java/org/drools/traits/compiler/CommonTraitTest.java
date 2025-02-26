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
package org.drools.traits.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.drools.drl.parser.DrlParser;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.traits.core.base.evaluators.IsAEvaluatorDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.kie.api.KieBase;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.fail;

public class CommonTraitTest {

    @BeforeAll
    public static void beforeClass() {
        System.setProperty(EvaluatorOption.PROPERTY_NAME + "isA", IsAEvaluatorDefinition.class.getName());
    }

    protected KieBase loadKnowledgeBaseFromString(String... drlContentStrings) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String drl : drlContentStrings) {
            drl = replaceCustomOperatorIfRequired(drl);
            kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        }

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    protected KieBase loadKnowledgeBaseFromDrlFile(String drlFilePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(drlFilePath)) {
            String drl = replaceCustomOperatorIfRequired(new String(is.readAllBytes(), StandardCharsets.UTF_8));
            return loadKnowledgeBaseFromString(drl);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected KieBase loadKnowledgeBaseWithKnowledgeBuilderOption(String drl,  KnowledgeBuilderOption... knowledgeBuilderOptions) {
        drl = replaceCustomOperatorIfRequired(drl);
        return new KieHelper(knowledgeBuilderOptions).addContent(drl, ResourceType.DRL ).build();
    }

    protected KieBase loadKnowledgeBaseWithKieBaseOption(String drl,  KieBaseOption... kieBaseOption) {
        drl = replaceCustomOperatorIfRequired(drl);
        return new KieHelper().addContent(drl, ResourceType.DRL ).build(kieBaseOption);
    }

    protected static String replaceCustomOperatorIfRequired(String drl) {
        if (DrlParser.ANTLR4_PARSER_ENABLED) {
            // new parser (DRL10) requires a prefix '##' for a custom operator
            return drl.replaceAll(" isA ", " ##isA ");
        }
        return drl;
    }
}
