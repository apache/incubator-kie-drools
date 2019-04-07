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

package org.jbpm.process.audit;

import java.util.List;

import org.jbpm.process.audit.command.AuditNodeInstanceLogQueryCommand;
import org.jbpm.process.audit.command.AuditProcessInstanceLogQueryCommand;
import org.jbpm.process.audit.command.AuditVariableInstanceLogQueryCommand;
import org.jbpm.process.audit.command.ClearHistoryLogsCommand;
import org.jbpm.process.audit.command.FindActiveProcessInstancesCommand;
import org.jbpm.process.audit.command.FindNodeInstancesCommand;
import org.jbpm.process.audit.command.FindProcessInstanceCommand;
import org.jbpm.process.audit.command.FindProcessInstancesCommand;
import org.jbpm.process.audit.command.FindSubProcessInstancesCommand;
import org.jbpm.process.audit.command.FindVariableInstancesByNameCommand;
import org.jbpm.process.audit.command.FindVariableInstancesCommand;
import org.jbpm.process.audit.query.NodeInstLogQueryBuilderImpl;
import org.jbpm.process.audit.query.NodeInstanceLogDeleteBuilderImpl;
import org.jbpm.process.audit.query.ProcInstLogQueryBuilderImpl;
import org.jbpm.process.audit.query.ProcessInstanceLogDeleteBuilderImpl;
import org.jbpm.process.audit.query.VarInstLogQueryBuilderImpl;
import org.jbpm.process.audit.query.VarInstanceLogDeleteBuilderImpl;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;

public class CommandBasedAuditLogService implements AuditLogService {

    private CommandExecutor executor;
    
    public CommandBasedAuditLogService(CommandExecutor executor) { 
       this.executor = executor; 
    }
    
    @Override
    public List<ProcessInstanceLog> findProcessInstances() {
        return executor.execute(new FindProcessInstancesCommand());
    }

    @Override
    public List<ProcessInstanceLog> findActiveProcessInstances() {
        return executor.execute(new FindActiveProcessInstancesCommand());
    }

    @Override
    public List<ProcessInstanceLog> findProcessInstances(String processId) {
        return executor.execute(new FindProcessInstancesCommand(processId));
    }

    @Override
    public List<ProcessInstanceLog> findActiveProcessInstances(String processId) {
        return executor.execute(new FindActiveProcessInstancesCommand(processId));
    }

    @Override
    public ProcessInstanceLog findProcessInstance(long processInstanceId) {
        return executor.execute(new FindProcessInstanceCommand(processInstanceId));
    }

    @Override
    public List<ProcessInstanceLog> findSubProcessInstances(long processInstanceId) {
        return executor.execute(new FindSubProcessInstancesCommand(processInstanceId));
    }

    @Override
    public List<NodeInstanceLog> findNodeInstances(long processInstanceId) {
        return executor.execute(new FindNodeInstancesCommand(processInstanceId));
    }

    @Override
    public List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId) {
        return executor.execute(new FindNodeInstancesCommand(processInstanceId, nodeId));
    }

    @Override
    public List<VariableInstanceLog> findVariableInstances(long processInstanceId) {
        return executor.execute(new FindVariableInstancesCommand(processInstanceId));
    }

    @Override
    public List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId) {
        return executor.execute(new FindVariableInstancesCommand(processInstanceId, variableId));
    }

    @Override
    public List<VariableInstanceLog> findVariableInstancesByName(String variableId, boolean activeProcesses) {
        return executor.execute(new FindVariableInstancesByNameCommand(variableId, activeProcesses));
    }

    @Override
    public List<VariableInstanceLog> findVariableInstancesByNameAndValue(String variableId, String value, boolean activeProcesses) {
        return executor.execute(new FindVariableInstancesByNameCommand(variableId, value, activeProcesses));
    }

    @Override
    public NodeInstanceLogQueryBuilder nodeInstanceLogQuery() {
        return new NodeInstLogQueryBuilderImpl(executor);
    }

    @Override
    public VariableInstanceLogQueryBuilder variableInstanceLogQuery() {
        return new VarInstLogQueryBuilderImpl(executor);
    }

    @Override
    public ProcessInstanceLogQueryBuilder processInstanceLogQuery() {
        return new ProcInstLogQueryBuilderImpl(executor);
    }
    
	@Override
	public ProcessInstanceLogDeleteBuilder processInstanceLogDelete() {
		return new ProcessInstanceLogDeleteBuilderImpl(executor);
	}
	
	@Override
	public NodeInstanceLogDeleteBuilder nodeInstanceLogDelete() {
		return new NodeInstanceLogDeleteBuilderImpl(executor);
	}
	
	@Override
	public VariableInstanceLogDeleteBuilder variableInstanceLogDelete() {
		return new VarInstanceLogDeleteBuilderImpl(executor);
	}

    @Override
    @SuppressWarnings("unchecked")
    public <T, R> List<R> queryLogs( QueryWhere queryWhere, Class<T> queryClass, Class<R> resultClass ) {
        if( queryClass.equals(NodeInstanceLog.class) ) { 
            return (List<R>) executor.execute(new AuditNodeInstanceLogQueryCommand(queryWhere));
        } else if( queryClass.equals(ProcessInstanceLog.class) ) { 
            return (List<R>) executor.execute(new AuditProcessInstanceLogQueryCommand(queryWhere));
        } else if( queryClass.equals(VariableInstanceLog.class) ) { 
            return (List<R>) executor.execute(new AuditVariableInstanceLogQueryCommand(queryWhere));
        } else { 
            String type = queryClass == null ? "null" : queryClass.getName();
            throw new IllegalArgumentException("Unknown type for query:" + type );
        }
    }

    @Override
    public void clear() {
        executor.execute(new ClearHistoryLogsCommand());
    }

    @Override
    public void dispose() {
       // no-op 
    }

}
