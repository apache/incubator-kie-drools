/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.app.audit.jpa.queries;

import java.util.List;

import org.kie.kogito.app.audit.api.DataAuditContext;
import org.kie.kogito.app.audit.graphql.type.JobExecutionTO;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQuery;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQueryProvider;

public class JPAGraphQLSchemaJobsQueryProvider implements GraphQLSchemaQueryProvider {

    @Override
    public List<GraphQLSchemaQuery> queries(DataAuditContext dataAuditContext) {
        return List.of(
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllScheduledJobs", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetJobById", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetJobHistoryById", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetJobHistoryByProcessInstanceId", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllPendingJobs", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllEligibleJobsForExecution", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllEligibleJobsForRetry", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllJobs", "GetAllJobs", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllCompletedJobs", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllInErrorJobs", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllCancelledJobs", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetAllJobsByStatus", JobExecutionTO.class),
                new JPASimpleNamedQuery<JobExecutionTO>("GetJobByProcessInstanceId", JobExecutionTO.class));

    }

}
