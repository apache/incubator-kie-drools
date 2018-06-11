/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.CMMNCaseProvider;
import org.jbpm.casemgmt.cmmn.xml.CMMNSemanticModule;
import org.kie.internal.builder.KnowledgeBuilder;

public class CMMNCaseProviderImpl implements CMMNCaseProvider {

    public void configurePackageBuilder(KnowledgeBuilder knowledgeBuilder) {
        KnowledgeBuilderConfigurationImpl conf = ((KnowledgeBuilderImpl) knowledgeBuilder).getBuilderConfiguration();
        if (conf.getSemanticModules().getSemanticModule(CMMNSemanticModule.CMMN_URI) == null) {
            conf.addSemanticModule(new CMMNSemanticModule());
        }
    }

}
