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
import { SortBy, ProcessInstanceFilter } from '@kogito-apps/process-list';
import { ProcessInstance } from '@kogito-apps/management-console-shared';
import { GraphQL } from '@kogito-apps/consoles-common';
import { buildProcessListWhereArgument } from '../../utils/QueryUtils';

export interface ProcessListQueries {
  getProcessInstances(
    start: number,
    end: number,
    filters: ProcessInstanceFilter,
    sortBy: SortBy
  ): Promise<ProcessInstance[]>;
  getChildProcessInstances(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]>;
}

export class GraphQLProcessListQueries implements ProcessListQueries {
  private readonly client: ApolloClient<any>;

  constructor(client: ApolloClient<any>) {
    this.client = client;
  }

  getProcessInstances(
    offset: number,
    limit: number,
    filters: ProcessInstanceFilter,
    sortBy: SortBy
  ): Promise<ProcessInstance[]> {
    return new Promise<ProcessInstance[]>((resolve, reject) => {
      this.client
        .query({
          query: GraphQL.GetProcessInstancesDocument,
          variables: {
            where: buildProcessListWhereArgument(filters),
            offset: offset,
            limit: limit,
            orderBy: sortBy
          },
          fetchPolicy: 'network-only'
        })
        .then(value => {
          resolve(value.data.ProcessInstances);
        })
        .catch(reason => reject(reason));
    });
  }

  getChildProcessInstances(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]> {
    return new Promise<ProcessInstance[]>((resolve, reject) => {
      this.client
        .query({
          query: GraphQL.GetChildInstancesDocument,
          variables: {
            rootProcessInstanceId
          }
        })
        .then(value => {
          resolve(value.data.ProcessInstances);
        })
        .catch(reason => reject(reason));
    });
  }
}
