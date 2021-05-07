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

import { ProcessDetailsQueries } from './ProcessDetailsQueries';
import {
  ProcessInstance,
  Job,
  JobCancel,
  AbortResponse,
  SvgSuccessResponse,
  SvgErrorResponse
} from '@kogito-apps/management-console-shared';
import {
  getSvg,
  handleJobReschedule,
  handleAbort,
  jobCancel
} from '../../apis';

export interface ProcessDetailsGatewayApi {
  processDetailsState: any;
  getProcessDiagram: (
    data: ProcessInstance
  ) => Promise<SvgSuccessResponse | SvgErrorResponse>;
  abortProcess: (data: ProcessInstance) => Promise<AbortResponse>;
  cancelJob: (job: Pick<Job, 'id' | 'endpoint'>) => Promise<JobCancel>;
  rescheduleJob: (
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ) => Promise<{ modalTitle: string; modalContent: string }>;
  processDetailsQuery(id: string): Promise<ProcessInstance>;
  jobsQuery(id: string): Promise<Job[]>;
}

export interface ProcessDetailsState {
  id: string;
}

export class ProcessDetailsGatewayApiImpl implements ProcessDetailsGatewayApi {
  private readonly queries: ProcessDetailsQueries;
  private _ProcessDetailsState: ProcessDetailsState;

  constructor(queries: ProcessDetailsQueries) {
    this.queries = queries;
    this._ProcessDetailsState = { id: '' };
  }

  get processDetailsState(): ProcessDetailsState {
    return this._ProcessDetailsState;
  }

  getProcessDiagram = async (
    data: ProcessInstance
  ): Promise<SvgSuccessResponse | SvgErrorResponse> => {
    const res = await getSvg(data);
    return Promise.resolve(res);
  };

  abortProcess = (data: ProcessInstance): Promise<AbortResponse> => {
    return handleAbort(data);
  };

  cancelJob = (job: Pick<Job, 'id' | 'endpoint'>): Promise<JobCancel> => {
    return jobCancel(job);
  };

  rescheduleJob = (
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> => {
    return handleJobReschedule(job, repeatInterval, repeatLimit, scheduleDate);
  };

  processDetailsQuery(id: string): Promise<ProcessInstance> {
    return new Promise<any>((resolve, reject) => {
      this.queries
        .getProcessDetails(id)
        .then(value => {
          resolve(value);
        })
        .catch(reason => {
          reject(reason);
        });
    });
  }

  jobsQuery(id: string): Promise<Job[]> {
    return new Promise<any>((resolve, reject) => {
      this.queries
        .getJobs(id)
        .then(value => {
          resolve(value);
        })
        .catch(reason => {
          reject(reason);
        });
    });
  }
}
