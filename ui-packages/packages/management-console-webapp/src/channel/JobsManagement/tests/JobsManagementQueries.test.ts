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

import { act } from 'react-dom/test-utils';
import reactApollo from 'react-apollo';
import { GraphQLJobsManagementQueries } from '../JobsManagementQueries';
import { JobStatus } from '@kogito-apps/management-console-shared';
jest.mock('apollo-client');

jest.mock('react-apollo', () => {
  const ApolloClient = { query: jest.fn() };
  return { useApolloClient: jest.fn(() => ApolloClient) };
});

const mGraphQLResponse = {
  data: {
    Jobs: [
      {
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
        endpoint: 'http://localhost:4000/jobs',
        executionCounter: 0,
        expirationTime: new Date('2020-08-29T04:35:54.631Z'),
        id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
        lastUpdate: new Date('2020-06-29T03:35:54.635Z'),
        priority: 0,
        processId: 'travels',
        processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        repeatInterval: null,
        repeatLimit: null,
        retries: 2,
        rootProcessId: '',
        scheduledId: null,
        status: JobStatus.Scheduled
      }
    ]
  }
};
describe('JobsManagementQueries tests', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  let Queries: GraphQLJobsManagementQueries;

  const offset = 0;
  const limit = 10;
  const filters = [JobStatus.Scheduled];
  const orderBy: any = { lastUpdate: 'ASC' };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
    Queries = new GraphQLJobsManagementQueries(client);
  });
  it('test getJobs method with success response', async () => {
    client.query.mockReturnValueOnce(mGraphQLResponse);
    const response = await Queries.getJobs(offset, limit, filters, orderBy);
    expect(response).toEqual(mGraphQLResponse.data.Jobs);
  });
});
