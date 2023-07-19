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

import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import {
  MockedEnvelopeClient,
  MockedProcessFormEnvelopeViewApi
} from './mocks/Mocks';
import { ProcessFormChannelApi, ProcessFormEnvelopeApi } from '../../api';
import { ProcessFormEnvelopeViewApi } from '../ProcessFormEnvelopeView';
import { ProcessFormEnvelopeContext } from '../ProcessFormEnvelopeContext';
import { ProcessFormEnvelopeApiImpl } from '../ProcessFormEnvelopeApiImpl';

const processDefinitionData = {
  processName: 'process1',
  endpoint: 'http://localhost:4000'
};

describe('ProcessFormEnvelopeApiImpl tests', () => {
  it('initialize', async () => {
    const envelopeClient = new MockedEnvelopeClient();
    const view = new MockedProcessFormEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      ProcessFormEnvelopeApi,
      ProcessFormChannelApi,
      ProcessFormEnvelopeViewApi,
      ProcessFormEnvelopeContext
    > = {
      envelopeClient,
      envelopeContext: {},
      viewDelegate: () => Promise.resolve(() => view)
    };

    const envelopeApi = new ProcessFormEnvelopeApiImpl(args);

    envelopeApi.processForm__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        processDefinition: processDefinitionData
      }
    );

    expect(envelopeClient.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
    const initializedView = await view.initialize;
    expect(initializedView).toHaveBeenCalledWith({
      processDefinition: processDefinitionData
    });
  });
});
