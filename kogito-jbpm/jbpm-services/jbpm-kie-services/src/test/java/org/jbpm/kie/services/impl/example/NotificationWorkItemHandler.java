/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.kie.services.impl.example;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author esteban
 */
public class NotificationWorkItemHandler implements WorkItemHandler{
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationWorkItemHandler.class);

    public final static String WIP_INPUT_RELEASE = "in_release_name";
    public final static String WIP_INPUT_REPORT = "in_test_report";
    public final static String WIP_INPUT_EMAILS = "in_users";
    
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        
        //Read release name
        String releaseName = (String) workItem.getParameter(WIP_INPUT_RELEASE);

        //Read report
        String report = (String) workItem.getParameter(WIP_INPUT_REPORT);
        
        //Read emails
        String emails = (String) workItem.getParameter(WIP_INPUT_EMAILS);
        
        //check mandatory parameters
        if (releaseName == null || releaseName.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_RELEASE + "' parameter is mandatory!");
        }

        if (report == null || report.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_REPORT + "' parameter is mandatory!");
        }
        
        if (emails == null || emails.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_EMAILS + "' parameter is mandatory!");
        }

        
        StringBuilder email = new StringBuilder("");
        email.append("To: ").append(emails).append("\n");
        email.append("Subject: ").append(releaseName).append(" Released!\n");
        email.append("Body: \n").append(report).append("\n");
        
        logger.debug("{}",email.toString());
        
        manager.completeWorkItem(workItem.getId(), null);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }
    
}
