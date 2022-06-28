/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import {
  MessageBusClientApi,
  RequestPropertyNames
} from '@kogito-tooling/envelope-bus/dist/api';
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

  it('doSubmit', () => {
    const formJSON = {
      something: 'something'
    };
    driver.startWorkflow(formJSON);

    expect(requests.workflowForm__startWorkflow).toHaveBeenCalledWith(formJSON);
  });
});
