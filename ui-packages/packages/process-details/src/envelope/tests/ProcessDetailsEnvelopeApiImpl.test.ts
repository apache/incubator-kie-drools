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
  MockedProcessDetailsEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kogito-tooling/envelope';
import { ProcessDetailsChannelApi, ProcessDetailsEnvelopeApi } from '../../api';
import { ProcessDetailsEnvelopeApiImpl } from '../ProcessDetailsEnvelopeApiImpl';
import { ProcessDetailsEnvelopeViewApi } from '../ProcessDetailsEnvelopeView';
import { ProcessDetailsEnvelopeContext } from '../ProcessDetailsEnvelopeContext';

describe('ProcessDetailsEnvelopeApiImpl tests', () => {
  it('initialize', () => {
    const envelopeBusController = new MockedEnvelopeBusController();
    const view = new MockedProcessDetailsEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      ProcessDetailsEnvelopeApi,
      ProcessDetailsChannelApi,
      ProcessDetailsEnvelopeViewApi,
      ProcessDetailsEnvelopeContext
    > = {
      envelopeBusController,
      envelopeContext: {},
      view: () => view
    };

    const envelopeApi = new ProcessDetailsEnvelopeApiImpl(args);

    envelopeApi.processDetails__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        processId: 'a1e139d5-4e77-48c9-84ae-34578e904e5a'
      }
    );

    expect(envelopeBusController.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
  });
});
