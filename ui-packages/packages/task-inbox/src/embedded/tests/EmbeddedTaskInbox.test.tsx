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
import { EmbeddedTaskInbox } from '../EmbeddedTaskInbox';
import { MockedTaskInboxDriver } from './utils/Mocks';
import { mount } from 'enzyme';

describe('EmbeddedTaskInbox tests', () => {
  it('Snapshot', () => {
    const props = {
      targetOrigin: 'origin',
      envelopePath: 'path',
      driver: new MockedTaskInboxDriver(),
      allTaskStates: ['Ready', 'Completed'],
      activeTaskStates: ['Ready']
    };

    const wrapper = mount(<EmbeddedTaskInbox {...props} />);

    expect(wrapper).toMatchSnapshot();

    expect(wrapper.props().allTaskStates).toStrictEqual(props.allTaskStates);
    expect(wrapper.props().activeTaskStates).toStrictEqual(
      props.activeTaskStates
    );
    expect(wrapper.props().driver).toStrictEqual(props.driver);
    expect(wrapper.props().targetOrigin).toStrictEqual(props.targetOrigin);
    expect(wrapper.props().envelopePath).toStrictEqual(props.envelopePath);

    const iframe = wrapper.find('iframe');

    expect(iframe.exists()).toBeTruthy();
    expect(iframe.props().src).toBe(props.envelopePath);
  });
});
