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
import ErrorPopover from '../ErrorPopover';
import { mount } from 'enzyme';
import { Popover } from '@patternfly/react-core';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared';
const props = {
  processInstanceData: {
    id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    processId: 'travels',
    businessKey: 'T1234',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Error,
    rootProcessInstanceId: null,
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    start: new Date('2019-10-22T03:40:44.089Z'),
    end: new Date('2019-10-22T03:40:44.089Z'),
    lastUpdate: new Date('2019-10-22T03:40:44.089Z'),
    error: {
      nodeDefinitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8',
      message: 'Something went wrong'
    },
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [],
    childProcessInstances: []
  },
  onSkipClick: jest.fn(),
  onRetryClick: jest.fn()
};

describe('Errorpopover component tests', () => {
  it('snapshot testing with error object', () => {
    const wrapper = mount(<ErrorPopover {...props} />).find('ErrorPopover');
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing without error object', () => {
    const wrapper = mount(
      <ErrorPopover
        {...{
          ...props,
          processInstanceData: { ...props.processInstanceData, error: null }
        }}
      />
    ).find('ErrorPopover');
    expect(wrapper).toMatchSnapshot();
  });

  it('Skip call success', () => {
    let wrapper = mount(<ErrorPopover {...props} />);
    wrapper.find(Popover).props()['footerContent'][0]['props']['onClick']();
    wrapper = wrapper.update();
    expect(props.onSkipClick).toHaveBeenCalled();
  });

  it('Retry call success', () => {
    let wrapper = mount(<ErrorPopover {...props} />);
    wrapper.find(Popover).props()['footerContent'][1]['props']['onClick']();
    wrapper = wrapper.update();
    expect(props.onRetryClick).toHaveBeenCalled();
  });
});
