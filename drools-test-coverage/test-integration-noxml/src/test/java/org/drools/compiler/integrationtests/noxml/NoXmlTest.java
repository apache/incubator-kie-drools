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
package org.drools.compiler.integrationtests.noxml;

import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class NoXmlTest {

    @Test
    public void testKieHelperKieModuleModel() throws Exception {
        final String drl =
                    "rule R when\n" +
                    "    String()\n" +
                    "then\n" +
                    "end\n";

        KieModuleModel kModuleModel = KieServices.get().newKieModuleModel();

        KieHelper kHelper = new KieHelper();

        KieBase kieBase = kHelper
                .setKieModuleModel(kModuleModel)
                .addContent(drl, ResourceType.DRL)
                .build(ExecutableModelProject.class);

        KieSession kieSession = kieBase.newKieSession();
        kieSession.insert("test");
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }
}
