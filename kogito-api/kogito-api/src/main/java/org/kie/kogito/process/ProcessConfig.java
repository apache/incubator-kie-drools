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
package org.kie.kogito.process;

import org.kie.kogito.KogitoConfig;
import org.kie.kogito.auth.AuthTokenProvider;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.calendar.BusinessCalendar;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.signal.SignalManagerHub;
import org.kie.kogito.uow.UnitOfWorkManager;

public interface ProcessConfig extends KogitoConfig {

    WorkItemHandlerConfig workItemHandlers();

    ProcessEventListenerConfig processEventListeners();

    SignalManagerHub signalManagerHub();

    UnitOfWorkManager unitOfWorkManager();

    JobsService jobsService();

    ProcessVersionResolver versionResolver();

    IdentityProvider identityProvider();

    AuthTokenProvider authTokenProvider();

    BusinessCalendar getBusinessCalendar();
}
