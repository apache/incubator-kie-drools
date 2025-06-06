/*
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
package org.kie.dmn.efesto.compiler.model;

import java.util.Set;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.validation.DMNValidator;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;

@SuppressWarnings("rawtypes")
public interface DmnCompilationContext extends EfestoCompilationContext {

    static DmnCompilationContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return DmnCompilationContextImpl.builder(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader)).build();
    }

    static DmnCompilationContext buildWithMemoryCompilerClassLoader(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return DmnCompilationContextImpl.builder(memoryCompilerClassLoader).build();
    }

    static DmnCompilationContext buildWithEfestoCompilationContext(EfestoCompilationContextImpl context) {
        return DmnCompilationContextImpl.builder(context)
                .build();
    }

    static DmnCompilationContext buildWithEfestoCompilationContext(EfestoCompilationContextImpl context,
                                                                   Set<DMNProfile> customDMNProfiles,
                                                                   Set<DMNValidator.Validation> validations,
                                                                   RuntimeTypeCheckOption runtimeTypeCheckOption) {
        return DmnCompilationContextImpl.builder(context)
                .withCustomDMNProfiles(customDMNProfiles)
                .withValidations(validations)
                .withRuntimeTypeCheckOption(runtimeTypeCheckOption)
                .build();
    }


    static DmnCompilationContext buildWithParentClassLoader(ClassLoader parentClassLoader,
                                                            Set<DMNProfile> customDMNProfiles,
                                                            Set<DMNValidator.Validation> validations,
                                                            RuntimeTypeCheckOption runtimeTypeCheckOption) {
        return DmnCompilationContextImpl.builder(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader))
                .withCustomDMNProfiles(customDMNProfiles)
                .withValidations(validations)
                .withRuntimeTypeCheckOption(runtimeTypeCheckOption)
                .build();
    }

    KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration();

    Set<DMNProfile> getCustomDMNProfiles();

    Set<DMNValidator.Validation> getValidations();

    RuntimeTypeCheckOption getRuntimeTypeCheckOption();

    ClassLoader getContextClassloader();
}
