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
import { ProcessInstance, Job } from '@kogito-apps/management-console-shared';

export interface ProcessDetailsGatewayApi {
  processDetailsState: any;
  initialLoad: () => Promise<void>;
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

  initialLoad = (): Promise<any> => {
    return Promise.resolve();
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

  jobsQuery(id: string): Promise<any> {
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
