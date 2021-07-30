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
  SvgSuccessResponse,
  SvgErrorResponse,
  TriggerableNode,
  NodeInstance
} from '@kogito-apps/management-console-shared';
import {
  getSvg,
  handleJobReschedule,
  handleProcessAbort,
  handleProcessRetry,
  handleProcessSkip,
  jobCancel,
  getTriggerableNodes,
  handleNodeTrigger,
  handleProcessVariableUpdate,
  handleNodeInstanceCancel,
  handleNodeInstanceRetrigger
} from '../apis';

export interface OnOpenProcessInstanceDetailsListener {
  onOpen(id: string): void;
}

export interface ProcessDetailsUnSubscribeHandler {
  unSubscribe: () => void;
}

export interface ProcessDetailsState {
  id: string;
}

export interface ProcessDetailsGatewayApi {
  processDetailsState: any;
  getProcessDiagram: (
    data: ProcessInstance
  ) => Promise<SvgSuccessResponse | SvgErrorResponse>;
  handleProcessAbort: (processInstance: ProcessInstance) => Promise<void>;
  cancelJob: (job: Pick<Job, 'id' | 'endpoint'>) => Promise<JobCancel>;
  rescheduleJob: (
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ) => Promise<{ modalTitle: string; modalContent: string }>;
  getTriggerableNodes(
    processInstance: ProcessInstance
  ): Promise<TriggerableNode[]>;
  handleNodeTrigger(processInstance: ProcessInstance, node: any): Promise<void>;
  handleProcessVariableUpdate: (
    processInstance: ProcessInstance,
    updateJson: Record<string, unknown>
  ) => Promise<Record<string, unknown>>;
  processDetailsQuery(id: string): Promise<ProcessInstance>;
  jobsQuery(id: string): Promise<Job[]>;
  openProcessInstanceDetails(id: string): Promise<void>;
  onOpenProcessInstanceDetailsListener: (
    listener: OnOpenProcessInstanceDetailsListener
  ) => ProcessDetailsUnSubscribeHandler;
  handleProcessRetry: (processInstance: ProcessInstance) => Promise<void>;
  handleNodeInstanceCancel: (
    processInstance: ProcessInstance,
    node: NodeInstance
  ) => Promise<void>;
  handleProcessSkip: (processInstance: ProcessInstance) => Promise<void>;
  handleNodeInstanceRetrigger(
    processInstance: ProcessInstance,
    node: NodeInstance
  ): Promise<void>;
}

export class ProcessDetailsGatewayApiImpl implements ProcessDetailsGatewayApi {
  private readonly queries: ProcessDetailsQueries;
  private _ProcessDetailsState: ProcessDetailsState;
  private readonly listeners: OnOpenProcessInstanceDetailsListener[] = [];

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

  handleProcessAbort = (processInstance: ProcessInstance): Promise<void> => {
    return handleProcessAbort(processInstance);
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

  getTriggerableNodes(
    processInstance: ProcessInstance
  ): Promise<TriggerableNode[]> {
    return getTriggerableNodes(processInstance);
  }

  handleNodeTrigger(
    processInstance: ProcessInstance,
    node: TriggerableNode
  ): Promise<void> {
    return handleNodeTrigger(processInstance, node);
  }

  handleProcessVariableUpdate = (
    processInstance: ProcessInstance,
    updatedJson: Record<string, unknown>
  ) => {
    return handleProcessVariableUpdate(processInstance, updatedJson);
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

  openProcessInstanceDetails(id: string): Promise<void> {
    this._ProcessDetailsState = { id: id };
    this.listeners.forEach(listener => listener.onOpen(id));
    return Promise.resolve();
  }

  onOpenProcessInstanceDetailsListener(
    listener: OnOpenProcessInstanceDetailsListener
  ): ProcessDetailsUnSubscribeHandler {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return {
      unSubscribe
    };
  }

  handleProcessRetry(processInstance: ProcessInstance): Promise<void> {
    return handleProcessRetry(processInstance);
  }

  handleNodeInstanceCancel(
    processInstance: ProcessInstance,
    node: NodeInstance
  ): Promise<void> {
    return handleNodeInstanceCancel(processInstance, node);
  }

  handleProcessSkip(processInstance: ProcessInstance): Promise<void> {
    return handleProcessSkip(processInstance);
  }

  handleNodeInstanceRetrigger(
    processInstance: ProcessInstance,
    node: NodeInstance
  ): Promise<void> {
    return handleNodeInstanceRetrigger(processInstance, node);
  }
}
