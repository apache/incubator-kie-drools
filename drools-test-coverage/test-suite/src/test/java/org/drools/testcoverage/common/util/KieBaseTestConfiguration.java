/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
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
    CLOUD_IDENTITY(true, false, false),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_IDENTITY_ALPHA_NETWORK(true, false, true),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_IDENTITY_MODEL_PATTERN(true, false, false, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK(true, false, true, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_EQUALITY(false, false, false),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_EQUALITY_ALPHA_NETWORK(false, false, true),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_EQUALITY_MODEL_PATTERN(false, false, false, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK(false, false, true, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_IDENTITY(true, true, false),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_IDENTITY_ALPHA_NETWORK(true, true, true),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_IDENTITY_MODEL_PATTERN(true, true, false, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK(true, true, true, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_EQUALITY(false, true, false),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_EQUALITY_ALPHA_NETWORK(false, true, true),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_EQUALITY_MODEL_PATTERN(false, true, false, ExecutableModelProject.class),

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK(false, true, true, ExecutableModelProject.class);

    public static final String KIE_BASE_MODEL_NAME = "KieBaseModelName";

    private static List<KieBaseOption> additionalKieBaseOptions = Collections.emptyList();

    private boolean identity;
    private boolean streamMode;
    private final boolean alphaNetworkCompiler;
    private final Class<? extends KieBuilder.ProjectType> executableModelProjectClass;

    KieBaseTestConfiguration(final boolean identity, final boolean streamMode, final boolean alphaNetworkCompiler) {
        this(identity, streamMode, alphaNetworkCompiler, null);
    }

    KieBaseTestConfiguration(final boolean identity, final boolean streamMode, final boolean alphaNetworkCompiler,
                             final Class<? extends KieBuilder.ProjectType> executableModelProjectClass) {
        this.identity = identity;
        this.streamMode = streamMode;
        this.alphaNetworkCompiler = alphaNetworkCompiler;
        this.executableModelProjectClass = executableModelProjectClass;
    }

    // additionalKieBaseOptions is only used by getKieBaseConfiguration(), not by getKieBaseModel(). Little confusing
    @Override
    public void setAdditionalKieBaseOptions(final KieBaseOption... options) {
        additionalKieBaseOptions = Arrays.asList(options);
    }

    @Override
    public boolean isIdentity() {
        return identity;
    }

    @Override
    public boolean isStreamMode() {
        return streamMode;
    }

    @Override
    public boolean useAlphaNetworkCompiler() {
        return alphaNetworkCompiler;
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
        kieBaseModel.setDefault(true);
        return kieBaseModel;
    }

    @Override
    public KieBaseConfiguration getKieBaseConfiguration() {
        final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
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
        additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
        return kieBaseConfiguration;
    }
}