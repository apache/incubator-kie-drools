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
package org.drools.testcoverage.functional.model;

import java.io.IOException;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;

public class RulesWithInTest {

    @Test
    public void testRecreateKieBaseNewContainer() throws IOException {
        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = createKJar(kieServices);

        kieServices.newKieContainer(releaseId).newKieBase(kieServices.newKieBaseConfiguration());
        kieServices.newKieContainer(releaseId).newKieBase(kieServices.newKieBaseConfiguration());
    }

    @Test
    public void testRecreateKieBaseReuseContainer() throws IOException {
        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = createKJar(kieServices);

        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        kieContainer.newKieBase(kieServices.newKieBaseConfiguration());
        kieContainer.newKieBase(kieServices.newKieBaseConfiguration());
    }

    private ReleaseId createKJar(final KieServices kieServices) throws IOException {
        final String drl = "package org.drools.testcoverage.functional; \n"
                + "rule \"testRule\" \n"
                + "when \n"
                + "    String(this == \"test\") \n"
                + "then \n"
                + "end\n";
        final Resource drlResource = kieServices.getResources().newByteArrayResource(drl.getBytes());
        drlResource.setResourceType(ResourceType.DRL);
        drlResource.setTargetPath("org/drools/testcoverage/functional/model/testFile.drl");

        return BuildtimeUtil.createKJarFromResources(true, drlResource);
    }
}
