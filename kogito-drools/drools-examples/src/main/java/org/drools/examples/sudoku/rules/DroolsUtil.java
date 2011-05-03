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

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

public class DroolsUtil {

    public static KnowledgeBase readKnowledgeBase(String drlFileName) throws Exception {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        // This parses and compiles a DRL file.
        kBuilder.add(ResourceFactory.newClassPathResource(drlFileName, org.drools.examples.sudoku.Main.class ),
                     ResourceType.DRL);
        if (kBuilder.hasErrors()){
            for (KnowledgeBuilderError err: kBuilder.getErrors()) {
                System.out.println(err.toString());
            }
            throw new IllegalStateException("DRL errors");
        }

        // Add the package to a knowledge base (deploy the rule package).
        KnowledgeBaseConfiguration kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
        kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());
        return kBase;
    }
}
