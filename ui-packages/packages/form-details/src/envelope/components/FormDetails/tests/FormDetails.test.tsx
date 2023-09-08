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
import FormDetails from '../FormDetails';
import { MockedFormDetailsDriver } from '../../../tests/mocks/MockedFormDetailsDriver';
import { act } from 'react-dom/test-utils';

jest.mock('../../FormEditor/FormEditor');
jest.mock('../../../containers/FormDisplayerContainer/FormDisplayerContainer');

describe('form details tests', () => {
  const driver = new MockedFormDetailsDriver();

  it('render form details - source', async () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: driver,
      formData: {
        name: 'form1',
        type: 'HTML' as any,
        lastModified: new Date('2020-07-11T18:30:00.000Z')
      },
      targetOrigin: 'http://localhost:9000'
    };
    let wrapper;
    await act(async () => {
      wrapper = mount(<FormDetails {...props} />);
    });
    wrapper = wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });

  it('render form details - config', async () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: driver,
      formData: {
        name: 'form1',
        type: 'html' as any,
        lastModified: new Date('2020-07-11T18:30:00.000Z')
      },
      targetOrigin: 'http://localhost:9000'
    };
    let wrapper;
    await act(async () => {
      wrapper = mount(<FormDetails {...props} />);
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find('TabButton').at(1).find('button').simulate('click');
    });
    await act(async () => {
      wrapper = wrapper.update();
    });
    expect(wrapper).toMatchSnapshot();
  });
});
