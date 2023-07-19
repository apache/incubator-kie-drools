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

import {
  MockedEnvelopeClient,
  MockedProcessListEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import {
  ProcessListChannelApi,
  ProcessListEnvelopeApi
} from '../../api';
import { ProcessListEnvelopeApiImpl } from '../ProcessListEnvelopeApiImpl';
import { ProcessListEnvelopeViewApi } from '../ProcessListEnvelopeView';
import { ProcessListEnvelopeContext } from '../ProcessListEnvelopeContext';
import {
  OrderBy,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';

describe('ProcessListEnvelopeApiImpl tests', () => {
  it('initialize', async () => {
    const envelopeClient = MockedEnvelopeClient;
    const view = new MockedProcessListEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      ProcessListEnvelopeApi,
      ProcessListChannelApi,
      ProcessListEnvelopeViewApi,
      ProcessListEnvelopeContext
    > = {
      envelopeClient,
      envelopeContext: {},
      viewDelegate: () => Promise.resolve(() => view)
    };

    const envelopeApi = new ProcessListEnvelopeApiImpl(args);

    envelopeApi.processList__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        initialState: {
          filters: {
            status: [ProcessInstanceState.Active],
            businessKey: []
          },
          sortBy: {
            lastUpdate: OrderBy.DESC
          }
        },
        singularProcessLabel: 'Workflow',
        pluralProcessLabel: 'Workflows',
        isWorkflow: true
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
