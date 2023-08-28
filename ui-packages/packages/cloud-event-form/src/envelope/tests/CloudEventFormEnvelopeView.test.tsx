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

import React from 'react';
import { render } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import CloudEventFormEnvelopeView, {
  CloudEventFormEnvelopeViewApi
} from '../CloudEventFormEnvelopeView';
import { MockedMessageBusClientApi } from './mocks/Mocks';

describe('CloudEventFormEnvelopeView tests', () => {
  beforeAll(() => {
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: jest.fn().mockImplementation((query) => ({
        matches: false,
        media: query,
        onchange: null,
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn()
      }))
    });
  });
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<CloudEventFormEnvelopeViewApi>();

    const container = render(
      <CloudEventFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).container;

    expect(container).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize({
          isNewInstanceEvent: true,
          defaultValues: {
            cloudEventSource: '/local/test',
            instanceId: '1234'
          }
        });
      }
    });

    const checkWorkflowForm = container.querySelector(
      '[data-ouia-component-type="workflow-form"]'
    );
    expect(checkWorkflowForm).toBeTruthy();
  });
});
