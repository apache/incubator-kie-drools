/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  MockedCloudEventFormEnvelopeViewApi,
  MockedEnvelopeBusControllerDefinition
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kogito-tooling/envelope';
import { EnvelopeBusController } from '@kogito-tooling/envelope-bus/dist/envelope';
import { CloudEventFormChannelApi, CloudEventFormEnvelopeApi } from '../../api';
import { CloudEventFormEnvelopeViewApi } from '../CloudEventFormEnvelopeView';
import { CloudEventFormEnvelopeApiImpl } from '../CloudEventFormEnvelopeApiImpl';

describe('CloudEventFormEnvelopeApiImpl tests', () => {
  it('initialize', () => {
    const envelopeBusController = new MockedEnvelopeBusControllerDefinition();
    const view = new MockedCloudEventFormEnvelopeViewApi();

    const args: EnvelopeApiFactoryArgs<
      CloudEventFormEnvelopeApi,
      CloudEventFormChannelApi,
      CloudEventFormEnvelopeViewApi,
      undefined
    > = {
      envelopeBusController: envelopeBusController as EnvelopeBusController<
        CloudEventFormEnvelopeApi,
        CloudEventFormChannelApi
      >,
      envelopeContext: undefined,
      view: () => view
    };

    const envelopeApi = new CloudEventFormEnvelopeApiImpl(args);

    envelopeApi.cloudEventForm__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        isNewInstanceEvent: true,
        defaultValues: {
          cloudEventSource: '/local/test',
          instanceId: '1234'
        }
      }
    );

    expect(envelopeBusController.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );

    expect(view.initialize).toHaveBeenCalled();
  });
});
