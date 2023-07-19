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
  MockedTaskInboxEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import { TaskInboxChannelApi, TaskInboxEnvelopeApi } from '../../api';
import { TaskInboxEnvelopeApiImpl } from '../TaskInboxEnvelopeApiImpl';
import { TaskInboxEnvelopeViewApi } from '../TaskInboxEnvelopeView';
import { TaskInboxEnvelopeContext } from '../TaskInboxEnvelopeContext';

describe('TaskInboxEnvelopeApiImpl tests', () => {
  it('initialize', async () => {
    const envelopeClient = MockedEnvelopeClient;
    const view = new MockedTaskInboxEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      TaskInboxEnvelopeApi,
      TaskInboxChannelApi,
      TaskInboxEnvelopeViewApi,
      TaskInboxEnvelopeContext
    > = {
      envelopeClient,
      envelopeContext: {},
      viewDelegate: () => Promise.resolve(() => view)
    };

    const envelopeApi = new TaskInboxEnvelopeApiImpl(args);

    const activeTaskStates = ['Ready'];
    const allTaskStates = ['Ready', 'Finished'];
    envelopeApi.taskInbox__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        initialState: undefined,
        activeTaskStates,
        allTaskStates
      }
    );
    expect(envelopeClient.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
    const initializedView = await view.initialize;
    envelopeApi.taskInbox__notify('John');

    expect(initializedView).toHaveBeenCalledWith(
      undefined,
      allTaskStates,
      activeTaskStates
    );
  });
});
