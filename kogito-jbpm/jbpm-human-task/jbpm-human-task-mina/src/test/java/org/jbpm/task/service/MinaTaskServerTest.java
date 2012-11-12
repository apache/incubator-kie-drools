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

package org.jbpm.task.service;

import org.jbpm.task.BaseTest;
import org.jbpm.task.service.mina.MinaTaskServer;

public class MinaTaskServerTest extends BaseTest {


	private MinaTaskServer minaTaskServer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	

	
	public void testMinaTaskServer() throws Exception {
		minaTaskServer = new MinaTaskServer(taskService);
		Thread t = new Thread(minaTaskServer);
		t.start();
		Thread.sleep(5000);
		minaTaskServer.stop();
	}

}
