/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.test.ejbservices.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.jbpm.test.container.mock.RestService;
import org.jbpm.services.api.model.VariableDesc;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.internal.query.QueryContext;

@Category({EAP.class, WAS.class, WLS.class})
public class ERestServiceTaskTest extends AbstractRuntimeEJBServicesTest {

    @BeforeClass
    public static void startRestService() {
        RestService.start();
    }

    @AfterClass
    public static void stopRestService() {
        RestService.stop();
    }

    @Before
    @Override
    public void deployKieJar() {
        if (kieJar == null) {
            kieJar = archive.deployServiceKieJar().getIdentifier();
        }
    }
    
    @Test
    public void testRestWorkItem() throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("url", RestService.PING_URL);
        parameters.put("method", "GET");
        
        Long pid = processService.startProcess(kieJar, REST_WORK_ITEM_PROCESS_ID, parameters);
        Assertions.assertThat(pid).isNotNull();
        
        Collection<VariableDesc> result = runtimeDataService.getVariableHistory(pid, "result", new QueryContext());
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.iterator().next().getNewValue()).isEqualTo("pong");
        
        Collection<VariableDesc> status = runtimeDataService.getVariableHistory(pid, "status", new QueryContext());
        Assertions.assertThat(status).hasSize(1);
        Assertions.assertThat(status.iterator().next().getNewValue()).isEqualTo("200");
        
        Collection<VariableDesc> statusMsg = runtimeDataService.getVariableHistory(pid, "statusMsg", new QueryContext());
        Assertions.assertThat(statusMsg).hasSize(1);
        Assertions.assertThat(statusMsg.iterator().next().getNewValue()).contains("successfully completed Ok");
        
    }
    
}
