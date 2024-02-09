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
package org.drools.mvel.integrationtests;

import java.io.StringReader;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public class FakeDRFAssemblerService implements KieAssemblerService {

    private static final String GATEWAY_RULE = "package com.example.rules\n" +
                                               "import com.example.*;\n" +
                                               "rule \"RuleFlow-Split-example-xxx-DROOLS_DEFAULT\"  @Propagation(EAGER)\n" +
                                               "      ruleflow-group \"DROOLS_SYSTEM\"\n" +
                                               "    when\n" +
                                               "      exists String(this == \"Left\")\n" +
                                               "    then\n" +
                                               "end";

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DRF;
    }

    @Override
    public void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        // Just add one fake gateway drl rule. Not for process capability testing
        ((KnowledgeBuilderImpl) kbuilder).addPackageFromDrl(new StringReader(GATEWAY_RULE), resource);
    }
}
