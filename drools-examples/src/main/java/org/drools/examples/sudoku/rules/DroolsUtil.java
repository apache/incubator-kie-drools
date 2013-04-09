/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.examples.sudoku.rules;

import org.kie.internal.KnowledgeBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;

public class DroolsUtil {

    public static KnowledgeBase readKnowledgeBase(String... drlFileNames) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        // This parses and compiles a DRL file.
        for (String drlFileName: drlFileNames) {
            kBuilder.add(ResourceFactory.newClassPathResource(drlFileName, org.drools.examples.sudoku.SudokuExample.class ),
                         ResourceType.DRL);
            if (kBuilder.hasErrors()){
                for (KnowledgeBuilderError err: kBuilder.getErrors()) {
                    System.out.println(err.toString());
                }
                throw new IllegalStateException("DRL errors");
            }
        }

        // Add the package to a knowledge base (deploy the rule package).
        KieBaseConfiguration kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
        kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());
        return kBase;
    }
}
