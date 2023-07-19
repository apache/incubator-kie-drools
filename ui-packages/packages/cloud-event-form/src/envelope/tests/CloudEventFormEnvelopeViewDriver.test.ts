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
  MessageBusClientApi,
  RequestPropertyNames
} from '@kie-tools-core/envelope-bus/dist/api';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import {
  CloudEventFormChannelApi,
  CloudEventMethod,
  CloudEventRequest
} from '../../api';
import { CloudEventFormEnvelopeViewDriver } from '../CloudEventFormEnvelopeViewDriver';

let channelApi: MessageBusClientApi<CloudEventFormChannelApi>;
let requests: Pick<
  CloudEventFormChannelApi,
  RequestPropertyNames<CloudEventFormChannelApi>
>;
let driver: CloudEventFormEnvelopeViewDriver;

describe('CloudEventFormEnvelopeViewDriver tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    channelApi = new MockedMessageBusClientApi();
    requests = channelApi.requests;
    driver = new CloudEventFormEnvelopeViewDriver(channelApi);
  });

  describe('Requests', () => {
    it('trigger cloud event', () => {
      const eventRequest: CloudEventRequest = {
        method: CloudEventMethod.POST,
        endpoint: '/',
        headers: {
          type: 'test',
          source: 'test',
          extensions: {}
        },
        data: ''
      };
      driver.triggerCloudEvent(eventRequest);

      expect(requests.cloudEventForm__triggerCloudEvent).toHaveBeenCalledWith(
        eventRequest
      );
    });
  });
});
