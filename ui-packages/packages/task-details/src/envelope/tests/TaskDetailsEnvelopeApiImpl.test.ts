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

import { TaskDetailsChannelApi, TaskDetailsEnvelopeApi } from '../../api';

import { TaskDetailsEnvelopeViewApi } from '../TaskDetailsEnvelopeView';
import { TaskDetailsEnvelopeContext } from '../TaskDetailsEnvelopeContext';
import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import { TaskDetailsEnvelopeApiImpl } from '../TaskDetailsEnvelopeApiImpl';
import {
  MockedEnvelopeClient,
  MockedTaskDetailsEnvelopeViewApi,
  userTask
} from './utils/Mocks';

describe('TaskDetailsEnvelopeApiImpl tests', () => {
  it('init', async () => {
    const envelopeClient = MockedEnvelopeClient;
    const view = new MockedTaskDetailsEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      TaskDetailsEnvelopeApi,
      TaskDetailsChannelApi,
      TaskDetailsEnvelopeViewApi,
      TaskDetailsEnvelopeContext
    > = {
      envelopeClient,
      envelopeContext: {},
      viewDelegate: () => Promise.resolve(() => view)
    };

    const envelopeApi = new TaskDetailsEnvelopeApiImpl(args);

    envelopeApi.taskDetails__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        task: userTask
      }
    );

    expect(envelopeClient.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
    const setTaskView = await view.setTask;
    expect(setTaskView).toHaveBeenCalledWith(userTask);
  });
});
