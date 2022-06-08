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
import { mount } from 'enzyme';
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
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessDefinitionList {...props} />);
    });
    expect(
      wrapper.find(ProcessDefinitionList).props()[
        'isEnvelopeConnectedToChannel'
      ]
    ).toBeFalsy();
  });

  it('render ProcessDefinition list - table', async () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: driver,
      singularProcessLabel: 'Workflow'
    };
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessDefinitionList {...props} />);
    });
    expect(wrapper).toMatchSnapshot();
  });
});
