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
import { MockedMessageBusClientApi } from './mocks/Mocks';
import WorkflowFormEnvelopeView, {
  WorkflowFormEnvelopeViewApi
} from '../WorkflowFormEnvelopeView';
import WorkflowForm from '../components/WorkflowForm/WorkflowForm';

jest.mock('../components/WorkflowForm/WorkflowForm');

describe('WorkflowFormEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<WorkflowFormEnvelopeViewApi>();

    let wrapper = mount(
      <WorkflowFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).find('WorkflowFormEnvelopeView');

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize({
          workflowName: 'workflow1',
          endpoint: 'http://localhost:4000'
        });
      }
    });

    wrapper = wrapper.update();

    expect(wrapper.find(WorkflowFormEnvelopeView)).toMatchSnapshot();

    const workflowForm = wrapper.find(WorkflowForm);

    expect(workflowForm.exists()).toBeTruthy();
    expect(workflowForm.props().driver).not.toBeNull();
  });
});
