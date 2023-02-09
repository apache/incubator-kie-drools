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

import { mount } from 'enzyme';
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

    const wrapper = mount(<EmbeddedCloudEventForm {...props} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.props().driver).toStrictEqual(props.driver);
    expect(wrapper.props().targetOrigin).toStrictEqual(props.targetOrigin);
    expect(wrapper.props().isNewInstanceEvent).toBeUndefined();
    expect(wrapper.props().defaultValues).toBeUndefined();

    const contentDiv = wrapper.find('div');

    expect(contentDiv.exists()).toBeTruthy();
  });

  it('Snapshot - isNewInstanceEvent', () => {
    const props = {
      targetOrigin: 'origin',
      driver: new MockedCloudEventFormDriver(),
      isNewInstanceEvent: true
    };

    const wrapper = mount(<EmbeddedCloudEventForm {...props} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.props().driver).toStrictEqual(props.driver);
    expect(wrapper.props().targetOrigin).toStrictEqual(props.targetOrigin);
    expect(wrapper.props().isNewInstanceEvent).toStrictEqual(true);
    expect(wrapper.props().defaultValues).toBeUndefined();

    const contentDiv = wrapper.find('div');

    expect(contentDiv.exists()).toBeTruthy();
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

    const wrapper = mount(<EmbeddedCloudEventForm {...props} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.props().driver).toStrictEqual(props.driver);
    expect(wrapper.props().targetOrigin).toStrictEqual(props.targetOrigin);
    expect(wrapper.props().defaultValues).not.toBeNull();

    const contentDiv = wrapper.find('div');

    expect(contentDiv.exists()).toBeTruthy();
  });
});
