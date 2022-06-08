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

import React from 'react';
import { act } from 'react-dom/test-utils';
import { mount } from 'enzyme';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import ProcessDefinitionListEnvelopeView, {
  ProcessDefinitionListEnvelopeViewApi
} from '../ProcessDefinitionListEnvelopeView';
import ProcessDefinitionList from '../components/ProcessDefinitionList/ProcessDefinitionList';

jest.mock('../components/ProcessDefinitionList/ProcessDefinitionList');

describe('ProcessDefinitionListEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<ProcessDefinitionListEnvelopeViewApi>();

    let wrapper = mount(
      <ProcessDefinitionListEnvelopeView
        channelApi={channelApi}
        ref={forwardRef}
      />
    ).find('ProcessDefinitionListEnvelopeView');

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize({ singularProcessLabel: 'Workflow' });
      }
    });

    wrapper = wrapper.update();

    const envelopeView = wrapper.find(ProcessDefinitionListEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const processDefinitionList = envelopeView.find(ProcessDefinitionList);

    expect(processDefinitionList.exists()).toBeTruthy();
    expect(
      processDefinitionList.props().isEnvelopeConnectedToChannel
    ).toBeTruthy();
    expect(processDefinitionList.props().driver).not.toBeNull();
  });
});
