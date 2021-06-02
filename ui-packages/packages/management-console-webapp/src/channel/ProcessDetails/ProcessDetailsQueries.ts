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

import { ApolloClient } from 'apollo-client';
import { GraphQL } from '@kogito-apps/consoles-common';
import { ProcessInstance, Job } from '@kogito-apps/management-console-shared';

export interface ProcessDetailsQueries {
  getProcessDetails(id: string): Promise<ProcessInstance>;
  getJobs(id: string): Promise<Job[]>;
}

export class GraphQLProcessDetailsQueries implements ProcessDetailsQueries {
  private readonly client: ApolloClient<any>;

  constructor(client: ApolloClient<any>) {
    this.client = client;
  }

  async getProcessDetails(id: string): Promise<ProcessInstance> {
    try {
      const response = await this.client.query({
        query: GraphQL.GetProcessInstanceByIdDocument,
        variables: {
          id
        },
        fetchPolicy: 'network-only'
      });
      const emptyResponse = {} as ProcessInstance;
      if (response && response.data.ProcessInstances.length > 0) {
        return Promise.resolve(response.data.ProcessInstances[0]);
      } else {
        return Promise.resolve(emptyResponse);
      }
    } catch (error) {
      return Promise.reject(error);
    }
  }

  async getJobs(id: string): Promise<Job[]> {
    try {
      const response = await this.client.query({
        query: GraphQL.GetJobsByProcessInstanceIdDocument,
        variables: {
          processInstanceId: id
        },
        fetchPolicy: 'network-only'
      });
      return Promise.resolve(response.data.Jobs);
    } catch (error) {
      return Promise.reject(error);
    }
  }
}
