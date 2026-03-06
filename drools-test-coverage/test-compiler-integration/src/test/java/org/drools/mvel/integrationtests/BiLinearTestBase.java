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
package org.drools.mvel.integrationtests;

import org.drools.base.common.NetworkNode;
import org.drools.core.reteoo.builder.BiLinearDetector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for BiLinear tests providing common setup, teardown, and assertion methods.
 */
public abstract class BiLinearTestBase {

    @BeforeEach
    public void setUp() {
        BiLinearDetector.setBiLinearEnabled(true);
    }

    @AfterEach
    public void cleanup() {
        BiLinearDetector.setBiLinearEnabled(false);
    }

    protected KieBase buildKieBase(String drl) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);
        return kieHelper.build();
    }

    protected void assertBiLinearNodeCount(NetworkVisitor visitor, KieBase kieBase, int bilinearNodeCount) {
        List<NetworkNode> biLinearNodes = visitor.findBiLinearJoinNodes(kieBase);

        assertThat(biLinearNodes)
                .as("BiLinear optimization should create BiLinearJoinNode(s)")
                .isNotEmpty();

        assertThat(biLinearNodes).hasSize(bilinearNodeCount);
    }

    protected void assertNoBiLinearNodes(NetworkVisitor visitor, KieBase kieBase) {
        List<NetworkNode> biLinearNodes = visitor.findBiLinearJoinNodes(kieBase);

        assertThat(biLinearNodes)
                .as("BiLinearJoinNodes should not be created")
                .isEmpty();
    }
}
