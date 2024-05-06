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
package org.kie.dmn.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.modelcompiler.CanonicalKieModule;
import org.junit.jupiter.api.AfterEach;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.dmn.core.compiler.ExecModelCompilerOption;

/**
 * DROOLS-3238
 * This base test class purpose is to test DMN model in KJAR containing Drools Model file.<br/>
 * Specifically, as long as the base method {@link #wrapWithDroolsModelResource(KieServices, ReleaseId, Resource...)} is used, this will enable testing a DMN resource
 * running alongside the CanonicalKieModule, which is needed while using the executable model for DRL file.<br/><br/>
 * <i>Please note that these tests don't actually verify the correct behaviour of the compiled DRL files, but they only verify the DMN model.</i> Ref: https://github.com/kiegroup/drools/pull/2460#issue-298982811
 */
public abstract class BaseInterpretedVsCompiledTestCanonicalKieModule {

    protected static Object[] params() {
        return new Object[][]{ { false, true}, {false, false} };
    }

    private boolean useExecModelCompiler;
    protected boolean canonicalKieModule;

    protected void init(boolean useExecModelCompiler, boolean canonicalKieModule) {
        this.useExecModelCompiler = useExecModelCompiler;
        this.canonicalKieModule = canonicalKieModule;
        System.setProperty(ExecModelCompilerOption.PROPERTY_NAME, Boolean.toString(useExecModelCompiler));
    }

    @AfterEach
    public void after() {
        System.clearProperty(ExecModelCompilerOption.PROPERTY_NAME);
    }

    public Resource[] wrapWithDroolsModelResource(KieServices ks, ReleaseId releaseId, Resource... original) {
        List<Resource> resources = new ArrayList<>(Arrays.asList(original));
        if(canonicalKieModule) {
            resources.add(getDroolsModelResource(ks, releaseId));
        }
        return resources.toArray(new Resource[0]);
    }

    private Resource getDroolsModelResource(KieServices ks, ReleaseId releaseId) {
        return ks.getResources()
                .newClassPathResource("/org/kie/dmn/core/drools-model", this.getClass())
                .setTargetPath(CanonicalKieModule.getModelFileWithGAV(releaseId));
    }
}
