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

import { render } from '@testing-library/react';
import React from 'react';
import { EmbeddedCloudEventForm } from '../EmbeddedCloudEventForm';
import { MockedCloudEventFormDriver } from './utils/Mocks';

jest.mock('../../envelope/components/CloudEventForm/CloudEventForm');

describe('EmbeddedCloudEventForm tests', () => {
  it('Snapshot', () => {
    const props = {
      targetOrigin: 'origin',
      driver: new MockedCloudEventFormDriver()
    };

    const container = render(<EmbeddedCloudEventForm {...props} />).container;

    expect(container).toMatchSnapshot();

    const contentDiv = container.querySelector('div');

    expect(contentDiv).toBeTruthy();
  });

  it('Snapshot - isNewInstanceEvent', () => {
    const props = {
      targetOrigin: 'origin',
      driver: new MockedCloudEventFormDriver(),
      isNewInstanceEvent: true
    };

    const container = render(<EmbeddedCloudEventForm {...props} />).container;

    expect(container).toMatchSnapshot();

    const contentDiv = container.querySelector('div');

    expect(contentDiv).toBeTruthy();
  });

  it('Snapshot - defaultValue', () => {
    const props = {
      targetOrigin: 'origin',
      driver: new MockedCloudEventFormDriver(),
      defaultValues: {
        cloudEventSource: '/local/source',
        instanceId: '1234'
      }
    };

    const container = render(<EmbeddedCloudEventForm {...props} />).container;

    expect(container).toMatchSnapshot();

    const contentDiv = container.querySelector('div');

    expect(contentDiv).toBeTruthy();
  });
});
