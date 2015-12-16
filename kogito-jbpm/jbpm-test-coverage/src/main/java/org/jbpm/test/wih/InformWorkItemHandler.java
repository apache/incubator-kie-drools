/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.wih;

import java.util.Map;

import org.jbpm.casemgmt.CaseMgmtService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class InformWorkItemHandler implements WorkItemHandler {
    
    private CaseMgmtService caseMgmtService;
    private String lastMessage;
    private String lastMessageToWhom;
    
    public InformWorkItemHandler() {
        
    }
    
    public void setCaseMgmtService(CaseMgmtService caseMgmtService) {
        this.caseMgmtService = caseMgmtService;
    }
    
    public CaseMgmtService getCaseMgmtService() {
        return caseMgmtService;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
    
    public String getLastMessageToWhom() {
        return lastMessageToWhom;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        long caseId = workItem.getProcessInstanceId();
        Map<String, String[]> caseRoleInstanceNames = caseMgmtService.getCaseRoleInstanceNames(caseId);
        String[] usersToBeInformed = caseRoleInstanceNames.get("informed");
        lastMessage = (String) caseMgmtService.getCaseData(caseId).get("informAbout");
        lastMessageToWhom = "";
        
        for (String userId : usersToBeInformed) {
            System.out.println(userId + " <= " + lastMessage);
            lastMessageToWhom = userId + ",";
        }
        manager.completeWorkItem(workItem.getId(), null);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        
    }

}
