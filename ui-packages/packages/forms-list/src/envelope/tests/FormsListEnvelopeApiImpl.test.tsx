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
  MockedFormsListEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kogito-tooling/envelope';
import { FormsListChannelApi, FormsListEnvelopeApi } from '../../api';
import { FormsListEnvelopeApiImpl } from '../FormsListEnvelopeApiImpl';
import { FormsListEnvelopeViewApi } from '../FormsListEnvelopeView';
import { FormsListEnvelopeContext } from '../FormsListEnvelopeContext';

describe('FormsListEnvelopeApiImpl tests', () => {
  it('initialize', () => {
    const envelopeBusController = MockedEnvelopeBusController;
    const view = new MockedFormsListEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      FormsListEnvelopeApi,
      FormsListChannelApi,
      FormsListEnvelopeViewApi,
      FormsListEnvelopeContext
    > = {
      envelopeBusController,
      envelopeContext: {},
      view: () => view
    };

    const envelopeApi = new FormsListEnvelopeApiImpl(args);

    envelopeApi.formsList__init({
      envelopeServerId: 'envelopeServerId',
      origin: 'origin'
    });

    expect(envelopeBusController.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
    expect(view.initialize).toHaveBeenCalled();
  });
});
