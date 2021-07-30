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
  MockedEnvelopeBusController,
  MockedTaskInboxEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kogito-tooling/envelope';
import { TaskInboxChannelApi, TaskInboxEnvelopeApi } from '../../api';
import { TaskInboxEnvelopeApiImpl } from '../TaskInboxEnvelopeApiImpl';
import { TaskInboxEnvelopeViewApi } from '../TaskInboxEnvelopeView';
import { TaskInboxEnvelopeContext } from '../TaskInboxEnvelopeContext';

describe('TaskInboxEnvelopeApiImpl tests', () => {
  it('initialize', () => {
    const envelopeBusController = new MockedEnvelopeBusController();
    const view = new MockedTaskInboxEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      TaskInboxEnvelopeApi,
      TaskInboxChannelApi,
      TaskInboxEnvelopeViewApi,
      TaskInboxEnvelopeContext
    > = {
      envelopeBusController,
      envelopeContext: {},
      view: () => view
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
    envelopeApi.taskInbox__notify('John');

    expect(envelopeBusController.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );

    expect(view.initialize).toHaveBeenCalledWith(
      undefined,
      allTaskStates,
      activeTaskStates
    );
  });
});
