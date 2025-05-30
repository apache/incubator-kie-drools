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

import java.util.Collections;
import java.util.Set;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.validation.DMNValidator;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.memorycompiler.KieMemoryCompiler;

@SuppressWarnings("rawtypes")
public class DmnCompilationContextImpl extends EfestoCompilationContextImpl implements DmnCompilationContext {

    private Set<DMNProfile> customDMNProfiles;
    private Set<DMNValidator.Validation> validations;
    private RuntimeTypeCheckOption runtimeTypeCheckOption;


    public static Builder builder(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return new Builder(memoryCompilerClassLoader);
    }

    public static Builder builder(EfestoCompilationContextImpl context) {
        return new Builder(context);
    }

    public DmnCompilationContextImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this(memoryCompilerClassLoader, null, null, null);
    }

    private DmnCompilationContextImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader,
                                     Set<DMNProfile> customDMNProfiles,
                                     Set<DMNValidator.Validation> validations,
                                     RuntimeTypeCheckOption runtimeTypeCheckOption) {
        super(memoryCompilerClassLoader, false);
        this.customDMNProfiles = customDMNProfiles;
        this.validations = validations;
        this.runtimeTypeCheckOption = runtimeTypeCheckOption;
    }

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(memoryCompilerClassLoader);
    }

    @Override
    public Set<DMNProfile> getCustomDMNProfiles() {
        return customDMNProfiles;
    }

    @Override
    public Set<DMNValidator.Validation> getValidations() {
        return validations != null ? Collections.unmodifiableSet(validations) : Collections.emptySet();
    }

    @Override
    public RuntimeTypeCheckOption getRuntimeTypeCheckOption() {
        return runtimeTypeCheckOption;
    }

    @Override
    public ClassLoader getContextClassloader() {
        return memoryCompilerClassLoader;
    }

    public static class Builder {

        private final DmnCompilationContextImpl compilationContext;

        private Builder(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
            this.compilationContext = new DmnCompilationContextImpl(memoryCompilerClassLoader);
        }

        private Builder(EfestoCompilationContextImpl context) {
            this.compilationContext = (DmnCompilationContextImpl) EfestoCompilationContextUtils.buildFromContext(context, DmnCompilationContextImpl.class);
        }

        public Builder withCustomDMNProfiles(Set<DMNProfile> customDMNProfiles) {
            compilationContext.customDMNProfiles = customDMNProfiles;
            return this;
        }

        public Builder withValidations(Set<DMNValidator.Validation> validations) {
            compilationContext.validations = validations;
            return this;
        }

        public Builder withRuntimeTypeCheckOption(RuntimeTypeCheckOption runtimeTypeCheckOption) {
            compilationContext.runtimeTypeCheckOption = runtimeTypeCheckOption;
            return this;
        }

        public DmnCompilationContextImpl build() {
            return compilationContext;
        }

    }
}
