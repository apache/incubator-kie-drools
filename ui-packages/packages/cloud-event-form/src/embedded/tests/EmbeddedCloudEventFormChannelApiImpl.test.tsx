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
  CloudEventFormChannelApi,
  CloudEventFormDriver,
  CloudEventMethod,
  CloudEventRequest
} from '../../api';
import { MockedCloudEventFormDriver } from './utils/Mocks';
import { EmbeddedCloudEventFormChannelApiImpl } from '../EmbeddedCloudEventFormChannelApiImpl';

let driver: CloudEventFormDriver;
let api: CloudEventFormChannelApi;

describe('EmbeddedCloudEventFormChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedCloudEventFormDriver();
    api = new EmbeddedCloudEventFormChannelApiImpl(driver);
  });

  it('cloudEventForm__triggerCloudEvent', () => {
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

    api.cloudEventForm__triggerCloudEvent(eventRequest);
    expect(driver.triggerCloudEvent).toHaveBeenCalledWith(eventRequest);
  });
});
