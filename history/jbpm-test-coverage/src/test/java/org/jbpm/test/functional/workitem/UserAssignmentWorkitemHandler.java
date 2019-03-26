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
package org.jbpm.test.functional.workitem;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAssignmentWorkitemHandler implements WorkItemHandler{

    public static final String fnameStr = "michael";
    public static final String lnameStr = "jordan";

    private static final Logger logger = LoggerFactory.getLogger(UserAssignmentWorkitemHandler.class);

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        String fname = (String) workItem.getParameter("firstNameIn");
        String lname = (String) workItem.getParameter("lastNameIn");

        logger.info("got parameters: " + fname + " and " + lname);

        // set fname, lname so and set as results so its mapped
        // to process vars according to workitem assignments
        fname = fnameStr;
        lname = lnameStr;

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("firstNameOut", fname);
        results.put("lastNameOut", lname);
        workItemManager.completeWorkItem(workItem.getId(), results);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }
}
