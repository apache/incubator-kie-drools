/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.model.project.codegen;

import java.util.NoSuchElementException;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.model.project.codegen.context.DroolsModelBuildContext;

public class KogitoKnowledgeBuilderConfigurationImpl extends KnowledgeBuilderConfigurationImpl {

    public static KogitoKnowledgeBuilderConfigurationImpl fromContext(DroolsModelBuildContext buildContext) {
        KogitoKnowledgeBuilderConfigurationImpl conf = new KogitoKnowledgeBuilderConfigurationImpl(buildContext.getClassLoader());
        for (String prop : buildContext.getApplicationProperties()) {
            if (prop.startsWith("drools")) {
                conf.setProperty(prop, buildContext.getApplicationProperty(prop).orElseThrow(NoSuchElementException::new));
            }
        }
        return conf;
    }

    public KogitoKnowledgeBuilderConfigurationImpl() {
    }

    public KogitoKnowledgeBuilderConfigurationImpl(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected ClassLoader getFunctionFactoryClassLoader() {
        return KogitoKnowledgeBuilderConfigurationImpl.class.getClassLoader();
    }
}
