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

import { JobsManagementChannelApi, JobsManagementDriver } from '../api';
import {
  Job,
  JobStatus,
  BulkCancel,
  JobCancel,
  JobsSortBy
} from '@kogito-apps/management-console-shared';

export class JobsManagementChannelApiImpl implements JobsManagementChannelApi {
  constructor(private readonly driver: JobsManagementDriver) {}

  jobList__initialLoad(
    filter: JobStatus[],
    orderBy: JobsSortBy
  ): Promise<void> {
    return this.driver.initialLoad(filter, orderBy);
  }

  jobList__applyFilter(filter: JobStatus[]): Promise<void> {
    return this.driver.applyFilter(filter);
  }

  jobList__bulkCancel(jobsToBeActioned: Job[]): Promise<BulkCancel> {
    return this.driver.bulkCancel(jobsToBeActioned);
  }

  jobList_cancelJob(job: Pick<Job, 'id' | 'endpoint'>): Promise<JobCancel> {
    return this.driver.cancelJob(job);
  }

  jobList_rescheduleJob(
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return this.driver.rescheduleJob(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
  }

  jobList_sortBy(orderBy: JobsSortBy): Promise<void> {
    return this.driver.sortBy(orderBy);
  }

  jobList__query(offset: number, limit: number): Promise<Job[]> {
    return this.driver.query(offset, limit);
  }
}
