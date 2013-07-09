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

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.drools.persistence.TransactionManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class has been deprecated because it only uses static methods, 
 * which when used with the static EMF instance, pose a risk in multi-threaded environments. 
 * 
 * Please use instances of the {@link AuditLogService} class instead. 
 */
@Deprecated
public class JPAProcessInstanceDbLog {

    private static Logger logger = LoggerFactory.getLogger(JPAProcessInstanceDbLog.class);
    
    private static AuditLogService staticAuditLogService = new JPAAuditLogService(null);
    
    @Deprecated
    public JPAProcessInstanceDbLog() {
    }
    
    @Deprecated
    public JPAProcessInstanceDbLog(Environment env){
        staticAuditLogService.setEnvironment(env);
    }

    public static void setEnvironment(Environment newEnv) { 
        staticAuditLogService.setEnvironment(newEnv);
    }
    
    @SuppressWarnings("unchecked")
    public static List<ProcessInstanceLog> findProcessInstances() {
        return staticAuditLogService.findProcessInstances();
    }

    @SuppressWarnings("unchecked")
    public static List<ProcessInstanceLog> findProcessInstances(String processId) {
        return staticAuditLogService.findProcessInstances(processId);
    }

    @SuppressWarnings("unchecked")
    public static List<ProcessInstanceLog> findActiveProcessInstances(String processId) {
        return staticAuditLogService.findActiveProcessInstances(processId);
    }

    public static ProcessInstanceLog findProcessInstance(long processInstanceId) {
        return staticAuditLogService.findProcessInstance(processInstanceId);
    }
    
    @SuppressWarnings("unchecked")
    public static List<ProcessInstanceLog> findSubProcessInstances(long processInstanceId) {
        return staticAuditLogService.findSubProcessInstances(processInstanceId);
    }
    
    @SuppressWarnings("unchecked")
    public static List<NodeInstanceLog> findNodeInstances(long processInstanceId) {
        return staticAuditLogService.findNodeInstances(processInstanceId);
    }

    @SuppressWarnings("unchecked")
    public static List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId) {
        return staticAuditLogService.findNodeInstances(processInstanceId, nodeId);
    }

    @SuppressWarnings("unchecked")
    public static List<VariableInstanceLog> findVariableInstances(long processInstanceId) {
        return staticAuditLogService.findVariableInstances(processInstanceId);
    }

    @SuppressWarnings("unchecked")
    public static List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId) {
        return staticAuditLogService.findVariableInstances(processInstanceId, variableId);
    }

    @SuppressWarnings("unchecked")
    public static void clear() {
        staticAuditLogService.clear();
    }

    @Deprecated
    public static void dispose() {
        staticAuditLogService.dispose();
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.staticAuditLogService.dispose();
    }

}
