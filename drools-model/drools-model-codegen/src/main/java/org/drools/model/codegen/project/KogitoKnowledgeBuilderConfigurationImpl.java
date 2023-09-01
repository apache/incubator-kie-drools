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
package org.drools.model.codegen.project;

import java.util.NoSuchElementException;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.MultiValueKieBuilderOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.utils.ChainedProperties;

public class KogitoKnowledgeBuilderConfigurationImpl extends KnowledgeBuilderConfigurationImpl {

    public static KogitoKnowledgeBuilderConfigurationImpl fromContext(DroolsModelBuildContext buildContext) {
        throw new UnsupportedOperationException();
//        KogitoKnowledgeBuilderConfigurationImpl conf = new KogitoKnowledgeBuilderConfigurationImpl(buildContext.getClassLoader());
//        for (String prop : buildContext.getApplicationProperties()) {
//            if (prop.startsWith("drools")) {
//                conf.setProperty(prop, buildContext.getApplicationProperty(prop).orElseThrow(NoSuchElementException::new));
//            }
//        }
//        return conf;
    }


    public KogitoKnowledgeBuilderConfigurationImpl(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig, ClassLoader classLoader, ChainedProperties chainedProperties) {
        super(compConfig);
    }

    @Override
    protected ClassLoader getFunctionFactoryClassLoader() {
        return KogitoKnowledgeBuilderConfigurationImpl.class.getClassLoader();
    }
}
