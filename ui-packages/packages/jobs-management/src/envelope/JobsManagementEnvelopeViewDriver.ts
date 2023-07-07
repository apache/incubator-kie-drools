/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { MessageBusClientApi } from '@kogito-tooling/envelope-bus/dist/api';
import {
  Job,
  JobStatus,
  BulkCancel,
  JobCancel,
  JobsSortBy
} from '@kogito-apps/management-console-shared';
import { JobsManagementChannelApi, JobsManagementDriver } from '../api';

export default class JobsManagementEnvelopeViewDriver
  implements JobsManagementDriver
{
  constructor(
    private readonly channelApi: MessageBusClientApi<JobsManagementChannelApi>
  ) {}

  initialLoad(filter: JobStatus[], orderBy: JobsSortBy): Promise<void> {
    return this.channelApi.requests.jobList__initialLoad(filter, orderBy);
  }

  applyFilter(filter: JobStatus[]): Promise<void> {
    return this.channelApi.requests.jobList__applyFilter(filter);
  }

  bulkCancel(jobsToBeActioned: Job[]): Promise<BulkCancel> {
    return this.channelApi.requests.jobList__bulkCancel(jobsToBeActioned);
  }

  cancelJob(job: Pick<Job, 'id' | 'endpoint'>): Promise<JobCancel> {
    return this.channelApi.requests.jobList_cancelJob(job);
  }

  rescheduleJob(
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return this.channelApi.requests.jobList_rescheduleJob(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
  }

  sortBy(orderBy: JobsSortBy): Promise<void> {
    return this.channelApi.requests.jobList_sortBy(orderBy);
  }

  query(offset: number, limit: number): Promise<Job[]> {
    return this.channelApi.requests.jobList__query(offset, limit);
  }
}
