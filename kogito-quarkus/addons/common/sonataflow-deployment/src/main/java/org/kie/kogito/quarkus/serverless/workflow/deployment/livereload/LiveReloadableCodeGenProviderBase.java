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
package org.kie.kogito.quarkus.serverless.workflow.deployment.livereload;

import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public abstract class LiveReloadableCodeGenProviderBase<T extends CodeGenProvider> implements LiveReloadableCodeGenProvider {

    private final T delegate;

    public LiveReloadableCodeGenProviderBase(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public final boolean trigger(CodeGenContext context) throws CodeGenException {
        return delegate.trigger(context);
    }

    @Override
    public String inputDirectory() {
        return delegate.inputDirectory();
    }

    @Override
    public String providerId() {
        return delegate.providerId();
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {
        return delegate.shouldRun(sourceDir, config);
    }
}
