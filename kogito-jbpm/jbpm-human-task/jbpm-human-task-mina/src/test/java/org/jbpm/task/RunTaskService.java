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

package org.jbpm.task;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.service.SendIcal;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.kie.SystemEventListenerFactory;

public class RunTaskService {

	private EntityManagerFactory emf;
	private TaskService taskService;
	private TaskServiceSession taskSession;
	private MinaTaskServer server;

	public static void main(String[] args) throws Exception {
		new RunTaskService().start();
	}

	@SuppressWarnings("unchecked")
	private void start() throws Exception {
		Properties conf = new Properties();
		conf.setProperty("mail.smtp.host", "localhost");
		conf.setProperty("mail.smtp.port", "2345");
		conf.setProperty("from", "from@domain.com");
		conf.setProperty("replyTo", "replyTo@domain.com");
		conf.setProperty("defaultLanguage", "en-UK");
		SendIcal.initInstance(conf);

		// Use persistence.xml configuration
		emf = Persistence.createEntityManagerFactory("org.kie.task");

		taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
		MockUserInfo userInfo = new MockUserInfo();
		taskService.setUserinfo(userInfo);
		Map<String, Object> vars = new HashMap();

		Reader reader = new InputStreamReader(RunTaskService.class.getResourceAsStream("LoadUsers.mvel"));
		Map<String, User> users = (Map<String, User>) eval(reader, vars);
		reader = new InputStreamReader(RunTaskService.class.getResourceAsStream("LoadGroups.mvel"));
		Map<String, Group> groups = (Map<String, Group>) eval(reader, vars);
		taskService.addUsersAndGroups(users, groups);
		
		server = new MinaTaskServer( taskService );
        Thread thread = new Thread( server );
        thread.start();
        Thread.sleep( 500 );
        System.out.println("Server started ...");
	}

	protected void stop() throws Exception {
        server.stop();
		taskSession.dispose();
		emf.close();
	}

    public Object eval(Reader reader, Map<String, Object> vars) {
        vars.put("now", new Date());
        return TaskService.eval(reader, vars);
    }

}
