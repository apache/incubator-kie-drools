/**
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
import {
  MessageBusClientApi,
  RequestPropertyNames
} from '@kie-tools-core/envelope-bus/dist/api';
import { WorkflowFormChannelApi } from '../../api';
import { WorkflowFormEnvelopeViewDriver } from '../WorkflowFormEnvelopeViewDriver';
import { MockedMessageBusClientApi } from './mocks/Mocks';

let channelApi: MessageBusClientApi<WorkflowFormChannelApi>;
let requests: Pick<
  WorkflowFormChannelApi,
  RequestPropertyNames<WorkflowFormChannelApi>
>;
let driver: WorkflowFormEnvelopeViewDriver;

describe('WorkflowFormEnvelopeViewDriver tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    channelApi = new MockedMessageBusClientApi();
    requests = channelApi.requests;
    driver = new WorkflowFormEnvelopeViewDriver(channelApi);
  });

  it('start custom workflow', () => {
    driver.startWorkflow('http://localhost:8080/test', { name: 'John' });
    expect(requests.workflowForm__startWorkflow).toHaveBeenCalledWith(
      'http://localhost:8080/test',
      { name: 'John' }
    );
  });
});
