/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { render } from '@testing-library/react';
import ProcessDefinitionList from '../ProcessDefinitionList';
import { MockedProcessDefinitionListDriver } from '../../../tests/mocks/MockedProcessDefinitionListDriver';
import { act } from 'react-dom/test-utils';

describe('ProcessDefinition list tests', () => {
  Date.now = jest.fn(() => 1487076708000);
  const driver = new MockedProcessDefinitionListDriver();

  it('envelope not connected to channel', async () => {
    const props = {
      isEnvelopeConnectedToChannel: false,
      driver: null,
      singularProcessLabel: 'Workflow'
    };
    let container;
    await act(async () => {
      container = render(<ProcessDefinitionList {...props} />).container;
    });

    const checkIsEnvelopeConnectedToChannel = container.querySelector('h3');
    expect(checkIsEnvelopeConnectedToChannel.textContent).toEqual(
      'Loading workflow definitions...'
    );
  });

  it('render ProcessDefinition list - table', async () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: driver,
      singularProcessLabel: 'Workflow'
    };
    let container;
    await act(async () => {
      container = render(<ProcessDefinitionList {...props} />).container;
    });
    expect(container).toMatchSnapshot();

    const checkToolbar = container.querySelector(
      '[class="pf-c-toolbar__content"]'
    );
    expect(checkToolbar).toBeTruthy();

    const checkToolbarButton = container.querySelector('[type="button"]');
    expect(checkToolbarButton).toBeTruthy();
  });

  it('render ProcessDefinition list - table with cloud event enabled', async () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: driver,
      singularProcessLabel: 'Workflow',
      isTriggerCloudEventEnabled: true
    };
    let container;
    await act(async () => {
      container = render(<ProcessDefinitionList {...props} />).container;
    });
    expect(container).toMatchSnapshot();

    const checkToolbar = container.querySelector(
      '[class="pf-c-toolbar__content"]'
    );
    expect(checkToolbar).toBeTruthy();

    const checkToolbarButton = container.querySelector('[type="button"]');
    expect(checkToolbarButton).toBeTruthy();
  });
});
