package org.jbpm.process.audit;

import java.util.List;

import org.jbpm.process.audit.command.ClearHistoryLogsCommand;
import org.jbpm.process.audit.command.FindActiveProcessInstancesCommand;
import org.jbpm.process.audit.command.FindNodeInstancesCommand;
import org.jbpm.process.audit.command.FindProcessInstanceCommand;
import org.jbpm.process.audit.command.FindProcessInstancesCommand;
import org.jbpm.process.audit.command.FindSubProcessInstancesCommand;
import org.jbpm.process.audit.command.FindVariableInstancesByNameCommand;
import org.jbpm.process.audit.command.FindVariableInstancesCommand;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Environment;

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
    public void clear() {
        executor.execute(new ClearHistoryLogsCommand());
    }

    @Override
    public void dispose() {
       // no-op 
    }

}
