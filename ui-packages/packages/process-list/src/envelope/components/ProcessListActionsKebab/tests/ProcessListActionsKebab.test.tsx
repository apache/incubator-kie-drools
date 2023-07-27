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
import ProcessListActionsKebab from '../ProcessListActionsKebab';
import { mount } from 'enzyme';
import {
  Dropdown,
  KebabToggle,
  DropdownItem
} from '@patternfly/react-core/dist/js/components/Dropdown';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared/dist/types';

describe('Process list actions kebab tests', () => {
  const props = {
    processInstance: {
      id: '538f9feb-5a14-4096-b791-2055b38da7c6',
      processId: 'travels',
      businessKey: 'Tra234',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels',
      rootProcessInstanceId: null,
      roles: [],
      state: ProcessInstanceState.Error,
      addons: [
        'jobs-management',
        'prometheus-monitoring',
        'process-management'
      ],
      start: new Date('2019-10-22T03:40:44.089Z'),
      error: {
        nodeDefinitionId: '__a1e139d5-4e77-48c9-84ae-34578e9817n',
        message: 'Something went wrong'
      },
      lastUpdate: new Date('2019-10-22T03:40:44.089Z'),
      serviceUrl: 'http://localhost:4000',
      endpoint: 'http://localhost:4000',
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-23T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-23T22:00:00Z[UTC]","city":"New York","country":"US","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"New York","country":"US","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Berlin","country":"Germany","street":"Bakers","zipCode":"100200"},"email":"cristiano@redhat.com","firstName":"Cristiano","lastName":"Nicolai","nationality":"German"}}',
      nodes: [],
      milestones: [],
      isSelected: false,
      childProcessInstances: []
    },
    onSkipClick: jest.fn(),
    onRetryClick: jest.fn(),
    onAbortClick: jest.fn()
  };
  it('Skip click test', () => {
    let wrapper = mount(<ProcessListActionsKebab {...props} />);
    wrapper.find(Dropdown).find(KebabToggle).find('button').simulate('click');
    wrapper = wrapper.update();
    expect(
      wrapper.find(DropdownItem).at(1).find('a').children().contains('Skip')
    ).toBeTruthy();
    wrapper.find(DropdownItem).at(1).simulate('click');
    expect(props.onSkipClick).toHaveBeenCalled();
  });

  it('Retry click test', () => {
    let wrapper = mount(<ProcessListActionsKebab {...props} />);
    wrapper.find(Dropdown).find(KebabToggle).find('button').simulate('click');
    wrapper = wrapper.update();
    expect(
      wrapper.find(DropdownItem).at(0).find('a').children().contains('Retry')
    ).toBeTruthy();
    wrapper.find(DropdownItem).at(0).simulate('click');
    expect(props.onRetryClick).toHaveBeenCalled();
  });

  it('Abort click test', () => {
    let wrapper = mount(<ProcessListActionsKebab {...props} />);
    wrapper.find(Dropdown).find(KebabToggle).find('button').simulate('click');
    wrapper = wrapper.update();
    expect(
      wrapper.find(DropdownItem).at(2).find('a').children().contains('Abort')
    ).toBeTruthy();
    wrapper.find(DropdownItem).at(2).simulate('click');
    expect(props.onAbortClick).toHaveBeenCalled();
  });
  it('Trigger CloudEvent click test', () => {
    const onOpenTriggerCloudEvent = jest.fn();
    let wrapper = mount(
      <ProcessListActionsKebab
        {...props}
        onOpenTriggerCloudEvent={onOpenTriggerCloudEvent}
      />
    );
    wrapper.find(Dropdown).find(KebabToggle).find('button').simulate('click');
    wrapper = wrapper.update();
    expect(
      wrapper
        .find(DropdownItem)
        .at(2)
        .find('a')
        .children()
        .contains('Send Cloud Event')
    ).toBeTruthy();
    wrapper.find(DropdownItem).at(2).simulate('click');
    expect(onOpenTriggerCloudEvent).toHaveBeenCalled();
  });

  it('process instance in active state', () => {
    let wrapper = mount(
      <ProcessListActionsKebab
        {...{
          ...props,
          processInstance: {
            ...props.processInstance,
            state: ProcessInstanceState.Active
          }
        }}
      />
    );
    wrapper.find(Dropdown).find(KebabToggle).find('button').simulate('click');
    wrapper = wrapper.update();
    expect(wrapper.find(DropdownItem).length).toEqual(1);
    expect(
      wrapper.find(DropdownItem).at(0).find('a').children().contains('Abort')
    ).toBeTruthy();
    wrapper.find(DropdownItem).at(0).simulate('click');
    expect(props.onAbortClick).toHaveBeenCalled();
  });
});
