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
package org.drools.testcoverage.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.core.impl.RuleBaseFactory;
import org.drools.model.codegen.ExecutableModelProject;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.conf.KieBaseOption;

/**
 * Represents all possible KieBase configurations for tests.
 */
public enum KieBaseTestConfiguration implements KieBaseModelProvider {

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_IDENTITY(IDENTITY),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_IDENTITY_ALPHA_NETWORK(IDENTITY + ALPHA_NETWORK_COMPILER),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_IDENTITY_MODEL_PATTERN(IDENTITY, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK(IDENTITY + ALPHA_NETWORK_COMPILER, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_EQUALITY(0),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_EQUALITY_ALPHA_NETWORK(ALPHA_NETWORK_COMPILER),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_EQUALITY_MODEL_PATTERN(0, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK(ALPHA_NETWORK_COMPILER, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_IDENTITY(IDENTITY + STREAM_MODE),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_IDENTITY_ALPHA_NETWORK(IDENTITY + STREAM_MODE + ALPHA_NETWORK_COMPILER),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_IDENTITY_MODEL_PATTERN(IDENTITY + STREAM_MODE, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK(IDENTITY + STREAM_MODE + ALPHA_NETWORK_COMPILER, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_EQUALITY(STREAM_MODE),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_EQUALITY_ALPHA_NETWORK(STREAM_MODE + ALPHA_NETWORK_COMPILER),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_EQUALITY_MODEL_PATTERN(STREAM_MODE, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK(STREAM_MODE + ALPHA_NETWORK_COMPILER, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_IDENTITY_IMMUTABLE(IDENTITY + IMMUTABLE),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_IDENTITY_IMMUTABLE_MODEL_PATTERN(IDENTITY + IMMUTABLE, ExecutableModelProject.class);


    public static final String KIE_BASE_MODEL_NAME = "KieBaseModelName";

    private static List<KieBaseOption> additionalKieBaseOptions = Collections.emptyList();

    private final int options;
    private final Class<? extends KieBuilder.ProjectType> executableModelProjectClass;

    KieBaseTestConfiguration(final int options) {
        this(options, null);
    }

    KieBaseTestConfiguration(final int options,
                             final Class<? extends KieBuilder.ProjectType> executableModelProjectClass) {
        this.options = options;
        this.executableModelProjectClass = executableModelProjectClass;
    }

    // additionalKieBaseOptions is only used by getKieBaseConfiguration(), not by getKieBaseModel(). Little confusing
    @Override
    public void setAdditionalKieBaseOptions(final KieBaseOption... options) {
        additionalKieBaseOptions = Arrays.asList(options);
    }

    @Override
    public boolean isIdentity() {
        return (options & IDENTITY) != 0;
    }

    @Override
    public boolean isStreamMode() {
        return (options & STREAM_MODE) != 0;
    }

    @Override
    public boolean useAlphaNetworkCompiler() {
        return (options & ALPHA_NETWORK_COMPILER) != 0;
    }

    @Override
    public boolean isImmutable() {
        return (options & IMMUTABLE) != 0;
    }

    @Override
    public Optional<Class<? extends KieBuilder.ProjectType>> getExecutableModelProjectClass() {
        return Optional.ofNullable(executableModelProjectClass);
    }

    public boolean isExecutableModel() {
        return executableModelProjectClass != null;
    }

    @Override
    public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
        final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
        if (isStreamMode()) {
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
        } else {
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
        }
        if (isIdentity()) {
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
        } else {
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
        }
        if (isImmutable()) {
            kieBaseModel.setMutability(KieBaseMutabilityOption.DISABLED);
        } else {
            kieBaseModel.setMutability(KieBaseMutabilityOption.ALLOWED);
        }
        kieBaseModel.setDefault(true);
        return kieBaseModel;
    }

    @Override
    public KieBaseConfiguration getKieBaseConfiguration() {
        final KieBaseConfiguration kieBaseConfiguration = RuleBaseFactory.newKnowledgeBaseConfiguration();
        if (isStreamMode()) {
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
        } else {
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
        }
        if (isIdentity()) {
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
        } else {
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
        }
        if (isImmutable()) {
            kieBaseConfiguration.setOption(KieBaseMutabilityOption.DISABLED);
        } else {
            kieBaseConfiguration.setOption(KieBaseMutabilityOption.ALLOWED);
        }
        additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
        return kieBaseConfiguration;
    }
}