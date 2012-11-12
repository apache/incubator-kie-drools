/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.task.service.test.async;

import static org.jbpm.task.service.test.impl.TestServerUtil.createTestTaskClientConnector;
import static org.jbpm.task.service.test.impl.TestServerUtil.startAsyncServer;

import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.base.async.IcalBaseAsyncTest;
import org.jbpm.task.service.test.impl.TestTaskServer;
import org.junit.AfterClass;
import org.kie.util.ChainedProperties;
import org.kie.util.ClassLoaderUtil;
import org.subethamail.wiser.Wiser;

public class IcalAsyncTest extends IcalBaseAsyncTest {

    @Override 
    protected void setUp() throws Exception {
        super.setUp();
        
        ChainedProperties props = new ChainedProperties("process.email.conf", ClassLoaderUtil.getClassLoader(null, getClass(), false ));
        setEmailHost(props.getProperty("host", "locahost"));
        setEmailPort(props.getProperty("port", "2345"));        
        
        server = startAsyncServer(taskService);

        client = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        client.connect();

        setWiser(new Wiser());
        getWiser().setHostname(getEmailHost());
        getWiser().setPort(Integer.parseInt(getEmailPort()));         
        getWiser().start();
    }

    @AfterClass
    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        getWiser().stop();
        super.tearDown();
    }

}
