/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.runtime.process;


import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetWorkItemIdsCommand implements ExecutableCommand<List<Long>> {

    /** generated serial version UID */
    private static final long serialVersionUID = 1471981530823361925L;

    public List<Long> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        Set<WorkItem> workItems = ((WorkItemManager) ksession.getWorkItemManager()).getWorkItems();
        List<Long> workItemIds = new ArrayList<Long>(workItems.size());
        for(WorkItem workItem : workItems ) { 
            workItemIds.add(workItem.getId());
        }
        return workItemIds;
    }

}
