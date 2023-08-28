/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import React from 'react';
import { act } from 'react-dom/test-utils';
import { render } from '@testing-library/react';
import {
  MockedMessageBusClientApi,
  workflowForm__getCustomWorkflowSchema,
  workflowSchema
} from './mocks/Mocks';
import WorkflowFormEnvelopeView, {
  WorkflowFormEnvelopeViewApi
} from '../WorkflowFormEnvelopeView';

describe('WorkflowFormEnvelopeView tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: jest.fn().mockImplementation((query) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(), // Deprecated
        removeListener: jest.fn(), // Deprecated
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn()
      }))
    });
  });

  it('Loading', () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<WorkflowFormEnvelopeViewApi>();

    const container = render(
      <WorkflowFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).container;

    expect(container).toMatchSnapshot();
    const checkLoading = container.querySelector('h3');
    expect(checkLoading?.textContent).toEqual('Loading workflow form...');
  });

  it('Workflow Form', async () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<WorkflowFormEnvelopeViewApi>();

    workflowForm__getCustomWorkflowSchema.mockReturnValue(
      Promise.resolve(null)
    );

    const container = render(
      <WorkflowFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).container;

    await act(async () => {
      if (forwardRef.current) {
        await forwardRef.current.initialize({
          workflowName: 'workflow1',
          endpoint: 'http://localhost:4000'
        });
      }
    });

    expect(container).toMatchSnapshot();
    const checkWorkflowForm = container.querySelector(
      '[data-ouia-component-type="workflow-form"]'
    );
    expect(checkWorkflowForm).toBeTruthy();
  });

  it('Custom Workflow Form', async () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<WorkflowFormEnvelopeViewApi>();

    workflowForm__getCustomWorkflowSchema.mockReturnValue(
      Promise.resolve(workflowSchema)
    );

    const container = render(
      <WorkflowFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).container;

    await act(async () => {
      if (forwardRef.current) {
        await forwardRef.current.initialize({
          workflowName: 'workflow1',
          endpoint: 'http://localhost:4000'
        });
      }
    });

    expect(container).toMatchSnapshot();
    const checkCustomWorkflowForm = container.querySelector(
      '[data-ouia-component-type="custom-workflow-form"]'
    );
    expect(checkCustomWorkflowForm).toBeTruthy();
  });
});
