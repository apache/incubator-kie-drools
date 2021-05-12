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

import { ProcessDetailsDriver } from '../../../api';
import {
  AbortResponse,
  Job,
  JobCancel,
  ProcessInstance,
  SvgErrorResponse,
  SvgSuccessResponse
} from '@kogito-apps/management-console-shared';

export default class TestProcessDetailsDriver implements ProcessDetailsDriver {
  constructor(id: string) {
    this.doSetState(id);
  }

  initialLoad(): Promise<void> {
    return Promise.resolve();
  }

  private doSetState(id: string) {
    // do nothing
  }

  processDetailsQuery(id: string): Promise<ProcessInstance> {
    this.doSetState(id);
    return null;
  }

  openProcessInstanceDetails(id: string): void {
    this.doSetState(id);
    // do nothing
  }

  abortProcess(data: ProcessInstance): Promise<AbortResponse> {
    return Promise.resolve(undefined);
  }

  cancelJob(job: Pick<Job, 'id' | 'endpoint'>): Promise<JobCancel> {
    return Promise.resolve(undefined);
  }

  getProcessDiagram(
    data: ProcessInstance
  ): Promise<SvgSuccessResponse | SvgErrorResponse> {
    return Promise.resolve(undefined);
  }

  jobsQuery(id: string): Promise<Job[]> {
    return Promise.resolve([]);
  }

  rescheduleJob(
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return Promise.resolve({ modalContent: '', modalTitle: '' });
  }
}
