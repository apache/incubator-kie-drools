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
  getCustomWorkflowSchema,
  startWorkflowCloudEvent,
  startWorkflowRest
} from '../../apis';
import {
  WorkflowFormGatewayApi,
  WorkflowFormGatewayApiImpl
} from '../WorkflowFormGatewayApi';
import { WorkflowDefinition } from '@kogito-apps/workflow-form/dist/api';

jest.mock('../../apis/apis', () => ({
  startWorkflowCloudEvent: jest.fn(),
  getCustomWorkflowSchema: jest.fn(),
  startWorkflowRest: jest.fn()
}));

let gatewayApi: WorkflowFormGatewayApi;

describe('WorkflowFormListGatewayApi tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new WorkflowFormGatewayApiImpl('baseUrl', '/q/dev');
  });

  it('start workflow instance', async () => {
    const workflowDefinitionData: WorkflowDefinition = {
      workflowName: 'workflow1',
      endpoint: 'http://localhost:8080/hiring'
    };
    await gatewayApi.startWorkflowCloudEvent(workflowDefinitionData);
    expect(startWorkflowCloudEvent).toHaveBeenCalledWith(
      workflowDefinitionData,
      '',
      'baseUrl'
    );
  });

  it('get custom workflow schema', async () => {
    const workflowName = 'expression';
    await gatewayApi.getCustomWorkflowSchema(workflowName);
    expect(getCustomWorkflowSchema).toHaveBeenCalledWith(
      'baseUrl',
      '/q/dev',
      'expression'
    );
  });

  it('start workflow rest', async () => {
    await gatewayApi.startWorkflowRest(
      { name: 'John' },
      'http://localhost:8080/test'
    );
    expect(startWorkflowRest).toHaveBeenCalledWith(
      { name: 'John' },
      'http://localhost:8080/test',
      ''
    );
  });
});
