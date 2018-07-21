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

import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;

import static org.drools.testcoverage.common.util.KieBaseModelProvider.RunType.FLOW_DSL;
import static org.drools.testcoverage.common.util.KieBaseModelProvider.RunType.FLOW_WITH_ALPHA_NETWORK;
import static org.drools.testcoverage.common.util.KieBaseModelProvider.RunType.PATTERN_DSL;
import static org.drools.testcoverage.common.util.KieBaseModelProvider.RunType.PATTERN_WITH_ALPHA_NETWORK;
import static org.drools.testcoverage.common.util.KieBaseModelProvider.RunType.STANDARD_FROM_DRL;
import static org.drools.testcoverage.common.util.KieBaseModelProvider.RunType.STANDARD_WITH_ALPHA_NETWORK;

/**
 * Represents various tested KieBase configurations.
 */
public enum KieBaseTestConfiguration implements KieBaseModelProvider {

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_IDENTITY {
        @Override
        public RunType runType() {
            return STANDARD_FROM_DRL;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_IDENTITY_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return STANDARD_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is used.
     */
    CLOUD_IDENTITY_MODEL_FLOW {
        @Override
        public RunType runType() {
            return FLOW_DSL;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },


    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is used.
     */
    CLOUD_IDENTITY_MODEL_FLOW_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return FLOW_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_IDENTITY_MODEL_PATTERN {
        @Override
        public RunType runType() {
            return PATTERN_DSL;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return PATTERN_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_EQUALITY {
        @Override
        public RunType runType() {
            return STANDARD_FROM_DRL;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    CLOUD_EQUALITY_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return STANDARD_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is used.
     */
    CLOUD_EQUALITY_MODEL_FLOW {
        @Override
        public RunType runType() {
            return FLOW_DSL;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is used.
     */
    CLOUD_EQUALITY_MODEL_FLOW_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return FLOW_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_EQUALITY_MODEL_PATTERN {
        @Override
        public RunType runType() {
            return PATTERN_DSL;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return PATTERN_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return false;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_IDENTITY {
        @Override
        public RunType runType() {
            return STANDARD_FROM_DRL;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_IDENTITY_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return STANDARD_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is used.
     */
    STREAM_IDENTITY_MODEL_FLOW {
        @Override
        public RunType runType() {
            return FLOW_DSL;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model is used.
     */
    STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return FLOW_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_IDENTITY_MODEL_PATTERN {
        @Override
        public RunType runType() {
            return PATTERN_DSL;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return PATTERN_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_EQUALITY {
        @Override
        public RunType runType() {
            return STANDARD_FROM_DRL;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },


    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is not used.
     */
    STREAM_EQUALITY_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return STANDARD_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is used.
     */
    STREAM_EQUALITY_MODEL_FLOW {
        @Override
        public RunType runType() {
            return FLOW_DSL;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model is used.
     */
    STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return FLOW_DSL;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_EQUALITY_MODEL_PATTERN {
        @Override
        public RunType runType() {
            return PATTERN_DSL;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }

    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     * Canonical rule model with pattern dialect is used.
     */
    STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK {
        @Override
        public RunType runType() {
            return PATTERN_WITH_ALPHA_NETWORK;
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isStreamMode() {
            return true;
        }

        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(KIE_BASE_MODEL_NAME);
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            additionalKieBaseOptions.forEach(kieBaseConfiguration::setOption);
            return kieBaseConfiguration;
        }

    };

    public static final String KIE_BASE_MODEL_NAME = "KieBaseModelName";

    private static List<KieBaseOption> additionalKieBaseOptions = Collections.emptyList();

    @Override
    public void setAdditionalKieBaseOptions(final KieBaseOption... options) {
        additionalKieBaseOptions = Arrays.asList(options);
    }
}