/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.cmmn;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;

public abstract class AbstractCmmnBaseTest {
    
    protected KieBase createKnowledgeBase(String... cases) throws Exception {
        Resource[] resources = new Resource[cases.length];
        for (int i = 0; i < cases.length; ++i) {
            String p = cases[i];
            resources[i] = (ResourceFactory.newClassPathResource(p));
        }
        return createKnowledgeBaseFromResources(resources);
    }

    protected KieBase createKnowledgeBaseFromResources(Resource... cases)
            throws Exception {

        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        if (cases.length > 0) {
            KieFileSystem kfs = ks.newKieFileSystem();

            for (Resource c : cases) {
                kfs.write(c);
            }

            KieBuilder kb = ks.newKieBuilder(kfs);

            kb.buildAll(); // kieModule is automatically deployed to KieRepository
                           // if successfully built.

            if (kb.getResults().hasMessages(Level.ERROR)) {
                throw new RuntimeException("Build Errors:\n"
                        + kb.getResults().toString());
            }
        }

        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        return kContainer.getKieBase();
    }
}
