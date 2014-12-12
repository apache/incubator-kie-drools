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

package org.jbpm.process.audit;

import java.util.List;

import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;

/**
 * Implementations of this class 
 * deal with {@link ProcessInstanceLog}, {@link NodeInstanceLog} 
 * and {@link VariableInstanceLog} entities. 
 * </p>
 * Please see the public methods for the interface of this service. 
 */
public interface AuditLogService extends AuditService {

    /**
     * Service methods
     * @return
     */
	@Override
    public List<ProcessInstanceLog> findProcessInstances();

    public List<ProcessInstanceLog> findActiveProcessInstances();
    
    public List<ProcessInstanceLog> findProcessInstances(String processId);

    public List<ProcessInstanceLog> findActiveProcessInstances(String processId);

    public ProcessInstanceLog findProcessInstance(long processInstanceId);

    public List<ProcessInstanceLog> findSubProcessInstances(long processInstanceId);

    public List<NodeInstanceLog> findNodeInstances(long processInstanceId);

    public List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId);

    public List<VariableInstanceLog> findVariableInstances(long processInstanceId);

    public List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId);

    public List<VariableInstanceLog> findVariableInstancesByName(String variableId, boolean onlyActiveProcesses);
    
    public List<VariableInstanceLog> findVariableInstancesByNameAndValue(String variableId, String value, boolean onlyActiveProcesses);
    
    /**
     * Creates a "query builder" instance that allows the user to specify the 
     * specific query criteria to retrieve {@link NodeInstanceLog} instances.
     * @return a {@link NodeInstanceLogQueryBuilder} instance
     */
    public NodeInstanceLogQueryBuilder nodeInstanceLogQuery();
    
    /**
     * Creates a "query builder" instance that allows the user to specify the 
     * specific query criteria to retrieve {@link VariableInstanceLog} instances.
     * @return a {@link VariableInstanceLogQueryBuilder} instance
     */
    public VariableInstanceLogQueryBuilder variableInstanceLogQuery();
    
    /**
     * Creates a "query builder" instance that allows the user to specify the 
     * specific query criteria to retrieve {@link ProcessInstanceLog} instances.
     * @return a {@link ProcessInstanceLogQueryBuilder} instance
     */
    public ProcessInstanceLogQueryBuilder processInstanceLogQuery();
    
    public ProcessInstanceLogDeleteBuilder processInstanceLogDelete();
    
    public NodeInstanceLogDeleteBuilder nodeInstanceLogDelete();
    
    public VariableInstanceLogDeleteBuilder variableInstanceLogDelete();
   
    public List<org.kie.api.runtime.manager.audit.NodeInstanceLog> queryNodeInstanceLogs(QueryData queryData);

    public List<org.kie.api.runtime.manager.audit.VariableInstanceLog> queryVariableInstanceLogs(QueryData queryData);
    
    public List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> queryProcessInstanceLogs(QueryData queryData);
}