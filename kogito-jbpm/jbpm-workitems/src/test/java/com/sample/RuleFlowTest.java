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

package com.sample;

import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.kie.logger.KnowledgeRuntimeLogger;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.process.instance.WorkItemHandler;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.process.workitem.archive.ArchiveWorkItemHandler;
import org.jbpm.process.workitem.email.EmailWorkItemHandler;
import org.jbpm.process.workitem.exec.ExecWorkItemHandler;
import org.jbpm.process.workitem.finder.FinderWorkItemHandler;
import org.jbpm.process.workitem.transform.FileTransformer;
import org.jbpm.process.workitem.transform.TransformWorkItemHandler;
import org.junit.Ignore;

/**
 * This is a sample file to launch a ruleflow.
 */
@Ignore
public class RuleFlowTest {

	public static final void main(String[] args) {
		try {
			
			KnowledgeBase kbase = createKnowledgeBase();
			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			ksession.getWorkItemManager().registerWorkItemHandler("Finder", new FinderWorkItemHandler());
			ksession.getWorkItemManager().registerWorkItemHandler("Archive", new ArchiveWorkItemHandler());
			ksession.getWorkItemManager().registerWorkItemHandler("Exec", new ExecWorkItemHandler());
			ksession.getWorkItemManager().registerWorkItemHandler("Log", new WorkItemHandler() {
				public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
					System.out.println("Log: " + workItem.getParameter("Message"));
					manager.completeWorkItem(workItem.getId(), null);
				}
				public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
				}
			});
			EmailWorkItemHandler emailWorkItemHandler = new EmailWorkItemHandler();
			emailWorkItemHandler.setConnection("mail-out.example.com", "25", null, null);
			ksession.getWorkItemManager().registerWorkItemHandler("Email", emailWorkItemHandler);
			TransformWorkItemHandler transformWorkItemHandler = new TransformWorkItemHandler();
			transformWorkItemHandler.registerTransformer(FileTransformer.class);
			ksession.getWorkItemManager().registerWorkItemHandler("Transform", transformWorkItemHandler);
			KnowledgeRuntimeLogger log = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
			ksession.startProcess("com.sample.ruleflow");
			log.close();
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase createKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(new ClassPathResource("/FileFinder.rf"), ResourceType.DRF);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		return kbase;
	}

}