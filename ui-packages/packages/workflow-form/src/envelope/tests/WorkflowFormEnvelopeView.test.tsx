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
import { mount } from 'enzyme';
import {
  MockedMessageBusClientApi,
  workflowForm__getCustomWorkflowSchema,
  workflowSchema
} from './mocks/Mocks';
import WorkflowFormEnvelopeView, {
  WorkflowFormEnvelopeViewApi
} from '../WorkflowFormEnvelopeView';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import CustomWorkflowForm from '../components/CustomWorkflowForm/CustomWorkflowForm';
import WorkflowForm from '../components/WorkflowForm/WorkflowForm';

jest.mock('../components/WorkflowForm/WorkflowForm');
jest.mock('../components/CustomWorkflowForm/CustomWorkflowForm');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/components-common/dist/components/KogitoSpinner', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    KogitoSpinner: () => {
      return <MockedComponent />;
    }
  })
);

describe('WorkflowFormEnvelopeView tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('Loading', () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<WorkflowFormEnvelopeViewApi>();

    const wrapper = mount(
      <WorkflowFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );

    expect(wrapper).toMatchSnapshot();

    const spinner = wrapper.find(KogitoSpinner);
    expect(spinner.exists()).toBeTruthy();
  });

  it('Workflow Form', async () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<WorkflowFormEnvelopeViewApi>();

    workflowForm__getCustomWorkflowSchema.mockReturnValue(
      Promise.resolve(null)
    );

    let wrapper = mount(
      <WorkflowFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );

    await act(async () => {
      if (forwardRef.current) {
        await forwardRef.current.initialize({
          workflowName: 'workflow1',
          endpoint: 'http://localhost:4000'
        });
      }
    });

    wrapper = wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const workflowForm = wrapper.find(WorkflowForm);
    expect(workflowForm.exists()).toBeTruthy();
    expect(workflowForm.props().driver).not.toBeNull();
  });

  it('Custom Workflow Form', async () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<WorkflowFormEnvelopeViewApi>();

    workflowForm__getCustomWorkflowSchema.mockReturnValue(
      Promise.resolve(workflowSchema)
    );

    let wrapper = mount(
      <WorkflowFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );

    await act(async () => {
      if (forwardRef.current) {
        await forwardRef.current.initialize({
          workflowName: 'workflow1',
          endpoint: 'http://localhost:4000'
        });
      }
    });

    wrapper = wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const customWorkflowForm = wrapper.find(CustomWorkflowForm);
    expect(customWorkflowForm.exists()).toBeTruthy();
    expect(customWorkflowForm.props().driver).not.toBeNull();
  });
});
