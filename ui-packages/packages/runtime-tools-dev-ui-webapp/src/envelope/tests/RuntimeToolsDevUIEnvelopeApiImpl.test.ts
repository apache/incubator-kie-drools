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
  MockedRuntimeToolsDevUIEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kogito-tooling/envelope';
import {
  RuntimeToolsDevUIChannelApi,
  RuntimeToolsDevUIEnvelopeApi
} from '../../api';
import { RuntimeToolsDevUIEnvelopeApiImpl } from '../RuntimeToolsDevUIEnvelopeApiImpl';
import { RuntimeToolsDevUIEnvelopeContextType } from '../RuntimeToolsDevUIEnvelopeContext';
import { RuntimeToolsDevUIEnvelopeViewApi } from '../RuntimeToolsDevUIEnvelopeViewApi';

describe('JobsManagementEnvelopeApiImpl tests', () => {
  it('initialize', () => {
    const envelopeBusController = new MockedEnvelopeBusController();
    const view = new MockedRuntimeToolsDevUIEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      RuntimeToolsDevUIEnvelopeApi,
      RuntimeToolsDevUIChannelApi,
      RuntimeToolsDevUIEnvelopeViewApi,
      RuntimeToolsDevUIEnvelopeContextType
    > = {
      envelopeBusController,
      envelopeContext: {} as any,
      view: () => view
    };

    const envelopeApi = new RuntimeToolsDevUIEnvelopeApiImpl(args);

    envelopeApi.runtimeToolsDevUI_initRequest(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        users: [],
        dataIndexUrl: '',
        page: ''
      }
    );

    expect(envelopeBusController.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
  });
});
