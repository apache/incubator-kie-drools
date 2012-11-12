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

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;

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
        KnowledgeBaseConfiguration kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
        kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());
        return kBase;
    }
}
