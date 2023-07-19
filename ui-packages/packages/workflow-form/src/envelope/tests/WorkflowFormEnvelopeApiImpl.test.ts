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

import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import {
  MockedEnvelopeClient,
  MockedWorkflowFormEnvelopeViewApi
} from './mocks/Mocks';
import { WorkflowFormChannelApi, WorkflowFormEnvelopeApi } from '../../api';
import { WorkflowFormEnvelopeViewApi } from '../WorkflowFormEnvelopeView';
import { WorkflowFormEnvelopeContext } from '../WorkflowFormEnvelopeContext';
import { WorkflowFormEnvelopeApiImpl } from '../WorkflowFormEnvelopeApiImpl';

const workflowDefinitionData = {
  workflowName: 'workflow1',
  endpoint: 'http://localhost:4000'
};

describe('WorkflowFormEnvelopeApiImpl tests', () => {
  it('initialize', async () => {
    const envelopeClient = new MockedEnvelopeClient();
    const view = new MockedWorkflowFormEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      WorkflowFormEnvelopeApi,
      WorkflowFormChannelApi,
      WorkflowFormEnvelopeViewApi,
      WorkflowFormEnvelopeContext
    > = {
      envelopeClient,
      envelopeContext: {},
      viewDelegate: () => Promise.resolve(() => view)
    };

    const envelopeApi = new WorkflowFormEnvelopeApiImpl(args);

    envelopeApi.workflowForm__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        workflowDefinition: workflowDefinitionData
      }
    );

    expect(envelopeClient.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
    const initializedView = await view.initialize;
    expect(initializedView).toHaveBeenCalledWith({
      workflowDefinition: workflowDefinitionData
    });
  });
});
