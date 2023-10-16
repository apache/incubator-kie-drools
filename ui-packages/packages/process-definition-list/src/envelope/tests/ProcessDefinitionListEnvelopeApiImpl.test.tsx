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
  MockedEnvelopeClient,
  MockedProcessDefinitionListEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import {
  ProcessDefinitionListChannelApi,
  ProcessDefinitionListEnvelopeApi
} from '../../api';
import { ProcessDefinitionListEnvelopeApiImpl } from '../ProcessDefinitionListEnvelopeApiImpl';
import { ProcessDefinitionListEnvelopeViewApi } from '../ProcessDefinitionListEnvelopeView';
import { ProcessDefinitionListEnvelopeContext } from '../ProcessDefinitionListEnvelopeContext';

describe('ProcessDefinitionListEnvelopeApiImpl tests', () => {
  it('initialize', async () => {
    const envelopeClient = new MockedEnvelopeClient();
    const view = new MockedProcessDefinitionListEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      ProcessDefinitionListEnvelopeApi,
      ProcessDefinitionListChannelApi,
      ProcessDefinitionListEnvelopeViewApi,
      ProcessDefinitionListEnvelopeContext
    > = {
      envelopeClient,
      envelopeContext: {},
      viewDelegate: () => Promise.resolve(() => view)
    };

    const envelopeApi = new ProcessDefinitionListEnvelopeApiImpl(args);

    envelopeApi.processDefinitionList__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        singularProcessLabel: 'Workflow'
      }
    );

    expect(envelopeClient.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
    const initializedView = await view.initialize;
    expect(initializedView).toHaveBeenCalled();
  });
});
