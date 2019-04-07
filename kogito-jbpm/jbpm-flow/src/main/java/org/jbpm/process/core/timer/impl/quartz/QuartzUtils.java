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

package org.jbpm.process.core.timer.impl.quartz;

import java.util.ArrayList;
import java.util.List;

import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.quartz.Trigger;
import org.quartz.impl.jdbcjobstore.StdJDBCConstants;

public class QuartzUtils implements StdJDBCConstants {

    // next trigger query extension
   String SELECT_NEXT_TRIGGER_TO_ACQUIRE = "SELECT "
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + ", "
            + COL_NEXT_FIRE_TIME + ", " + COL_PRIORITY + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_SCHEDULER_NAME + " = " + SCHED_NAME_SUBST
            + " AND " + COL_TRIGGER_STATE + " = ? AND " + COL_NEXT_FIRE_TIME + " <= ? " 
            + "AND (" + COL_MISFIRE_INSTRUCTION + " = -1 OR (" +COL_MISFIRE_INSTRUCTION+ " != -1 AND "+ COL_NEXT_FIRE_TIME + " >= ?)) ";           
            
    String ORDER_BY = "ORDER BY "+ COL_NEXT_FIRE_TIME + " ASC, " + COL_PRIORITY + " DESC";
    
    // count misfired triggers query extension    
    String COUNT_MISFIRED_TRIGGERS_IN_STATE = "SELECT COUNT("
            + COL_TRIGGER_NAME + ") FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_SCHEDULER_NAME + " = " + SCHED_NAME_SUBST + " AND NOT ("
            + COL_MISFIRE_INSTRUCTION + " = " + Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY + ") AND " 
            + COL_NEXT_FIRE_TIME + " < ? " 
            + "AND " + COL_TRIGGER_STATE + " = ? ";

    // misfired triggers query extension
    String SELECT_MISFIRED_TRIGGERS_IN_STATE = "SELECT "
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_SCHEDULER_NAME + " = " + SCHED_NAME_SUBST + " AND NOT ("
            + COL_MISFIRE_INSTRUCTION + " = " + Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY + ") AND " 
            + COL_NEXT_FIRE_TIME + " < ? AND " + COL_TRIGGER_STATE + " = ? ";
            
    String MISFIRED_ORDER_BY = "ORDER BY " + COL_NEXT_FIRE_TIME + " ASC, " + COL_PRIORITY + " DESC";
    
    public List<String> getDeployments() {
        List<String> deploymentIds = new ArrayList<>(RuntimeManagerRegistry.get().getRegisteredIdentifiers());
        // add jbpm as trigger group for backward compatibility
        deploymentIds.add("jbpm");
        
        return deploymentIds;
    }
    
    public String nextTriggerQuery(List<String> deploymentIds) {
                        
        String query = SELECT_NEXT_TRIGGER_TO_ACQUIRE + buildGroupFilter(deploymentIds) + ORDER_BY;
        
        return query;
    }
    
    public String countMisfiredTriggersQuery(List<String> deploymentIds) {
        
        String query = COUNT_MISFIRED_TRIGGERS_IN_STATE + buildGroupFilter(deploymentIds);
        
        return query;
    }
    
    public String misfiredTriggersQuery(List<String> deploymentIds) {
        
        String query = SELECT_MISFIRED_TRIGGERS_IN_STATE + buildGroupFilter(deploymentIds) + MISFIRED_ORDER_BY;
        
        return query;
    }
    
    protected String buildGroupFilter(List<String> deploymentIds) {
        StringBuilder filter = new StringBuilder(" (");
        deploymentIds.forEach(s -> filter.append("?,"));                
        filter.deleteCharAt(filter.length() - 1);
        filter.append(") ");
        
        String groupFilter = "AND " + COL_TRIGGER_GROUP + " IN " + filter;
        
        return groupFilter;
    }
}
