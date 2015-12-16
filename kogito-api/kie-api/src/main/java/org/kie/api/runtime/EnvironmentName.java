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

package org.kie.api.runtime;

public class EnvironmentName {
    public static final String TRANSACTION_MANAGER                  = "org.kie.transaction.TransactionManager";
    public static final String TRANSACTION_SYNCHRONIZATION_REGISTRY = "org.kie.transaction.TransactionSynchronizationRegistry";
    public static final String TRANSACTION                          = "org.kie.transaction.Transaction";
    public static final String USE_LOCAL_TRANSACTIONS               = "org.kie.transaction.local";

    public static final String ENTITY_MANAGER_FACTORY               = "org.kie.api.persistence.jpa.EntityManagerFactory";
    public static final String CMD_SCOPED_ENTITY_MANAGER            = "org.kie.api.persistence.jpa.CmdScopedEntityManager";
    public static final String APP_SCOPED_ENTITY_MANAGER            = "org.kie.api.persistence.jpa.AppScopedEntityManager";
    public static final String PERSISTENCE_CONTEXT_MANAGER          = "org.kie.api.persistence.PersistenceContextManager";
    public static final String TASK_PERSISTENCE_CONTEXT_MANAGER     = "org.kie.api.persistence.TaskPersistenceContextManager";
    public static final String USE_PESSIMISTIC_LOCKING              = "org.kie.api.persistence.pessimistic";

    public static final String OBJECT_MARSHALLING_STRATEGIES        = "org.kie.api.marshalling.ObjectMarshallingStrategies";
    public static final String GLOBALS                              = "org.kie.Globals";
    public static final String CALENDARS                            = "org.kie.api.time.Calendars";
    public static final String DATE_FORMATS                         = "org.kie.build.DateFormats";
    
    public static final String TASK_USER_GROUP_CALLBACK             = "org.kie.api.task.UserGroupCallback";
    public static final String TASK_USER_INFO                       = "org.kie.api.task.UserInfo";
}
