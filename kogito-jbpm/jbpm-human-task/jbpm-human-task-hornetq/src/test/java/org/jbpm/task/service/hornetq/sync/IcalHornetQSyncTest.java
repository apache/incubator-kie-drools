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

package org.jbpm.task.service.hornetq.sync;

import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.base.sync.IcalBaseSyncTest;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskServer;
import org.kie.util.ChainedProperties;
import org.kie.util.ClassLoaderUtil;
import org.subethamail.wiser.Wiser;

public class IcalHornetQSyncTest extends IcalBaseSyncTest {

    protected void setUp() throws Exception {
        super.setUp();
        
        ChainedProperties props = new ChainedProperties("process.email.conf", ClassLoaderUtil.getClassLoader(null, getClass(), false ));
        setEmailHost(props.getProperty("host", "locahost"));
        setEmailPort(props.getProperty("port", "2345"));        
        
        server = new HornetQTaskServer(taskService, 5153);
		System.out.println("Waiting for the HornetQTask Server to come up");
        try {
            startTaskServerThread(server, false);
        } catch (Exception e) {
            startTaskServerThread(server, true);
        }

        client = new SyncTaskServiceWrapper(new AsyncHornetQTaskClient());
        client.connect("127.0.0.1", 5153);

        setWiser(new Wiser());
        getWiser().setHostname(getEmailHost());
        getWiser().setPort(Integer.parseInt(getEmailPort()));         
        getWiser().start();
    }

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }
}
